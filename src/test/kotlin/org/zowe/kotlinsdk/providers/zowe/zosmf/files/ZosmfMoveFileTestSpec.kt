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
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfMoveFileRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfMoveFileRequestBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfMoveFileResponse

class ZosmfMoveFileTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, FilesAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("moveFile") {
    should("moveFile move a USS file") {
      val fromPath = "/u/MVFTEST1/source.txt"
      val filePath = "u/MVFTEST1/target.txt"
      var recordedRequest: RecordedRequest? = null
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "moveFile_success_file",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedRequest = it
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(201)
        }
      )

      val moveFileRequest = ZosmfMoveFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfMoveFileRequestBody(fromPath)
      )
      val moveFileResponse = filesApi.moveFile(moveFileRequest)
        as? ZosmfMoveFileResponse
        ?: fail("Should be instance of ${ZosmfMoveFileResponse::class.java.name}")

      assertSoftly {
        moveFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.method shouldBe "PUT"
        recordedRequest?.getHeader("Content-Type") shouldContain "application/json"
        recordedBody shouldContain "\"from\": \"$fromPath\""
        recordedBody shouldContain "\"request\": \"move\""
        recordedBody shouldNotContain "\"overwrite\""
      }
    }

    should("moveFile move a USS directory with the overwrite option provided") {
      val fromPath = "/u/MVFTEST2/sourcedir"
      val dirPath = "u/MVFTEST2/targetdir"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "moveFile_success_overwrite",
        { it.requestLine.contains("/zosmf/restfiles/fs/$dirPath") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(201)
        }
      )

      val moveFileRequest = ZosmfMoveFileRequest(
        mockHttpConnection,
        dirPath,
        ZosmfMoveFileRequestBody(fromPath, overwrite = true)
      )
      val moveFileResponse = filesApi.moveFile(moveFileRequest)
        as? ZosmfMoveFileResponse
        ?: fail("Should be instance of ${ZosmfMoveFileResponse::class.java.name}")

      assertSoftly {
        moveFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"from\": \"$fromPath\""
        recordedBody shouldContain "\"overwrite\": true"
        recordedBody shouldContain "\"request\": \"move\""
      }
    }

    should("moveFile build the correct URL for an absolute path with the characters to encode") {
      val filePath = "/u/MVFTEST3/test dir"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "moveFile_success_path_encoding",
        { it.requestLine.contains("/zosmf/restfiles/fs/u/MVFTEST3/") },
        {
          recordedRequest = it
          MockResponse().setResponseCode(201)
        }
      )

      val moveFileRequest = ZosmfMoveFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfMoveFileRequestBody("/u/MVFTEST3/source.txt")
      )
      val moveFileResponse = filesApi.moveFile(moveFileRequest)
        as? ZosmfMoveFileResponse
        ?: fail("Should be instance of ${ZosmfMoveFileResponse::class.java.name}")

      assertSoftly {
        moveFileResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.requestLine shouldContain "/zosmf/restfiles/fs/u/MVFTEST3/test%20dir"
        recordedRequest?.requestLine?.contains("fs//") shouldBe false
      }
    }

    should("moveFile fail because the target of the move already exists") {
      val filePath = "u/MVFTEST4/existing.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "moveFile_fail_target_exists",
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

      val moveFileRequest = ZosmfMoveFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfMoveFileRequestBody("/u/MVFTEST4/source.txt")
      )
      val moveFileResponse = filesApi.moveFile(moveFileRequest)
        as? ZosmfMoveFileResponse
        ?: fail("Should be instance of ${ZosmfMoveFileResponse::class.java.name}")

      assertSoftly {
        moveFileResponse.status.type shouldBe StatusType.ERROR
        moveFileResponse.status.text shouldContain "500"
        moveFileResponse.status.text shouldContain "Category: 8"
        moveFileResponse.status.text shouldContain "RC: 8"
        moveFileResponse.status.text shouldContain "EDC5117I File exists"
      }
    }

    should("moveFile fail because the item to move is not found") {
      val filePath = "u/MVFTEST5/target.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "moveFile_fail_source_not_found",
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

      val moveFileRequest = ZosmfMoveFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfMoveFileRequestBody("/u/MVFTEST5/no_source.txt")
      )
      val moveFileResponse = filesApi.moveFile(moveFileRequest)
        as? ZosmfMoveFileResponse
        ?: fail("Should be instance of ${ZosmfMoveFileResponse::class.java.name}")

      assertSoftly {
        moveFileResponse.status.type shouldBe StatusType.ERROR
        moveFileResponse.status.text shouldContain "404"
        moveFileResponse.status.text shouldContain "Path name not found"
      }
    }

    should("moveFile fail because there are no permissions to move the item") {
      val filePath = "u/MVFTEST6/forbidden.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "moveFile_fail_no_permissions",
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

      val moveFileRequest = ZosmfMoveFileRequest(
        mockHttpConnection,
        filePath,
        ZosmfMoveFileRequestBody("/u/MVFTEST6/source.txt")
      )
      val moveFileResponse = filesApi.moveFile(moveFileRequest)
        as? ZosmfMoveFileResponse
        ?: fail("Should be instance of ${ZosmfMoveFileResponse::class.java.name}")

      assertSoftly {
        moveFileResponse.status.type shouldBe StatusType.ERROR
        moveFileResponse.status.text shouldContain "403"
        moveFileResponse.status.text shouldContain "EDC5111I Permission denied"
      }
    }
  }
})
