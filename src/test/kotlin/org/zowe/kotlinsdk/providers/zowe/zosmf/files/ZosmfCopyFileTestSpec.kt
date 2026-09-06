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
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfCopyFileRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfCopyFileRequestBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfCopyFileResponse

class ZosmfCopyFileTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, FilesAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("copyFile") {
    should("copyFile copy a USS file") {
      val fromPath = "/u/CPFTEST1/source.txt"
      val filePath = "u/CPFTEST1/target.txt"
      var recordedRequest: RecordedRequest? = null
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "copyFile_success_file",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedRequest = it
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(201)
        }
      )

      val copyFileRequest = ZosmfCopyFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfCopyFileRequestBody(fromPath)
      )
      val copyFileResponse = filesApi.copyFile(copyFileRequest)
        as? ZosmfCopyFileResponse
        ?: fail("Should be instance of ${ZosmfCopyFileResponse::class.java.name}")

      assertSoftly {
        copyFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.method shouldBe "PUT"
        recordedRequest?.getHeader("Content-Type") shouldContain "application/json"
        recordedBody shouldContain "\"from\": \"$fromPath\""
        recordedBody shouldContain "\"request\": \"copy\""
      }
    }

    should("copyFile copy a USS directory with all the copy options provided") {
      val fromPath = "/u/CPFTEST2/sourcedir"
      val dirPath = "u/CPFTEST2/targetdir"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "copyFile_success_with_options",
        { it.requestLine.contains("/zosmf/restfiles/fs/$dirPath") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(201)
        }
      )

      val copyFileRequest = ZosmfCopyFileRequest(
        mockHttpConnection,
        dirPath,
        ZosmfCopyFileRequestBody(
          from = fromPath,
          overwrite = true,
          recursive = true,
          links = ZosmfCopyFileRequestBody.Links.ALL,
          preserve = ZosmfCopyFileRequestBody.Preserve.MODTIME
        )
      )
      val copyFileResponse = filesApi.copyFile(copyFileRequest)
        as? ZosmfCopyFileResponse
        ?: fail("Should be instance of ${ZosmfCopyFileResponse::class.java.name}")

      assertSoftly {
        copyFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"overwrite\": true"
        recordedBody shouldContain "\"recursive\": true"
        recordedBody shouldContain "\"links\": \"all\""
        recordedBody shouldContain "\"preserve\": \"modtime\""
      }
    }

    should("copyFile copy a data set member to a USS file") {
      val filePath = "u/CPFTEST3/target.txt"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "copyFile_success_from_dataset",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(201)
        }
      )

      val copyFileRequest = ZosmfCopyFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfCopyFileRequestBody(
          from = null,
          fromDataset = ZosmfCopyFileRequestBody.FromDataset(
            "TEST.COPY.DS",
            "MEMBER1",
            ZosmfCopyFileRequestBody.FromDataset.Type.TEXT
          )
        )
      )
      val copyFileResponse = filesApi.copyFile(copyFileRequest)
        as? ZosmfCopyFileResponse
        ?: fail("Should be instance of ${ZosmfCopyFileResponse::class.java.name}")

      assertSoftly {
        copyFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"from-dataset\""
        recordedBody shouldContain "\"dsn\": \"TEST.COPY.DS\""
        recordedBody shouldContain "\"member\": \"MEMBER1\""
        recordedBody shouldContain "\"type\": \"text\""
        recordedBody shouldContain "\"request\": \"copy\""
      }
    }

    should("copyFile build the correct URL for an absolute path with the characters to encode") {
      val filePath = "/u/CPFTEST4/test dir"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "copyFile_success_path_encoding",
        { it.requestLine.contains("/zosmf/restfiles/fs/u/CPFTEST4/") },
        {
          recordedRequest = it
          MockResponse().setResponseCode(201)
        }
      )

      val copyFileRequest = ZosmfCopyFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfCopyFileRequestBody("/u/CPFTEST4/source.txt")
      )
      val copyFileResponse = filesApi.copyFile(copyFileRequest)
        as? ZosmfCopyFileResponse
        ?: fail("Should be instance of ${ZosmfCopyFileResponse::class.java.name}")

      assertSoftly {
        copyFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.requestLine shouldContain "/zosmf/restfiles/fs/u/CPFTEST4/test%20dir"
        recordedRequest?.requestLine?.contains("fs//") shouldBe false
      }
    }

    should("copyFile fail because the target of the copy already exists") {
      val filePath = "u/CPFTEST5/existing.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "copyFile_fail_target_exists",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":8," +
                "\"rc\":8," +
                "\"reason\":17," +
                "\"message\":\"File exists\"," +
                "\"details\":[\"EDC5117I File exists. (errno2=0x05620062)\"]" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
            .setResponseCode(500)
        }
      )

      val copyFileRequest = ZosmfCopyFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfCopyFileRequestBody("/u/CPFTEST5/source.txt")
      )
      val copyFileResponse = filesApi.copyFile(copyFileRequest)
        as? ZosmfCopyFileResponse
        ?: fail("Should be instance of ${ZosmfCopyFileResponse::class.java.name}")

      assertSoftly {
        copyFileResponse.status.type shouldBe StatusType.ERROR
        copyFileResponse.status.text shouldContain "500"
        copyFileResponse.status.text shouldContain "Category: 8"
        copyFileResponse.status.text shouldContain "RC: 8"
        copyFileResponse.status.text shouldContain "EDC5117I File exists"
      }
    }

    should("copyFile fail because the source to copy from is not found") {
      val filePath = "u/CPFTEST6/target.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "copyFile_fail_source_not_found",
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

      val copyFileRequest = ZosmfCopyFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfCopyFileRequestBody("/u/CPFTEST6/no_source.txt")
      )
      val copyFileResponse = filesApi.copyFile(copyFileRequest)
        as? ZosmfCopyFileResponse
        ?: fail("Should be instance of ${ZosmfCopyFileResponse::class.java.name}")

      assertSoftly {
        copyFileResponse.status.type shouldBe StatusType.ERROR
        copyFileResponse.status.text shouldContain "404"
        copyFileResponse.status.text shouldContain "Path name not found"
      }
    }

    should("copyFile fail because there are no permissions to copy the item") {
      val filePath = "u/CPFTEST7/forbidden.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "copyFile_fail_no_permissions",
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

      val copyFileRequest = ZosmfCopyFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfCopyFileRequestBody("/u/CPFTEST7/source.txt")
      )
      val copyFileResponse = filesApi.copyFile(copyFileRequest)
        as? ZosmfCopyFileResponse
        ?: fail("Should be instance of ${ZosmfCopyFileResponse::class.java.name}")

      assertSoftly {
        copyFileResponse.status.type shouldBe StatusType.ERROR
        copyFileResponse.status.text shouldContain "403"
        copyFileResponse.status.text shouldContain "EDC5111I Permission denied"
      }
    }
  }
})
