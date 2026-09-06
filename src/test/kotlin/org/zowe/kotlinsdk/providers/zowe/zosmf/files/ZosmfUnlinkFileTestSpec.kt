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
import io.kotest.provided.ProjectConfig.mockHttpConnection
import io.kotest.provided.ProjectConfig.zosmfMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.files.api.FilesAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfUnlinkFileRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfUnlinkFileRequestBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfUnlinkFileResponse

class ZosmfUnlinkFileTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, FilesAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("unlinkFile") {
    should("unlinkFile remove a link to a USS file") {
      val filePath = "u/ULNKTST1/link.txt"
      var recordedRequest: RecordedRequest? = null
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "unlinkFile_success",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedRequest = it
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(204)
        }
      )

      val unlinkFileRequest = ZosmfUnlinkFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfUnlinkFileRequestBody()
      )
      val unlinkFileResponse = filesApi.unlinkFile(unlinkFileRequest)
        as? ZosmfUnlinkFileResponse
        ?: fail("Should be instance of ${ZosmfUnlinkFileResponse::class.java.name}")

      assertSoftly {
        unlinkFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.method shouldBe "PUT"
        recordedRequest?.getHeader("Content-Type") shouldContain "application/json"
        recordedBody shouldContain "\"request\": \"unlink\""
      }
    }

    should("unlinkFile build the correct URL for an absolute path with the characters to encode") {
      val filePath = "/u/ULNKTST2/test link"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "unlinkFile_success_path_encoding",
        { it.requestLine.contains("/zosmf/restfiles/fs/u/ULNKTST2/") },
        {
          recordedRequest = it
          MockResponse().setResponseCode(204)
        }
      )

      val unlinkFileRequest = ZosmfUnlinkFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfUnlinkFileRequestBody()
      )
      val unlinkFileResponse = filesApi.unlinkFile(unlinkFileRequest)
        as? ZosmfUnlinkFileResponse
        ?: fail("Should be instance of ${ZosmfUnlinkFileResponse::class.java.name}")

      assertSoftly {
        unlinkFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.requestLine shouldContain "/zosmf/restfiles/fs/u/ULNKTST2/test%20link"
        recordedRequest?.requestLine?.contains("fs//") shouldBe false
      }
    }

    should("unlinkFile fail because the link to remove is not found") {
      val filePath = "u/ULNKTST3/not_found.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "unlinkFile_fail_not_found",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
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

      val unlinkFileRequest = ZosmfUnlinkFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfUnlinkFileRequestBody()
      )
      val unlinkFileResponse = filesApi.unlinkFile(unlinkFileRequest)
        as? ZosmfUnlinkFileResponse
        ?: fail("Should be instance of ${ZosmfUnlinkFileResponse::class.java.name}")

      assertSoftly {
        unlinkFileResponse.status.type shouldBe StatusType.ERROR
        unlinkFileResponse.status.text shouldContain "404"
        unlinkFileResponse.status.text shouldContain "Path name not found"
      }
    }

    should("unlinkFile fail because the path to unlink is a directory") {
      val filePath = "u/ULNKTST4/testdir"

      zosmfMockResponseDispatcher.injectResolver(
        "unlinkFile_fail_directory",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":8," +
                "\"rc\":8," +
                "\"reason\":1," +
                "\"message\":\"Is a directory\"," +
                "\"details\":[\"EDC5123I Is a directory. (errno2=0x05620062)\"]" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
            .setResponseCode(500)
        }
      )

      val unlinkFileRequest = ZosmfUnlinkFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfUnlinkFileRequestBody()
      )
      val unlinkFileResponse = filesApi.unlinkFile(unlinkFileRequest)
        as? ZosmfUnlinkFileResponse
        ?: fail("Should be instance of ${ZosmfUnlinkFileResponse::class.java.name}")

      assertSoftly {
        unlinkFileResponse.status.type shouldBe StatusType.ERROR
        unlinkFileResponse.status.text shouldContain "500"
        unlinkFileResponse.status.text shouldContain "EDC5123I Is a directory"
      }
    }

    should("unlinkFile fail because there are no permissions to remove the link") {
      val filePath = "u/ULNKTST5/forbidden.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "unlinkFile_fail_no_permissions",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":8," +
                "\"rc\":8," +
                "\"reason\":1," +
                "\"message\":\"Permission denied\"," +
                "\"details\":[\"EDC5111I Permission denied. (errno2=0x0C0F8106)\"]" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
            .setResponseCode(403)
        }
      )

      val unlinkFileRequest = ZosmfUnlinkFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfUnlinkFileRequestBody()
      )
      val unlinkFileResponse = filesApi.unlinkFile(unlinkFileRequest)
        as? ZosmfUnlinkFileResponse
        ?: fail("Should be instance of ${ZosmfUnlinkFileResponse::class.java.name}")

      assertSoftly {
        unlinkFileResponse.status.type shouldBe StatusType.ERROR
        unlinkFileResponse.status.text shouldContain "403"
        unlinkFileResponse.status.text shouldContain "EDC5111I Permission denied"
      }
    }
  }
})
