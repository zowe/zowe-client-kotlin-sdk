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
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.zowe.kotlinsdk.core.ChanneledRequest
import org.zowe.kotlinsdk.core.DataType
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.files.api.FilesAPI
import io.kotest.provided.ProjectConfig.mockHttpConnection
import io.kotest.provided.ProjectConfig.zosmfMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.zosmf.HttpByteRange
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMDataType
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfRetrieveFileContentRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfRetrieveFileContentRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfRetrieveFileContentResponse

class ZosmfRetrieveFileContentTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, FilesAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("retrieveFileContent") {
    should("retrieveFileContent return the USS file content") {
      val filePath = "u/RFCTEST1/test.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "retrieveFileContent_success_text",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          MockResponse()
            .setBody(
              "Test content\n" +
              "Test next line\n" +
              "This is a file content\n" +
              ""
            )
            .addHeader("Content-Type", "text/plain")
        }
      )

      val retrieveFileContentRequest = ZosmfRetrieveFileContentRequest(mockHttpConnection, filePath)
      val retrieveFileContentResponse = filesApi.retrieveFileContent(retrieveFileContentRequest)
        as? ZosmfRetrieveFileContentResponse
        ?: fail("Should be instance of ${ZosmfRetrieveFileContentResponse::class.java.name}")

      assertSoftly {
        retrieveFileContentResponse.status.type shouldBe StatusType.SUCCESS
        retrieveFileContentResponse.fetchedDataType shouldBe DataType.TEXT
        retrieveFileContentResponse.fetchedText shouldContain "file content"
      }
    }

    should("retrieveFileContent execute successfully retrieving an empty USS file") {
      val filePath = "u/RFCTEST2/empty.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "retrieveFileContent_success_text_empty",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          MockResponse()
            .setBody("")
            .addHeader("Content-Type", "text/plain")
        }
      )

      val retrieveFileContentRequest = ZosmfRetrieveFileContentRequest(mockHttpConnection, filePath)
      val retrieveFileContentResponse = filesApi.retrieveFileContent(retrieveFileContentRequest)
        as? ZosmfRetrieveFileContentResponse
        ?: fail("Should be instance of ${ZosmfRetrieveFileContentResponse::class.java.name}")

      assertSoftly {
        retrieveFileContentResponse.status.type shouldBe StatusType.SUCCESS
        retrieveFileContentResponse.fetchedDataType shouldBe DataType.TEXT
        retrieveFileContentResponse.fetchedText shouldBe ""
      }
    }

    should("retrieveFileContent send all the optional query params and the data type header") {
      val filePath = "u/RFCTEST3/search.txt"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "retrieveFileContent_success_search_params",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedRequest = it
          MockResponse()
            .setBody("Test content to search in")
            .addHeader("Content-Type", "text/plain")
        }
      )

      val retrieveFileContentRequest = ZosmfRetrieveFileContentRequest(
        mockHttpConnection,
        filePath,
        search = "content",
        insensitive = false,
        maxReturnSize = 10
      )
      val retrieveFileContentResponse = filesApi.retrieveFileContent(retrieveFileContentRequest)
        as? ZosmfRetrieveFileContentResponse
        ?: fail("Should be instance of ${ZosmfRetrieveFileContentResponse::class.java.name}")

      assertSoftly {
        retrieveFileContentResponse.status.type shouldBe StatusType.SUCCESS
        retrieveFileContentResponse.fetchedText shouldContain "Test content"
        recordedRequest?.requestLine shouldContain "search=content"
        recordedRequest?.requestLine shouldContain "insensitive=false"
        recordedRequest?.requestLine shouldContain "maxreturnsize=10"
        recordedRequest?.requestLine?.contains("research=") shouldBe false
        recordedRequest?.getHeader("X-IBM-Data-Type") shouldBe "text"
      }
    }

    should("retrieveFileContent build the correct URL for an absolute path with the characters to encode") {
      val filePath = "/u/RFCTEST9/test file.txt"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "retrieveFileContent_success_path_encoding",
        { it.requestLine.contains("/zosmf/restfiles/fs/u/RFCTEST9/") },
        {
          recordedRequest = it
          MockResponse()
            .setBody("Test content")
            .addHeader("Content-Type", "text/plain")
        }
      )

      val retrieveFileContentRequest = ZosmfRetrieveFileContentRequest(mockHttpConnection, filePath)
      val retrieveFileContentResponse = filesApi.retrieveFileContent(retrieveFileContentRequest)
        as? ZosmfRetrieveFileContentResponse
        ?: fail("Should be instance of ${ZosmfRetrieveFileContentResponse::class.java.name}")

      assertSoftly {
        retrieveFileContentResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.requestLine shouldContain "/zosmf/restfiles/fs/u/RFCTEST9/test%20file.txt"
        recordedRequest?.requestLine?.contains("fs//") shouldBe false
      }
    }

    should("retrieveFileContent send the 'Range' header for a partial binary read") {
      val filePath = "u/RFCTEST10/ranged.bin"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "retrieveFileContent_success_range",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedRequest = it
          val bodyToReturn = "Test binary content"
          MockResponse()
            .setChunkedBody(bodyToReturn, ChanneledRequest.DEFAULT_CHANNEL_SIZE)
            .addHeader("Content-Type", "application/octet-stream")
            .addHeader("Content-Length", bodyToReturn.length)
        }
      )

      val retrieveFileContentRequest = ZosmfRetrieveFileContentRequest(
        mockHttpConnection,
        filePath,
        headers = ZosmfRetrieveFileContentRequestHeaders(
          range = HttpByteRange(0, 499),
          xIBMDataType = XIBMDataType(XIBMDataType.Type.BINARY)
        )
      )
      val retrieveFileContentResponse = filesApi.retrieveFileContent(retrieveFileContentRequest)
        as? ZosmfRetrieveFileContentResponse
        ?: fail("Should be instance of ${ZosmfRetrieveFileContentResponse::class.java.name}")

      assertSoftly {
        retrieveFileContentResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.getHeader("Range") shouldBe "bytes=0-499"
        HttpByteRange(firstBytePos = 500).toString() shouldBe "bytes=500-"
        HttpByteRange(lastBytePos = 500).toString() shouldBe "bytes=-500"
        shouldThrow<IllegalArgumentException> { HttpByteRange(499, 0) }
        shouldThrow<IllegalArgumentException> { HttpByteRange() }
      }
    }

    should("retrieveFileContent get a USS file content in binary format") {
      val filePath = "u/RFCTEST4/binary.bin"

      zosmfMockResponseDispatcher.injectResolver(
        "retrieveFileContent_success_binary",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          val bodyToReturn = "Test binary content\nof a USS file"
          MockResponse()
            .setChunkedBody(
              bodyToReturn,
              ChanneledRequest.DEFAULT_CHANNEL_SIZE
            )
            .addHeader("Content-Type", "application/octet-stream")
            .addHeader("Content-Length", bodyToReturn.length)
        }
      )

      val retrieveFileContentRequest = ZosmfRetrieveFileContentRequest(
        mockHttpConnection,
        filePath,
        headers = ZosmfRetrieveFileContentRequestHeaders(xIBMDataType = XIBMDataType(XIBMDataType.Type.BINARY))
      )
      val retrieveFileContentResponse = filesApi.retrieveFileContent(retrieveFileContentRequest)
        as? ZosmfRetrieveFileContentResponse
        ?: fail("Should be instance of ${ZosmfRetrieveFileContentResponse::class.java.name}")

      val readChunks = retrieveFileContentResponse.readAsIs()
      assertSoftly {
        retrieveFileContentResponse.status.type shouldBe StatusType.SUCCESS
        retrieveFileContentResponse.fetchedDataType shouldBe DataType.BINARY
        readChunks.joinToString { String(it) } shouldContain "USS file"
      }
    }

    should("retrieveFileContent fail in binary mode when no 'Content-Length' header is returned") {
      val filePath = "u/RFCTEST5/no_content_length.bin"

      zosmfMockResponseDispatcher.injectResolver(
        "retrieveFileContent_fail_no_content_length",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          MockResponse()
            .setChunkedBody(
              "Test binary content",
              ChanneledRequest.DEFAULT_CHANNEL_SIZE
            )
            .addHeader("Content-Type", "application/octet-stream")
        }
      )

      val retrieveFileContentRequest = ZosmfRetrieveFileContentRequest(
        mockHttpConnection,
        filePath,
        headers = ZosmfRetrieveFileContentRequestHeaders(xIBMDataType = XIBMDataType(XIBMDataType.Type.BINARY))
      )
      val retrieveFileContentResponse = filesApi.retrieveFileContent(retrieveFileContentRequest)
        as? ZosmfRetrieveFileContentResponse
        ?: fail("Should be instance of ${ZosmfRetrieveFileContentResponse::class.java.name}")

      assertSoftly {
        retrieveFileContentResponse.status.type shouldBe StatusType.ERROR
        retrieveFileContentResponse.fetchedDataType shouldBe DataType.ERROR
        retrieveFileContentResponse.status.text shouldContain "500"
        retrieveFileContentResponse.status.text shouldContain "'Content-Length' header is not returned"
      }
    }

    should("retrieveFileContent fail because there is no entity found") {
      val filePath = "u/RFCTEST7/not_found.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "retrieveFileContent_fail_not_found",
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

      val retrieveFileContentRequest = ZosmfRetrieveFileContentRequest(mockHttpConnection, filePath)
      val retrieveFileContentResponse = filesApi.retrieveFileContent(retrieveFileContentRequest)
        as? ZosmfRetrieveFileContentResponse
        ?: fail("Should be instance of ${ZosmfRetrieveFileContentResponse::class.java.name}")

      assertSoftly {
        retrieveFileContentResponse.status.type shouldBe StatusType.ERROR
        retrieveFileContentResponse.fetchedDataType shouldBe DataType.ERROR
        retrieveFileContentResponse.status.text shouldContain "404"
        retrieveFileContentResponse.status.text shouldContain "Category: 1"
        retrieveFileContentResponse.status.text shouldContain "RC: 4"
        retrieveFileContentResponse.status.text shouldContain "Reason: 8"
        retrieveFileContentResponse.status.text shouldContain "Path name not found"
      }
    }

    should("retrieveFileContent fail due to some server error") {
      val filePath = "u/RFCTEST8/server_error.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "retrieveFileContent_fail_server_error",
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

      val retrieveFileContentRequest = ZosmfRetrieveFileContentRequest(mockHttpConnection, filePath)
      val retrieveFileContentResponse = filesApi.retrieveFileContent(retrieveFileContentRequest)
        as? ZosmfRetrieveFileContentResponse
        ?: fail("Should be instance of ${ZosmfRetrieveFileContentResponse::class.java.name}")

      assertSoftly {
        retrieveFileContentResponse.status.type shouldBe StatusType.ERROR
        retrieveFileContentResponse.fetchedDataType shouldBe DataType.ERROR
        retrieveFileContentResponse.status.text shouldContain "500"
        retrieveFileContentResponse.status.text shouldContain "File is busy"
      }
    }
  }
})