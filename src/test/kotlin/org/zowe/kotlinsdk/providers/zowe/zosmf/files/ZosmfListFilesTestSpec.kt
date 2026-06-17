/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.files

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.http.encodeURLParameter
import okhttp3.mockwebserver.MockResponse
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.files.api.FilesAPI
import org.zowe.kotlinsdk.core.files.api.messaging.ListFilesRequest
import org.zowe.kotlinsdk.core.files.data.FileItem
import io.kotest.provided.ProjectConfig
import io.kotest.provided.ProjectConfig.zosmfMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.definitions.ZosmfSymlinkMode
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfListFilesRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfListFilesResponse

class ZosmfListFilesTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, FilesAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("listFiles") {
    should("listFiles return the correct list of files") {
      val filesFilter = "/test"

      zosmfMockResponseDispatcher.injectResolver(
        "mock:listFiles_success",
        { it.requestLine.contains("/zosmf/restfiles/fs?path=${filesFilter.encodeURLParameter()}") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"items\":[" +
                  "{" +
                    "\"name\":\".\"," +
                    "\"mode\":\"drwxrwxrwx\"," +
                    "\"size\":8192," +
                    "\"uid\":0," +
                    "\"user\":\"OMVSKERN\"," +
                    "\"gid\":0," +
                    "\"group\":\"SYS1\"," +
                    "\"mtime\":\"2025-09-25T05:09:26\"" +
                  "}," +
                  "{" +
                    "\"name\":\"..\"," +
                    "\"mode\":\"drwxr-xr-x\"," +
                    "\"size\":16384," +
                    "\"uid\":0," +
                    "\"user\":\"OMVSKERN\"," +
                    "\"gid\":0," +
                    "\"group\":\"SYS1\"," +
                    "\"mtime\":\"2025-09-10T15:44:44\"" +
                  "}," +
                  "{" +
                    "\"name\":\"test_folder\"," +
                    "\"mode\":\"drwxrwxrwx\"," +
                    "\"size\":8192," +
                    "\"uid\":990001," +
                    "\"user\":\"OTHER_USER\"," +
                    "\"gid\":0," +
                    "\"group\":\"SYS1\"," +
                    "\"mtime\":\"2024-08-27T08:55:28\"" +
                  "}," +
                  "{" +
                    "\"name\":\"test.txt\"," +
                    "\"mode\":\"-rwxrw-rw-\"," +
                    "\"size\":0," +
                    "\"uid\":990007," +
                    "\"user\":\"TEST\"," +
                    "\"gid\":0," +
                    "\"group\":\"SYS1\"," +
                    "\"mtime\":\"2025-09-25T05:09:26\"" +
                  "}" +
                "]," +
                "\"returnedRows\":4," +
                "\"totalRows\":4," +
                "\"JSONversion\":1" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
        }
      )

      val listFilesRequest: ListFilesRequest = ZosmfListFilesRequest(
        ProjectConfig.mockHttpConnection,
        filter = filesFilter,
        depth = 0,
        followSymlinks = ZosmfSymlinkMode.REPORT
      )
      val listFilesResponse = filesApi.listFiles(listFilesRequest) as? ZosmfListFilesResponse
        ?: fail("Should be instance of ${ZosmfListFilesResponse::class.java.name}")
      assertSoftly {
        listFilesResponse.status.type shouldBe StatusType.SUCCESS
        listFilesResponse.items.size shouldBe 4
        listFilesResponse.items[0].name shouldBe "."
        listFilesResponse.items[1].name shouldBe ".."
        listFilesResponse.items[2].user shouldBe "OTHER_USER"
        listFilesResponse.items[3].fileType shouldBe FileItem.FileType.FILE
      }
    }

    should("listFiles return a 404 HTTP error when the path is not found") {
      val filesFilter = "/fail_test"

      zosmfMockResponseDispatcher.injectResolver(
        "mock:listFiles_error_not_found",
        { it.requestLine.contains("/zosmf/restfiles/fs?path=${filesFilter.encodeURLParameter()}") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":1," +
                "\"rc\":4," +
                "\"reason\":8," +
                "\"message\":\"Path name not found\"," +
                "\"details\":[\"EDC5129I No such file or directory. (errno2=0x053B006C)\"]" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
            .setResponseCode(404)
        }
      )

      val listFilesRequest = ZosmfListFilesRequest(
        ProjectConfig.mockHttpConnection,
        filter = filesFilter,
        depth = 0,
        followSymlinks = ZosmfSymlinkMode.REPORT
      )
      val listFilesResponse = filesApi.listFiles(listFilesRequest) as? ZosmfListFilesResponse
        ?: fail("Should be instance of ${ZosmfListFilesResponse::class.java.name}")
      println(listFilesResponse)
      assertSoftly {
        listFilesResponse.status.type shouldBe StatusType.ERROR
        listFilesResponse.status.text shouldContain "404"
        listFilesResponse.status.text shouldContain "Category: 1"
        listFilesResponse.status.text shouldContain "RC: 4"
        listFilesResponse.status.text shouldContain "Reason: 8"
      }
    }
  }
})