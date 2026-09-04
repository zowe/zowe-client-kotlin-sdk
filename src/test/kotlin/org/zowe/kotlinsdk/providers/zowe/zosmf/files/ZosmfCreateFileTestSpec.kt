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
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.definitions.ZosmfFileMode
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfCreateFileRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfCreateFileRequestBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfCreateFileResponse

class ZosmfCreateFileTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, FilesAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("createFile") {
    should("createFile create a USS file") {
      val filePath = "u/CFTEST1/test.txt"
      var recordedRequest: RecordedRequest? = null
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "createFile_success_file",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedRequest = it
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(201)
        }
      )

      val createFileRequest = ZosmfCreateFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfCreateFileRequestBody(
          ZosmfCreateFileRequestBody.ZosmfFileType.FILE,
          ZosmfFileMode.fromString("rw-r--r--")
        )
      )
      val createFileResponse = filesApi.createFile(createFileRequest)
        as? ZosmfCreateFileResponse
        ?: fail("Should be instance of ${ZosmfCreateFileResponse::class.java.name}")

      assertSoftly {
        createFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.method shouldBe "POST"
        recordedRequest?.getHeader("Content-Type") shouldContain "application/json"
        recordedBody shouldContain "\"type\": \"file\""
        recordedBody shouldContain "\"mode\": \"rw-r--r--\""
      }
    }

    should("createFile create a USS directory") {
      val dirPath = "u/CFTEST2/testdir"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "createFile_success_directory",
        { it.requestLine.contains("/zosmf/restfiles/fs/$dirPath") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(201)
        }
      )

      val createFileRequest = ZosmfCreateFileRequest(
        mockHttpConnection,
        dirPath,
        ZosmfCreateFileRequestBody(
          ZosmfCreateFileRequestBody.ZosmfFileType.FOLDER,
          ZosmfFileMode.fromString("rwxr-x---")
        )
      )
      val createFileResponse = filesApi.createFile(createFileRequest)
        as? ZosmfCreateFileResponse
        ?: fail("Should be instance of ${ZosmfCreateFileResponse::class.java.name}")

      assertSoftly {
        createFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"type\": \"directory\""
        recordedBody shouldContain "\"mode\": \"rwxr-x---\""
      }
    }

    should("createFile build the correct URL for an absolute path with the characters to encode") {
      val filePath = "/u/CFTEST3/test dir"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "createFile_success_path_encoding",
        { it.requestLine.contains("/zosmf/restfiles/fs/u/CFTEST3/") },
        {
          recordedRequest = it
          MockResponse().setResponseCode(201)
        }
      )

      val createFileRequest = ZosmfCreateFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfCreateFileRequestBody(
          ZosmfCreateFileRequestBody.ZosmfFileType.FOLDER,
          ZosmfFileMode.fromString("rwxr-xr-x")
        )
      )
      val createFileResponse = filesApi.createFile(createFileRequest)
        as? ZosmfCreateFileResponse
        ?: fail("Should be instance of ${ZosmfCreateFileResponse::class.java.name}")

      assertSoftly {
        createFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.requestLine shouldContain "/zosmf/restfiles/fs/u/CFTEST3/test%20dir"
        recordedRequest?.requestLine?.contains("fs//") shouldBe false
      }
    }

    should("createFile produce warning because the HTTP code is not in the list of the correct ones") {
      val filePath = "u/CFTEST4/unexpected_code.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "createFile_warn_unexpected_success_code",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        { MockResponse().setResponseCode(204) }
      )

      val createFileRequest = ZosmfCreateFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfCreateFileRequestBody(
          ZosmfCreateFileRequestBody.ZosmfFileType.FILE,
          ZosmfFileMode.fromString("rw-r--r--")
        )
      )
      val createFileResponse = filesApi.createFile(createFileRequest)
        as? ZosmfCreateFileResponse
        ?: fail("Should be instance of ${ZosmfCreateFileResponse::class.java.name}")

      assertSoftly { createFileResponse.status.type shouldBe StatusType.WARNING }
    }

    should("createFile fail because the item to create already exists") {
      val filePath = "u/CFTEST5/existing.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "createFile_fail_already_exists",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":1," +
                "\"rc\":4," +
                "\"reason\":19," +
                "\"message\":\"The file or directory already exists\"," +
                "\"details\":[\"EDC5117I File exists. (errno2=0x05620062)\"]" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
            .setResponseCode(500)
        }
      )

      val createFileRequest = ZosmfCreateFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfCreateFileRequestBody(
          ZosmfCreateFileRequestBody.ZosmfFileType.FILE,
          ZosmfFileMode.fromString("rw-r--r--")
        )
      )
      val createFileResponse = filesApi.createFile(createFileRequest)
        as? ZosmfCreateFileResponse
        ?: fail("Should be instance of ${ZosmfCreateFileResponse::class.java.name}")

      assertSoftly {
        createFileResponse.status.type shouldBe StatusType.ERROR
        createFileResponse.status.text shouldContain "500"
        createFileResponse.status.text shouldContain "Category: 1"
        createFileResponse.status.text shouldContain "RC: 4"
        createFileResponse.status.text shouldContain "Reason: 19"
        createFileResponse.status.text shouldContain "EDC5117I File exists"
      }
    }

    should("createFile fail because the path to hold the item is not found") {
      val filePath = "u/CFTEST6/not_found/test.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "createFile_fail_not_found",
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

      val createFileRequest = ZosmfCreateFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfCreateFileRequestBody(
          ZosmfCreateFileRequestBody.ZosmfFileType.FILE,
          ZosmfFileMode.fromString("rw-r--r--")
        )
      )
      val createFileResponse = filesApi.createFile(createFileRequest)
        as? ZosmfCreateFileResponse
        ?: fail("Should be instance of ${ZosmfCreateFileResponse::class.java.name}")

      assertSoftly {
        createFileResponse.status.type shouldBe StatusType.ERROR
        createFileResponse.status.text shouldContain "404"
        createFileResponse.status.text shouldContain "Path name not found"
      }
    }

    should("createFile fail because there are no permissions to create the item") {
      val filePath = "u/CFTEST7/forbidden.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "createFile_fail_no_permissions",
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

      val createFileRequest = ZosmfCreateFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfCreateFileRequestBody(
          ZosmfCreateFileRequestBody.ZosmfFileType.FILE,
          ZosmfFileMode.fromString("rw-r--r--")
        )
      )
      val createFileResponse = filesApi.createFile(createFileRequest)
        as? ZosmfCreateFileResponse
        ?: fail("Should be instance of ${ZosmfCreateFileResponse::class.java.name}")

      assertSoftly {
        createFileResponse.status.type shouldBe StatusType.ERROR
        createFileResponse.status.text shouldContain "403"
        createFileResponse.status.text shouldContain "EDC5111I Permission denied"
      }
    }
  }
})
