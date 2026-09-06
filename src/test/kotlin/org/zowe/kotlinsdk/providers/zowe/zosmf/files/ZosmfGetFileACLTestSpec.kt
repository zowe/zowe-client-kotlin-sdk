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
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfGetFileACLRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfGetFileACLRequestBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfGetFileACLResponse

class ZosmfGetFileACLTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, FilesAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("getFileACL") {
    should("getFileACL retrieve the access control list of a USS file") {
      val filePath = "u/ACLTEST1/test.txt"
      var recordedRequest: RecordedRequest? = null
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "getFileACL_success",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedRequest = it
          recordedBody = it.body.readUtf8()
          MockResponse()
            .setBody(
              "{\"stdout\":[" +
                "\"#file: /$filePath\"," +
                "\"#owner: ACLTEST1\"," +
                "\"#group: ACLGRP\"," +
                "\"user::rwx\"," +
                "\"group::r-x\"," +
                "\"other::r--\"" +
              "]}"
            )
            .addHeader("Content-Type", "application/json")
            .setResponseCode(200)
        }
      )

      val getFileACLRequest = ZosmfGetFileACLRequest(
        mockHttpConnection,
        filePath,
        ZosmfGetFileACLRequestBody()
      )
      val getFileACLResponse = filesApi.getFileACL(getFileACLRequest)
        as? ZosmfGetFileACLResponse
        ?: fail("Should be instance of ${ZosmfGetFileACLResponse::class.java.name}")

      assertSoftly {
        getFileACLResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.method shouldBe "PUT"
        recordedRequest?.getHeader("Content-Type") shouldContain "application/json"
        recordedBody shouldContain "\"request\": \"getfacl\""
        recordedBody shouldNotContain "\"type\""
        getFileACLResponse.fileACL shouldBe listOf(
          "#file: /$filePath",
          "#owner: ACLTEST1",
          "#group: ACLGRP",
          "user::rwx",
          "group::r-x",
          "other::r--"
        )
      }
    }

    should("getFileACL retrieve the access control list with all the display options applied") {
      val filePath = "u/ACLTEST2/test.txt"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "getFileACL_success_all_options",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse()
            .setBody("{\"stdout\":[\"user:ANOTHER:rw-,group:ACLGRP:r--\"]}")
            .addHeader("Content-Type", "application/json")
            .setResponseCode(200)
        }
      )

      val getFileACLRequest = ZosmfGetFileACLRequest(
        mockHttpConnection,
        filePath,
        ZosmfGetFileACLRequestBody(
          type = ZosmfGetFileACLRequestBody.Type.DIR,
          user = "ANOTHER",
          useCommas = true,
          suppressHeader = true,
          suppressBaseACL = true
        )
      )
      val getFileACLResponse = filesApi.getFileACL(getFileACLRequest)
        as? ZosmfGetFileACLResponse
        ?: fail("Should be instance of ${ZosmfGetFileACLResponse::class.java.name}")

      assertSoftly {
        getFileACLResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"request\": \"getfacl\""
        recordedBody shouldContain "\"type\": \"dir\""
        recordedBody shouldContain "\"user\": \"ANOTHER\""
        recordedBody shouldContain "\"use-commas\": true"
        recordedBody shouldContain "\"suppress-header\": true"
        recordedBody shouldContain "\"suppress-baseacl\": true"
        getFileACLResponse.fileACL shouldBe listOf("user:ANOTHER:rw-,group:ACLGRP:r--")
      }
    }

    should("getFileACL retrieve the access control list of a USS file with no entry to display") {
      val filePath = "u/ACLTEST3/test.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "getFileACL_success_empty",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        { MockResponse().setResponseCode(200) }
      )

      val getFileACLRequest = ZosmfGetFileACLRequest(
        mockHttpConnection,
        filePath,
        ZosmfGetFileACLRequestBody(suppressBaseACL = true)
      )
      val getFileACLResponse = filesApi.getFileACL(getFileACLRequest)
        as? ZosmfGetFileACLResponse
        ?: fail("Should be instance of ${ZosmfGetFileACLResponse::class.java.name}")

      assertSoftly {
        getFileACLResponse.status.type shouldBe StatusType.SUCCESS
        getFileACLResponse.fileACL shouldBe null
      }
    }

    should("getFileACL build the correct URL for an absolute path with the characters to encode") {
      val filePath = "/u/ACLTEST4/test file"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "getFileACL_success_path_encoding",
        { it.requestLine.contains("/zosmf/restfiles/fs/u/ACLTEST4/") },
        {
          recordedRequest = it
          MockResponse().setResponseCode(200)
        }
      )

      val getFileACLRequest = ZosmfGetFileACLRequest(
        mockHttpConnection,
        filePath,
        ZosmfGetFileACLRequestBody()
      )
      val getFileACLResponse = filesApi.getFileACL(getFileACLRequest)
        as? ZosmfGetFileACLResponse
        ?: fail("Should be instance of ${ZosmfGetFileACLResponse::class.java.name}")

      assertSoftly {
        getFileACLResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.requestLine shouldContain "/zosmf/restfiles/fs/u/ACLTEST4/test%20file"
        recordedRequest?.requestLine?.contains("fs//") shouldBe false
      }
    }

    should("getFileACL fail because the file to operate is not found") {
      val filePath = "u/ACLTEST5/not_found.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "getFileACL_fail_not_found",
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

      val getFileACLRequest = ZosmfGetFileACLRequest(
        mockHttpConnection,
        filePath,
        ZosmfGetFileACLRequestBody()
      )
      val getFileACLResponse = filesApi.getFileACL(getFileACLRequest)
        as? ZosmfGetFileACLResponse
        ?: fail("Should be instance of ${ZosmfGetFileACLResponse::class.java.name}")

      assertSoftly {
        getFileACLResponse.status.type shouldBe StatusType.ERROR
        getFileACLResponse.status.text shouldContain "404"
        getFileACLResponse.status.text shouldContain "Path name not found"
        getFileACLResponse.fileACL shouldBe null
      }
    }

    should("getFileACL fail because there are no permissions to read the file access control list") {
      val filePath = "u/ACLTEST6/forbidden.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "getFileACL_fail_no_permissions",
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

      val getFileACLRequest = ZosmfGetFileACLRequest(
        mockHttpConnection,
        filePath,
        ZosmfGetFileACLRequestBody()
      )
      val getFileACLResponse = filesApi.getFileACL(getFileACLRequest)
        as? ZosmfGetFileACLResponse
        ?: fail("Should be instance of ${ZosmfGetFileACLResponse::class.java.name}")

      assertSoftly {
        getFileACLResponse.status.type shouldBe StatusType.ERROR
        getFileACLResponse.status.text shouldContain "403"
        getFileACLResponse.status.text shouldContain "EDC5111I Permission denied"
      }
    }
  }
})
