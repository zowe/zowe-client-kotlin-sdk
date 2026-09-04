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
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMDataType
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfWriteToFileRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfWriteToFileRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfWriteToFileResponse

class ZosmfWriteToFileTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, FilesAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("writeToFile") {
    should("writeToFile write the text content to a USS file") {
      val filePath = "u/WTFTEST1/test.txt"
      val testContent = "Test content\nTest next line\n".toByteArray()
      var recordedRequest: RecordedRequest? = null
      var recordedBody: ByteArray? = null

      zosmfMockResponseDispatcher.injectResolver(
        "writeToFile_success_text",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedRequest = it
          recordedBody = it.body.readByteArray()
          MockResponse().setResponseCode(204)
        }
      )

      val writeToFileRequest = ZosmfWriteToFileRequest(mockHttpConnection, filePath, testContent)
      val writeToFileResponse = filesApi.writeToFile(writeToFileRequest)
        as? ZosmfWriteToFileResponse
        ?: fail("Should be instance of ${ZosmfWriteToFileResponse::class.java.name}")

      assertSoftly {
        writeToFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.method shouldBe "PUT"
        recordedBody shouldBe testContent
        recordedRequest?.getHeader("X-IBM-Data-Type") shouldBe "text"
      }
    }

    should("writeToFile create a USS file with the provided content") {
      val filePath = "u/WTFTEST2/created.txt"
      val testContent = "Test content".toByteArray()

      zosmfMockResponseDispatcher.injectResolver(
        "writeToFile_success_created",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        { MockResponse().setResponseCode(201) }
      )

      val writeToFileRequest = ZosmfWriteToFileRequest(mockHttpConnection, filePath, testContent)
      val writeToFileResponse = filesApi.writeToFile(writeToFileRequest)
        as? ZosmfWriteToFileResponse
        ?: fail("Should be instance of ${ZosmfWriteToFileResponse::class.java.name}")

      assertSoftly { writeToFileResponse.status.type shouldBe StatusType.SUCCESS }
    }

    should("writeToFile write the binary content to a USS file") {
      val filePath = "u/WTFTEST3/binary.bin"
      val testContent = "Test binary content".toByteArray() +
        byteArrayOf(0x00.toByte(), 0xFF.toByte(), 0x01.toByte(), 0x7F.toByte())
      var recordedRequest: RecordedRequest? = null
      var recordedBody: ByteArray? = null

      zosmfMockResponseDispatcher.injectResolver(
        "writeToFile_success_binary",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedRequest = it
          recordedBody = it.body.readByteArray()
          MockResponse().setResponseCode(204)
        }
      )

      val writeToFileRequest = ZosmfWriteToFileRequest(
        mockHttpConnection,
        filePath,
        testContent,
        headers = ZosmfWriteToFileRequestHeaders(xIBMDataType = XIBMDataType(XIBMDataType.Type.BINARY))
      )
      val writeToFileResponse = filesApi.writeToFile(writeToFileRequest)
        as? ZosmfWriteToFileResponse
        ?: fail("Should be instance of ${ZosmfWriteToFileResponse::class.java.name}")

      assertSoftly {
        writeToFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldBe testContent
        recordedRequest?.getHeader("X-IBM-Data-Type") shouldBe "binary"
      }
    }

    should("writeToFile send the 'If-Match' header to write the file only if it is not changed") {
      val filePath = "u/WTFTEST4/etag.txt"
      val etag = "A7CF12E8B23D4A9187654321FEDCBA09"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "writeToFile_success_if_match",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedRequest = it
          MockResponse().setResponseCode(204)
        }
      )

      val writeToFileRequest = ZosmfWriteToFileRequest(
        mockHttpConnection,
        filePath,
        "Test content".toByteArray(),
        headers = ZosmfWriteToFileRequestHeaders(ifMatch = etag)
      )
      val writeToFileResponse = filesApi.writeToFile(writeToFileRequest)
        as? ZosmfWriteToFileResponse
        ?: fail("Should be instance of ${ZosmfWriteToFileResponse::class.java.name}")

      assertSoftly {
        writeToFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.getHeader("If-Match") shouldBe etag
      }
    }

    should("writeToFile build the correct URL for an absolute path with the characters to encode") {
      val filePath = "/u/WTFTEST5/test file.txt"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "writeToFile_success_path_encoding",
        { it.requestLine.contains("/zosmf/restfiles/fs/u/WTFTEST5/") },
        {
          recordedRequest = it
          MockResponse().setResponseCode(204)
        }
      )

      val writeToFileRequest = ZosmfWriteToFileRequest(mockHttpConnection, filePath, "Test content".toByteArray())
      val writeToFileResponse = filesApi.writeToFile(writeToFileRequest)
        as? ZosmfWriteToFileResponse
        ?: fail("Should be instance of ${ZosmfWriteToFileResponse::class.java.name}")

      assertSoftly {
        writeToFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.requestLine shouldContain "/zosmf/restfiles/fs/u/WTFTEST5/test%20file.txt"
        recordedRequest?.requestLine?.contains("fs//") shouldBe false
      }
    }

    should("writeToFile produce warning because the HTTP code is not in the list of the correct ones") {
      val filePath = "u/WTFTEST6/unexpected_code.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "writeToFile_warn_unexpected_success_code",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        { MockResponse().setResponseCode(200) }
      )

      val writeToFileRequest = ZosmfWriteToFileRequest(mockHttpConnection, filePath, "Test content".toByteArray())
      val writeToFileResponse = filesApi.writeToFile(writeToFileRequest)
        as? ZosmfWriteToFileResponse
        ?: fail("Should be instance of ${ZosmfWriteToFileResponse::class.java.name}")

      assertSoftly { writeToFileResponse.status.type shouldBe StatusType.WARNING }
    }

    should("writeToFile fail because the path to write the file to is not found") {
      val filePath = "u/WTFTEST7/not_found/test.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "writeToFile_fail_not_found",
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

      val writeToFileRequest = ZosmfWriteToFileRequest(mockHttpConnection, filePath, "Test content".toByteArray())
      val writeToFileResponse = filesApi.writeToFile(writeToFileRequest)
        as? ZosmfWriteToFileResponse
        ?: fail("Should be instance of ${ZosmfWriteToFileResponse::class.java.name}")

      assertSoftly {
        writeToFileResponse.status.type shouldBe StatusType.ERROR
        writeToFileResponse.status.text shouldContain "404"
        writeToFileResponse.status.text shouldContain "Category: 1"
        writeToFileResponse.status.text shouldContain "RC: 4"
        writeToFileResponse.status.text shouldContain "Reason: 8"
        writeToFileResponse.status.text shouldContain "Path name not found"
      }
    }

    should("writeToFile fail because the file is changed since it was read") {
      val filePath = "u/WTFTEST8/precondition_failed.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "writeToFile_fail_precondition_failed",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":7," +
                "\"rc\":-1," +
                "\"reason\":-1," +
                "\"message\":\"Precondition failed\"," +
                "\"details\":[\"The file has been modified since it was read\"]" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
            .setResponseCode(412)
        }
      )

      val writeToFileRequest = ZosmfWriteToFileRequest(
        mockHttpConnection,
        filePath,
        "Test content".toByteArray(),
        headers = ZosmfWriteToFileRequestHeaders(ifMatch = "A7CF12E8B23D4A9187654321FEDCBA09")
      )
      val writeToFileResponse = filesApi.writeToFile(writeToFileRequest)
        as? ZosmfWriteToFileResponse
        ?: fail("Should be instance of ${ZosmfWriteToFileResponse::class.java.name}")

      assertSoftly {
        writeToFileResponse.status.type shouldBe StatusType.ERROR
        writeToFileResponse.status.text shouldContain "412"
        writeToFileResponse.status.text shouldContain "Precondition failed"
      }
    }

    should("writeToFile fail due to some server error") {
      val filePath = "u/WTFTEST9/server_error.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "writeToFile_fail_server_error",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          MockResponse()
            .setBody(
              "\"category\":6," +
              "\"rc\":8," +
              "\"reason\":201," +
              "\"message\":\"fopen() failed\"," +
              "\"stack\":\"\"," +
              "\"details\":[\"File is busy\"]"
            )
            .setResponseCode(500)
        }
      )

      val writeToFileRequest = ZosmfWriteToFileRequest(mockHttpConnection, filePath, "Test content".toByteArray())
      val writeToFileResponse = filesApi.writeToFile(writeToFileRequest)
        as? ZosmfWriteToFileResponse
        ?: fail("Should be instance of ${ZosmfWriteToFileResponse::class.java.name}")

      assertSoftly {
        writeToFileResponse.status.type shouldBe StatusType.ERROR
        writeToFileResponse.status.text shouldContain "500"
        writeToFileResponse.status.text shouldContain "File is busy"
      }
    }
  }
})
