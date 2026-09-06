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
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfSetFileACLRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfSetFileACLRequestBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfSetFileACLResponse

class ZosmfSetFileACLTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, FilesAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("setFileACL") {
    should("setFileACL replace the access control list of a USS file") {
      val filePath = "u/SACLTST1/test.txt"
      var recordedRequest: RecordedRequest? = null
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "setFileACL_success_set",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedRequest = it
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(200)
        }
      )

      val setFileACLRequest = ZosmfSetFileACLRequest(
        mockHttpConnection,
        filePath,
        ZosmfSetFileACLRequestBody(set = "user::rwx,group::r-x,other::---")
      )
      val setFileACLResponse = filesApi.setFileACL(setFileACLRequest)
        as? ZosmfSetFileACLResponse
        ?: fail("Should be instance of ${ZosmfSetFileACLResponse::class.java.name}")

      assertSoftly {
        setFileACLResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.method shouldBe "PUT"
        recordedRequest?.getHeader("Content-Type") shouldContain "application/json"
        recordedBody shouldContain "\"request\": \"setfacl\""
        recordedBody shouldContain "\"set\": \"user::rwx,group::r-x,other::---\""
        recordedBody shouldNotContain "\"modify\""
        recordedBody shouldNotContain "\"delete\""
      }
    }

    should("setFileACL modify and delete the access control list entries by the single request") {
      val filePath = "u/SACLTST2/test.txt"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "setFileACL_success_modify_and_delete",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(200)
        }
      )

      val setFileACLRequest = ZosmfSetFileACLRequest(
        mockHttpConnection,
        filePath,
        ZosmfSetFileACLRequestBody(
          modify = "user:ANOTHER:rw-",
          delete = "user:OBSOLETE:"
        )
      )
      val setFileACLResponse = filesApi.setFileACL(setFileACLRequest)
        as? ZosmfSetFileACLResponse
        ?: fail("Should be instance of ${ZosmfSetFileACLResponse::class.java.name}")

      assertSoftly {
        setFileACLResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"modify\": \"user:ANOTHER:rw-\""
        recordedBody shouldContain "\"delete\": \"user:OBSOLETE:\""
        recordedBody shouldNotContain "\"set\""
      }
    }

    should("setFileACL delete all the extended access control list entries by the type") {
      val filePath = "u/SACLTST3/testdir"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "setFileACL_success_delete_type",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(200)
        }
      )

      val setFileACLRequest = ZosmfSetFileACLRequest(
        mockHttpConnection,
        filePath,
        ZosmfSetFileACLRequestBody(
          abort = true,
          links = ZosmfSetFileACLRequestBody.Links.SUPPRESS,
          deleteType = ZosmfSetFileACLRequestBody.DeleteType.EVERY
        )
      )
      val setFileACLResponse = filesApi.setFileACL(setFileACLRequest)
        as? ZosmfSetFileACLResponse
        ?: fail("Should be instance of ${ZosmfSetFileACLResponse::class.java.name}")

      assertSoftly {
        setFileACLResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"abort\": true"
        recordedBody shouldContain "\"links\": \"suppress\""
        recordedBody shouldContain "\"delete-type\": \"every\""
      }
    }

    should("setFileACL build the correct URL for an absolute path with the characters to encode") {
      val filePath = "/u/SACLTST4/test file"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "setFileACL_success_path_encoding",
        { it.requestLine.contains("/zosmf/restfiles/fs/u/SACLTST4/") },
        {
          recordedRequest = it
          MockResponse().setResponseCode(200)
        }
      )

      val setFileACLRequest = ZosmfSetFileACLRequest(
        mockHttpConnection,
        filePath,
        ZosmfSetFileACLRequestBody(modify = "user:ANOTHER:rw-")
      )
      val setFileACLResponse = filesApi.setFileACL(setFileACLRequest)
        as? ZosmfSetFileACLResponse
        ?: fail("Should be instance of ${ZosmfSetFileACLResponse::class.java.name}")

      assertSoftly {
        setFileACLResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.requestLine shouldContain "/zosmf/restfiles/fs/u/SACLTST4/test%20file"
        recordedRequest?.requestLine?.contains("fs//") shouldBe false
      }
    }

    should("setFileACL fail because the file to operate is not found") {
      val filePath = "u/SACLTST5/not_found.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "setFileACL_fail_not_found",
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

      val setFileACLRequest = ZosmfSetFileACLRequest(
        mockHttpConnection,
        filePath,
        ZosmfSetFileACLRequestBody(modify = "user:ANOTHER:rw-")
      )
      val setFileACLResponse = filesApi.setFileACL(setFileACLRequest)
        as? ZosmfSetFileACLResponse
        ?: fail("Should be instance of ${ZosmfSetFileACLResponse::class.java.name}")

      assertSoftly {
        setFileACLResponse.status.type shouldBe StatusType.ERROR
        setFileACLResponse.status.text shouldContain "404"
        setFileACLResponse.status.text shouldContain "Path name not found"
      }
    }

    should("setFileACL fail because the access control list entries are malformed") {
      val filePath = "u/SACLTST6/test.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "setFileACL_fail_malformed_entries",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":6," +
                "\"rc\":-1," +
                "\"reason\":-1," +
                "\"message\":\"An invalid ACL entry was specified\"," +
                "\"details\":[\"FSUMB026 invalid ACL entry: not_an_entry\"]" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
            .setResponseCode(500)
        }
      )

      val setFileACLRequest = ZosmfSetFileACLRequest(
        mockHttpConnection,
        filePath,
        ZosmfSetFileACLRequestBody(set = "not_an_entry")
      )
      val setFileACLResponse = filesApi.setFileACL(setFileACLRequest)
        as? ZosmfSetFileACLResponse
        ?: fail("Should be instance of ${ZosmfSetFileACLResponse::class.java.name}")

      assertSoftly {
        setFileACLResponse.status.type shouldBe StatusType.ERROR
        setFileACLResponse.status.text shouldContain "500"
        setFileACLResponse.status.text shouldContain "FSUMB026 invalid ACL entry"
      }
    }
  }
})
