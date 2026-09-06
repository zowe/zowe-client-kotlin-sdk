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
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfLinkFileRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfLinkFileRequestBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfLinkFileResponse

class ZosmfLinkFileTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, FilesAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("linkFile") {
    should("linkFile create a symbolic link to a USS file") {
      val filePath = "u/LNKTEST1/link.txt"
      val sourcePath = "/u/LNKTEST1/source.txt"
      var recordedRequest: RecordedRequest? = null
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "linkFile_success_symbolic",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedRequest = it
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(201)
        }
      )

      val linkFileRequest = ZosmfLinkFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfLinkFileRequestBody(from = sourcePath, type = "SYM")
      )
      val linkFileResponse = filesApi.linkFile(linkFileRequest)
        as? ZosmfLinkFileResponse
        ?: fail("Should be instance of ${ZosmfLinkFileResponse::class.java.name}")

      assertSoftly {
        linkFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.method shouldBe "PUT"
        recordedRequest?.getHeader("Content-Type") shouldContain "application/json"
        recordedBody shouldContain "\"request\": \"link\""
        recordedBody shouldContain "\"from\": \"$sourcePath\""
        recordedBody shouldContain "\"type\": \"SYM\""
        recordedBody shouldNotContain "\"recursive\""
        recordedBody shouldNotContain "\"force\""
      }
    }

    should("linkFile link a USS directory recursively, replacing the existing path") {
      val filePath = "u/LNKTEST2/link"
      val sourcePath = "/u/LNKTEST2/source"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "linkFile_success_recursive_force",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(201)
        }
      )

      val linkFileRequest = ZosmfLinkFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfLinkFileRequestBody(from = sourcePath, type = "SYM", recursive = true, force = true)
      )
      val linkFileResponse = filesApi.linkFile(linkFileRequest)
        as? ZosmfLinkFileResponse
        ?: fail("Should be instance of ${ZosmfLinkFileResponse::class.java.name}")

      assertSoftly {
        linkFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"recursive\": true"
        recordedBody shouldContain "\"force\": true"
      }
    }

    should("linkFile build the correct URL for an absolute path with the characters to encode") {
      val filePath = "/u/LNKTEST3/test link"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "linkFile_success_path_encoding",
        { it.requestLine.contains("/zosmf/restfiles/fs/u/LNKTEST3/") },
        {
          recordedRequest = it
          MockResponse().setResponseCode(201)
        }
      )

      val linkFileRequest = ZosmfLinkFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfLinkFileRequestBody(from = "/u/LNKTEST3/source.txt", type = "SYM")
      )
      val linkFileResponse = filesApi.linkFile(linkFileRequest)
        as? ZosmfLinkFileResponse
        ?: fail("Should be instance of ${ZosmfLinkFileResponse::class.java.name}")

      assertSoftly {
        linkFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.requestLine shouldContain "/zosmf/restfiles/fs/u/LNKTEST3/test%20link"
        recordedRequest?.requestLine?.contains("fs//") shouldBe false
      }
    }

    should("linkFile fail because the path to create the link by already exists") {
      val filePath = "u/LNKTEST4/exists.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "linkFile_fail_exists",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":8," +
                "\"rc\":8," +
                "\"reason\":1," +
                "\"message\":\"File exists\"," +
                "\"details\":[\"EDC5117I File exists. (errno2=0x05620062)\"]" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
            .setResponseCode(500)
        }
      )

      val linkFileRequest = ZosmfLinkFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfLinkFileRequestBody(from = "/u/LNKTEST4/source.txt", type = "SYM")
      )
      val linkFileResponse = filesApi.linkFile(linkFileRequest)
        as? ZosmfLinkFileResponse
        ?: fail("Should be instance of ${ZosmfLinkFileResponse::class.java.name}")

      assertSoftly {
        linkFileResponse.status.type shouldBe StatusType.ERROR
        linkFileResponse.status.text shouldContain "500"
        linkFileResponse.status.text shouldContain "EDC5117I File exists"
      }
    }

    should("linkFile fail because the file to link to is not found") {
      val filePath = "u/LNKTEST5/link.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "linkFile_fail_source_not_found",
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

      val linkFileRequest = ZosmfLinkFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfLinkFileRequestBody(from = "/u/LNKTEST5/not_found.txt", type = "SYM")
      )
      val linkFileResponse = filesApi.linkFile(linkFileRequest)
        as? ZosmfLinkFileResponse
        ?: fail("Should be instance of ${ZosmfLinkFileResponse::class.java.name}")

      assertSoftly {
        linkFileResponse.status.type shouldBe StatusType.ERROR
        linkFileResponse.status.text shouldContain "404"
        linkFileResponse.status.text shouldContain "Path name not found"
      }
    }

    should("linkFile fail because there are no permissions to create the link") {
      val filePath = "u/LNKTEST6/forbidden.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "linkFile_fail_no_permissions",
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

      val linkFileRequest = ZosmfLinkFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfLinkFileRequestBody(from = "/u/LNKTEST6/source.txt", type = "SYM")
      )
      val linkFileResponse = filesApi.linkFile(linkFileRequest)
        as? ZosmfLinkFileResponse
        ?: fail("Should be instance of ${ZosmfLinkFileResponse::class.java.name}")

      assertSoftly {
        linkFileResponse.status.type shouldBe StatusType.ERROR
        linkFileResponse.status.text shouldContain "403"
        linkFileResponse.status.text shouldContain "EDC5111I Permission denied"
      }
    }
  }
})
