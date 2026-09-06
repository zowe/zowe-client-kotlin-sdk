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
import io.kotest.matchers.string.shouldNotContain
import io.kotest.provided.ProjectConfig.mockHttpConnection
import io.kotest.provided.ProjectConfig.zosmfMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.files.api.FilesAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.definitions.ZosmfFileMode
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfChangeFileModeRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfChangeFileModeRequestBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfChangeFileModeResponse

class ZosmfChangeFileModeTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, FilesAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("changeFileMode") {
    should("changeFileMode change the mode of a USS file") {
      val filePath = "u/CFMTEST1/test.txt"
      var recordedRequest: RecordedRequest? = null
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileMode_success_file",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedRequest = it
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(200)
        }
      )

      val changeFileModeRequest = ZosmfChangeFileModeRequest(
        mockHttpConnection,
        filePath,
        ZosmfChangeFileModeRequestBody(ZosmfFileMode.fromString("rw-r-----"))
      )
      val changeFileModeResponse = filesApi.changeFileMode(changeFileModeRequest)
        as? ZosmfChangeFileModeResponse
        ?: fail("Should be instance of ${ZosmfChangeFileModeResponse::class.java.name}")

      assertSoftly {
        changeFileModeResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.method shouldBe "PUT"
        recordedRequest?.getHeader("Content-Type") shouldContain "application/json"
        recordedBody shouldContain "\"mode\": \"rw-r-----\""
        recordedBody shouldContain "\"request\": \"chmod\""
        recordedBody shouldNotContain "\"recursive\""
        recordedBody shouldNotContain "\"links\""
      }
    }

    should("changeFileMode change the mode of a USS directory recursively") {
      val dirPath = "u/CFMTEST2/testdir"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileMode_success_recursive",
        { it.requestLine.contains("/zosmf/restfiles/fs/$dirPath") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(200)
        }
      )

      val changeFileModeRequest = ZosmfChangeFileModeRequest(
        mockHttpConnection,
        dirPath,
        ZosmfChangeFileModeRequestBody(
          ZosmfFileMode.fromString("rwxr-x---"),
          recursive = true
        )
      )
      val changeFileModeResponse = filesApi.changeFileMode(changeFileModeRequest)
        as? ZosmfChangeFileModeResponse
        ?: fail("Should be instance of ${ZosmfChangeFileModeResponse::class.java.name}")

      assertSoftly {
        changeFileModeResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"mode\": \"rwxr-x---\""
        recordedBody shouldContain "\"recursive\": true"
        recordedBody shouldContain "\"request\": \"chmod\""
      }
    }

    should("changeFileMode change the mode with the links option provided") {
      val filePath = "u/CFMTEST3/testlink"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileMode_success_links",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(200)
        }
      )

      val changeFileModeRequest = ZosmfChangeFileModeRequest(
        mockHttpConnection,
        filePath,
        ZosmfChangeFileModeRequestBody(
          ZosmfFileMode.fromString("rwxrwxrwx"),
          links = ZosmfChangeFileModeRequestBody.Links.SUPPRESS
        )
      )
      val changeFileModeResponse = filesApi.changeFileMode(changeFileModeRequest)
        as? ZosmfChangeFileModeResponse
        ?: fail("Should be instance of ${ZosmfChangeFileModeResponse::class.java.name}")

      assertSoftly {
        changeFileModeResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"links\": \"suppress\""
      }
    }

    should("changeFileMode build the correct URL for an absolute path with the characters to encode") {
      val filePath = "/u/CFMTEST4/test dir"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileMode_success_path_encoding",
        { it.requestLine.contains("/zosmf/restfiles/fs/u/CFMTEST4/") },
        {
          recordedRequest = it
          MockResponse().setResponseCode(200)
        }
      )

      val changeFileModeRequest = ZosmfChangeFileModeRequest(
        mockHttpConnection,
        filePath,
        ZosmfChangeFileModeRequestBody(ZosmfFileMode.fromString("rw-r--r--"))
      )
      val changeFileModeResponse = filesApi.changeFileMode(changeFileModeRequest)
        as? ZosmfChangeFileModeResponse
        ?: fail("Should be instance of ${ZosmfChangeFileModeResponse::class.java.name}")

      assertSoftly {
        changeFileModeResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.requestLine shouldContain "/zosmf/restfiles/fs/u/CFMTEST4/test%20dir"
        recordedRequest?.requestLine?.contains("fs//") shouldBe false
      }
    }

    should("changeFileMode fail because the item to change the mode of is not found") {
      val filePath = "u/CFMTEST5/not_found.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileMode_fail_not_found",
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

      val changeFileModeRequest = ZosmfChangeFileModeRequest(
        mockHttpConnection,
        filePath,
        ZosmfChangeFileModeRequestBody(ZosmfFileMode.fromString("rw-r--r--"))
      )
      val changeFileModeResponse = filesApi.changeFileMode(changeFileModeRequest)
        as? ZosmfChangeFileModeResponse
        ?: fail("Should be instance of ${ZosmfChangeFileModeResponse::class.java.name}")

      assertSoftly {
        changeFileModeResponse.status.type shouldBe StatusType.ERROR
        changeFileModeResponse.status.text shouldContain "404"
        changeFileModeResponse.status.text shouldContain "Path name not found"
      }
    }

    should("changeFileMode fail because there are no permissions to change the mode of the item") {
      val filePath = "u/CFMTEST6/forbidden.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileMode_fail_no_permissions",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":8," +
                "\"rc\":8," +
                "\"reason\":1," +
                "\"message\":\"Operation not permitted\"," +
                "\"details\":[\"EDC5139I Operation not permitted. (errno2=0x0C0F8106)\"]" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
            .setResponseCode(403)
        }
      )

      val changeFileModeRequest = ZosmfChangeFileModeRequest(
        mockHttpConnection,
        filePath,
        ZosmfChangeFileModeRequestBody(ZosmfFileMode.fromString("rwxrwxrwx"))
      )
      val changeFileModeResponse = filesApi.changeFileMode(changeFileModeRequest)
        as? ZosmfChangeFileModeResponse
        ?: fail("Should be instance of ${ZosmfChangeFileModeResponse::class.java.name}")

      assertSoftly {
        changeFileModeResponse.status.type shouldBe StatusType.ERROR
        changeFileModeResponse.status.text shouldContain "403"
        changeFileModeResponse.status.text shouldContain "EDC5139I Operation not permitted"
      }
    }
  }
})
