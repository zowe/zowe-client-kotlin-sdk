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
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.definitions.XIBMOption
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfDeleteFileRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfDeleteFileRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfDeleteFileResponse

class ZosmfDeleteFileTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, FilesAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("deleteFile") {
    should("deleteFile delete a USS file") {
      val filePath = "u/DFTEST1/test.txt"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "deleteFile_success_file",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedRequest = it
          MockResponse().setResponseCode(204)
        }
      )

      val deleteFileRequest = ZosmfDeleteFileRequest(mockHttpConnection, filePath)
      val deleteFileResponse = filesApi.deleteFile(deleteFileRequest)
        as? ZosmfDeleteFileResponse
        ?: fail("Should be instance of ${ZosmfDeleteFileResponse::class.java.name}")

      assertSoftly {
        deleteFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.method shouldBe "DELETE"
        recordedRequest?.getHeader("X-IBM-Option") shouldBe null
      }
    }

    should("deleteFile delete a USS directory recursively") {
      val dirPath = "u/DFTEST2/testdir"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "deleteFile_success_recursive",
        { it.requestLine.contains("/zosmf/restfiles/fs/$dirPath") },
        {
          recordedRequest = it
          MockResponse().setResponseCode(204)
        }
      )

      val deleteFileRequest = ZosmfDeleteFileRequest(
        mockHttpConnection,
        dirPath,
        ZosmfDeleteFileRequestHeaders(XIBMOption.RECURSIVE)
      )
      val deleteFileResponse = filesApi.deleteFile(deleteFileRequest)
        as? ZosmfDeleteFileResponse
        ?: fail("Should be instance of ${ZosmfDeleteFileResponse::class.java.name}")

      assertSoftly {
        deleteFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.getHeader("X-IBM-Option") shouldBe "recursive"
      }
    }

    should("deleteFile build the correct URL for an absolute path with the characters to encode") {
      val filePath = "/u/DFTEST3/test dir"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "deleteFile_success_path_encoding",
        { it.requestLine.contains("/zosmf/restfiles/fs/u/DFTEST3/") },
        {
          recordedRequest = it
          MockResponse().setResponseCode(204)
        }
      )

      val deleteFileRequest = ZosmfDeleteFileRequest(mockHttpConnection, filePath)
      val deleteFileResponse = filesApi.deleteFile(deleteFileRequest)
        as? ZosmfDeleteFileResponse
        ?: fail("Should be instance of ${ZosmfDeleteFileResponse::class.java.name}")

      assertSoftly {
        deleteFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.requestLine shouldContain "/zosmf/restfiles/fs/u/DFTEST3/test%20dir"
        recordedRequest?.requestLine?.contains("fs//") shouldBe false
      }
    }

    should("deleteFile produce warning because the HTTP code is not in the list of the correct ones") {
      val filePath = "u/DFTEST4/unexpected_code.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "deleteFile_warn_unexpected_success_code",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        { MockResponse().setResponseCode(200) }
      )

      val deleteFileRequest = ZosmfDeleteFileRequest(mockHttpConnection, filePath)
      val deleteFileResponse = filesApi.deleteFile(deleteFileRequest)
        as? ZosmfDeleteFileResponse
        ?: fail("Should be instance of ${ZosmfDeleteFileResponse::class.java.name}")

      assertSoftly { deleteFileResponse.status.type shouldBe StatusType.WARNING }
    }

    should("deleteFile fail because the directory to delete is not empty") {
      val dirPath = "u/DFTEST5/notempty"

      zosmfMockResponseDispatcher.injectResolver(
        "deleteFile_fail_not_empty",
        { it.requestLine.contains("/zosmf/restfiles/fs/$dirPath") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":1," +
                "\"rc\":4," +
                "\"reason\":93," +
                "\"message\":\"Directory not empty\"," +
                "\"details\":[\"EDC5136I Directory not empty. (errno2=0x05620062)\"]" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
            .setResponseCode(500)
        }
      )

      val deleteFileRequest = ZosmfDeleteFileRequest(mockHttpConnection, dirPath)
      val deleteFileResponse = filesApi.deleteFile(deleteFileRequest)
        as? ZosmfDeleteFileResponse
        ?: fail("Should be instance of ${ZosmfDeleteFileResponse::class.java.name}")

      assertSoftly {
        deleteFileResponse.status.type shouldBe StatusType.ERROR
        deleteFileResponse.status.text shouldContain "500"
        deleteFileResponse.status.text shouldContain "Category: 1"
        deleteFileResponse.status.text shouldContain "RC: 4"
        deleteFileResponse.status.text shouldContain "Reason: 93"
        deleteFileResponse.status.text shouldContain "EDC5136I Directory not empty"
      }
    }

    should("deleteFile fail because the item to delete is not found") {
      val filePath = "u/DFTEST6/not_found.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "deleteFile_fail_not_found",
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

      val deleteFileRequest = ZosmfDeleteFileRequest(mockHttpConnection, filePath)
      val deleteFileResponse = filesApi.deleteFile(deleteFileRequest)
        as? ZosmfDeleteFileResponse
        ?: fail("Should be instance of ${ZosmfDeleteFileResponse::class.java.name}")

      assertSoftly {
        deleteFileResponse.status.type shouldBe StatusType.ERROR
        deleteFileResponse.status.text shouldContain "404"
        deleteFileResponse.status.text shouldContain "Path name not found"
      }
    }

    should("deleteFile fail because there are no permissions to delete the item") {
      val filePath = "u/DFTEST7/forbidden.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "deleteFile_fail_no_permissions",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":8," +
                "\"rc\":8," +
                "\"reason\":13," +
                "\"message\":\"Permission denied\"," +
                "\"details\":[\"EDC5111I Permission denied. (errno2=0x0C0F8106)\"]" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
            .setResponseCode(403)
        }
      )

      val deleteFileRequest = ZosmfDeleteFileRequest(mockHttpConnection, filePath)
      val deleteFileResponse = filesApi.deleteFile(deleteFileRequest)
        as? ZosmfDeleteFileResponse
        ?: fail("Should be instance of ${ZosmfDeleteFileResponse::class.java.name}")

      assertSoftly {
        deleteFileResponse.status.type shouldBe StatusType.ERROR
        deleteFileResponse.status.text shouldContain "403"
        deleteFileResponse.status.text shouldContain "EDC5111I Permission denied"
      }
    }
  }
})
