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
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfChangeFileOwnerRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfChangeFileOwnerRequestBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfChangeFileOwnerResponse

class ZosmfChangeFileOwnerTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, FilesAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("changeFileOwner") {
    should("changeFileOwner change the owner of a USS file without the group provided") {
      val filePath = "u/CFOTEST1/test.txt"
      var recordedRequest: RecordedRequest? = null
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileOwner_success_owner_only",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedRequest = it
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(200)
        }
      )

      val changeFileOwnerRequest = ZosmfChangeFileOwnerRequest(
        mockHttpConnection,
        filePath,
        ZosmfChangeFileOwnerRequestBody("IBMUSER")
      )
      val changeFileOwnerResponse = filesApi.changeFileOwner(changeFileOwnerRequest)
        as? ZosmfChangeFileOwnerResponse
        ?: fail("Should be instance of ${ZosmfChangeFileOwnerResponse::class.java.name}")

      assertSoftly {
        changeFileOwnerResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.method shouldBe "PUT"
        recordedRequest?.getHeader("Content-Type") shouldContain "application/json"
        recordedBody shouldContain "\"owner\": \"IBMUSER\""
        recordedBody shouldContain "\"request\": \"chown\""
        recordedBody shouldNotContain "\"group\""
        recordedBody shouldNotContain "\"recursive\""
      }
    }

    should("changeFileOwner change the owner and the group of a USS file") {
      val filePath = "u/CFOTEST2/test.txt"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileOwner_success_owner_and_group",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(200)
        }
      )

      val changeFileOwnerRequest = ZosmfChangeFileOwnerRequest(
        mockHttpConnection,
        filePath,
        ZosmfChangeFileOwnerRequestBody("IBMUSER", "SYS1")
      )
      val changeFileOwnerResponse = filesApi.changeFileOwner(changeFileOwnerRequest)
        as? ZosmfChangeFileOwnerResponse
        ?: fail("Should be instance of ${ZosmfChangeFileOwnerResponse::class.java.name}")

      assertSoftly {
        changeFileOwnerResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"owner\": \"IBMUSER\""
        recordedBody shouldContain "\"group\": \"SYS1\""
      }
    }

    should("changeFileOwner change the owner of a USS directory recursively with the links option provided") {
      val dirPath = "u/CFOTEST3/testdir"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileOwner_success_recursive_links",
        { it.requestLine.contains("/zosmf/restfiles/fs/$dirPath") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(200)
        }
      )

      val changeFileOwnerRequest = ZosmfChangeFileOwnerRequest(
        mockHttpConnection,
        dirPath,
        ZosmfChangeFileOwnerRequestBody(
          "IBMUSER",
          "SYS1",
          links = ZosmfChangeFileOwnerRequestBody.Links.CHANGE,
          recursive = true
        )
      )
      val changeFileOwnerResponse = filesApi.changeFileOwner(changeFileOwnerRequest)
        as? ZosmfChangeFileOwnerResponse
        ?: fail("Should be instance of ${ZosmfChangeFileOwnerResponse::class.java.name}")

      assertSoftly {
        changeFileOwnerResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"links\": \"change\""
        recordedBody shouldContain "\"recursive\": true"
        recordedBody shouldContain "\"request\": \"chown\""
      }
    }

    should("changeFileOwner build the correct URL for an absolute path with the characters to encode") {
      val filePath = "/u/CFOTEST4/test dir"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileOwner_success_path_encoding",
        { it.requestLine.contains("/zosmf/restfiles/fs/u/CFOTEST4/") },
        {
          recordedRequest = it
          MockResponse().setResponseCode(200)
        }
      )

      val changeFileOwnerRequest = ZosmfChangeFileOwnerRequest(
        mockHttpConnection,
        filePath,
        ZosmfChangeFileOwnerRequestBody("IBMUSER")
      )
      val changeFileOwnerResponse = filesApi.changeFileOwner(changeFileOwnerRequest)
        as? ZosmfChangeFileOwnerResponse
        ?: fail("Should be instance of ${ZosmfChangeFileOwnerResponse::class.java.name}")

      assertSoftly {
        changeFileOwnerResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.requestLine shouldContain "/zosmf/restfiles/fs/u/CFOTEST4/test%20dir"
        recordedRequest?.requestLine?.contains("fs//") shouldBe false
      }
    }

    should("changeFileOwner fail because the item to change the owner of is not found") {
      val filePath = "u/CFOTEST5/not_found.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileOwner_fail_not_found",
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

      val changeFileOwnerRequest = ZosmfChangeFileOwnerRequest(
        mockHttpConnection,
        filePath,
        ZosmfChangeFileOwnerRequestBody("IBMUSER")
      )
      val changeFileOwnerResponse = filesApi.changeFileOwner(changeFileOwnerRequest)
        as? ZosmfChangeFileOwnerResponse
        ?: fail("Should be instance of ${ZosmfChangeFileOwnerResponse::class.java.name}")

      assertSoftly {
        changeFileOwnerResponse.status.type shouldBe StatusType.ERROR
        changeFileOwnerResponse.status.text shouldContain "404"
        changeFileOwnerResponse.status.text shouldContain "Path name not found"
      }
    }

    should("changeFileOwner fail because there are no permissions to change the owner of the item") {
      val filePath = "u/CFOTEST6/forbidden.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileOwner_fail_no_permissions",
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

      val changeFileOwnerRequest = ZosmfChangeFileOwnerRequest(
        mockHttpConnection,
        filePath,
        ZosmfChangeFileOwnerRequestBody("IBMUSER")
      )
      val changeFileOwnerResponse = filesApi.changeFileOwner(changeFileOwnerRequest)
        as? ZosmfChangeFileOwnerResponse
        ?: fail("Should be instance of ${ZosmfChangeFileOwnerResponse::class.java.name}")

      assertSoftly {
        changeFileOwnerResponse.status.type shouldBe StatusType.ERROR
        changeFileOwnerResponse.status.text shouldContain "403"
        changeFileOwnerResponse.status.text shouldContain "EDC5139I Operation not permitted"
      }
    }
  }
})
