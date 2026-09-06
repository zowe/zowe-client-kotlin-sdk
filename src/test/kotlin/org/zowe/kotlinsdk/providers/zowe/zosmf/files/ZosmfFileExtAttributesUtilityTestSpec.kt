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
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfFileExtAttributesUtilityRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfFileExtAttributesUtilityRequestBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfFileExtAttributesUtilityResponse

class ZosmfFileExtAttributesUtilityTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, FilesAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("fileExtAttributesUtility") {
    should("fileExtAttributesUtility set the extended attributes of a USS file") {
      val filePath = "u/FEATEST1/test.txt"
      var recordedRequest: RecordedRequest? = null
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "fileExtAttributesUtility_success_set",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedRequest = it
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(200)
        }
      )

      val extAttributesRequest = ZosmfFileExtAttributesUtilityRequest(
        mockHttpConnection,
        filePath,
        ZosmfFileExtAttributesUtilityRequestBody(set = "ap")
      )
      val extAttributesResponse = filesApi.fileExtAttributesUtility(extAttributesRequest)
        as? ZosmfFileExtAttributesUtilityResponse
        ?: fail("Should be instance of ${ZosmfFileExtAttributesUtilityResponse::class.java.name}")

      assertSoftly {
        extAttributesResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.method shouldBe "PUT"
        recordedRequest?.getHeader("Content-Type") shouldContain "application/json"
        recordedBody shouldContain "\"set\": \"ap\""
        recordedBody shouldContain "\"request\": \"extattr\""
        recordedBody shouldNotContain "\"reset\""
        extAttributesResponse.extendedAttributes shouldBe null
      }
    }

    should("fileExtAttributesUtility reset the extended attributes of a USS file") {
      val filePath = "u/FEATEST2/test.txt"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "fileExtAttributesUtility_success_reset",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(200)
        }
      )

      val extAttributesRequest = ZosmfFileExtAttributesUtilityRequest(
        mockHttpConnection,
        filePath,
        ZosmfFileExtAttributesUtilityRequestBody(reset = "ls")
      )
      val extAttributesResponse = filesApi.fileExtAttributesUtility(extAttributesRequest)
        as? ZosmfFileExtAttributesUtilityResponse
        ?: fail("Should be instance of ${ZosmfFileExtAttributesUtilityResponse::class.java.name}")

      assertSoftly {
        extAttributesResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"reset\": \"ls\""
        recordedBody shouldNotContain "\"set\""
      }
    }

    should("fileExtAttributesUtility display the extended attributes when neither of the options is provided") {
      val filePath = "u/FEATEST3/test.txt"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "fileExtAttributesUtility_success_display",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse()
            .setBody(
              "{\"stdout\":[" +
                "\"/$filePath\"," +
                "\"    APF authorized = NO\"," +
                "\"    Program controlled = YES\"" +
              "]}"
            )
            .addHeader("Content-Type", "application/json")
            .setResponseCode(200)
        }
      )

      val extAttributesRequest = ZosmfFileExtAttributesUtilityRequest(
        mockHttpConnection,
        filePath,
        ZosmfFileExtAttributesUtilityRequestBody()
      )
      val extAttributesResponse = filesApi.fileExtAttributesUtility(extAttributesRequest)
        as? ZosmfFileExtAttributesUtilityResponse
        ?: fail("Should be instance of ${ZosmfFileExtAttributesUtilityResponse::class.java.name}")

      assertSoftly {
        extAttributesResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"request\": \"extattr\""
        extAttributesResponse.extendedAttributes shouldBe listOf(
          "/$filePath",
          "    APF authorized = NO",
          "    Program controlled = YES"
        )
      }
    }

    should("fileExtAttributesUtility build the correct URL for an absolute path with the characters to encode") {
      val filePath = "/u/FEATEST4/test file"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "fileExtAttributesUtility_success_path_encoding",
        { it.requestLine.contains("/zosmf/restfiles/fs/u/FEATEST4/") },
        {
          recordedRequest = it
          MockResponse().setResponseCode(200)
        }
      )

      val extAttributesRequest = ZosmfFileExtAttributesUtilityRequest(
        mockHttpConnection,
        filePath,
        ZosmfFileExtAttributesUtilityRequestBody(set = "a")
      )
      val extAttributesResponse = filesApi.fileExtAttributesUtility(extAttributesRequest)
        as? ZosmfFileExtAttributesUtilityResponse
        ?: fail("Should be instance of ${ZosmfFileExtAttributesUtilityResponse::class.java.name}")

      assertSoftly {
        extAttributesResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.requestLine shouldContain "/zosmf/restfiles/fs/u/FEATEST4/test%20file"
        recordedRequest?.requestLine?.contains("fs//") shouldBe false
      }
    }

    should("fileExtAttributesUtility fail because the file to operate is not found") {
      val filePath = "u/FEATEST5/not_found.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "fileExtAttributesUtility_fail_not_found",
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

      val extAttributesRequest = ZosmfFileExtAttributesUtilityRequest(
        mockHttpConnection,
        filePath,
        ZosmfFileExtAttributesUtilityRequestBody(set = "a")
      )
      val extAttributesResponse = filesApi.fileExtAttributesUtility(extAttributesRequest)
        as? ZosmfFileExtAttributesUtilityResponse
        ?: fail("Should be instance of ${ZosmfFileExtAttributesUtilityResponse::class.java.name}")

      assertSoftly {
        extAttributesResponse.status.type shouldBe StatusType.ERROR
        extAttributesResponse.status.text shouldContain "404"
        extAttributesResponse.status.text shouldContain "Path name not found"
        extAttributesResponse.extendedAttributes shouldBe null
      }
    }

    should("fileExtAttributesUtility fail because there are no permissions to set the attribute") {
      val filePath = "u/FEATEST6/forbidden.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "fileExtAttributesUtility_fail_no_permissions",
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

      val extAttributesRequest = ZosmfFileExtAttributesUtilityRequest(
        mockHttpConnection,
        filePath,
        ZosmfFileExtAttributesUtilityRequestBody(set = "a")
      )
      val extAttributesResponse = filesApi.fileExtAttributesUtility(extAttributesRequest)
        as? ZosmfFileExtAttributesUtilityResponse
        ?: fail("Should be instance of ${ZosmfFileExtAttributesUtilityResponse::class.java.name}")

      assertSoftly {
        extAttributesResponse.status.type shouldBe StatusType.ERROR
        extAttributesResponse.status.text shouldContain "403"
        extAttributesResponse.status.text shouldContain "EDC5139I Operation not permitted"
      }
    }
  }
})
