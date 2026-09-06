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
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfChangeFileTagRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfChangeFileTagRequestBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.ZosmfChangeFileTagResponse

class ZosmfChangeFileTagTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, FilesAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("changeFileTag") {
    should("changeFileTag set the text tag with the code set provided") {
      val filePath = "u/CFTTEST1/test.txt"
      var recordedRequest: RecordedRequest? = null
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileTag_success_set_text",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedRequest = it
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(200)
        }
      )

      val changeFileTagRequest = ZosmfChangeFileTagRequest(
        mockHttpConnection,
        filePath,
        ZosmfChangeFileTagRequestBody(
          ZosmfChangeFileTagRequestBody.Action.SET,
          ZosmfChangeFileTagRequestBody.Type.TEXT,
          "IBM-1047"
        )
      )
      val changeFileTagResponse = filesApi.changeFileTag(changeFileTagRequest)
        as? ZosmfChangeFileTagResponse
        ?: fail("Should be instance of ${ZosmfChangeFileTagResponse::class.java.name}")

      assertSoftly {
        changeFileTagResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.method shouldBe "PUT"
        recordedRequest?.getHeader("Content-Type") shouldContain "application/json"
        recordedBody shouldContain "\"action\": \"set\""
        recordedBody shouldContain "\"type\": \"text\""
        recordedBody shouldContain "\"codeSet\": \"IBM-1047\""
        recordedBody shouldContain "\"request\": \"chtag\""
        recordedBody shouldNotContain "\"recursive\""
      }
    }

    should("changeFileTag remove the tag of a USS file") {
      val filePath = "u/CFTTEST2/test.txt"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileTag_success_remove",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(200)
        }
      )

      val changeFileTagRequest = ZosmfChangeFileTagRequest(
        mockHttpConnection,
        filePath,
        ZosmfChangeFileTagRequestBody(ZosmfChangeFileTagRequestBody.Action.REMOVE)
      )
      val changeFileTagResponse = filesApi.changeFileTag(changeFileTagRequest)
        as? ZosmfChangeFileTagResponse
        ?: fail("Should be instance of ${ZosmfChangeFileTagResponse::class.java.name}")

      assertSoftly {
        changeFileTagResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"action\": \"remove\""
        recordedBody shouldNotContain "\"type\""
        recordedBody shouldNotContain "\"codeSet\""
      }
    }

    should("changeFileTag list the tag information of a USS file") {
      val filePath = "u/CFTTEST3/test.txt"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileTag_success_list",
        { it.requestLine.contains("/zosmf/restfiles/fs/$filePath") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse()
            .setBody("{\"stdout\":[\"t IBM-1047    T=on  /$filePath\"]}")
            .addHeader("Content-Type", "application/json")
            .setResponseCode(200)
        }
      )

      val changeFileTagRequest = ZosmfChangeFileTagRequest(
        mockHttpConnection,
        filePath,
        ZosmfChangeFileTagRequestBody(ZosmfChangeFileTagRequestBody.Action.LIST)
      )
      val changeFileTagResponse = filesApi.changeFileTag(changeFileTagRequest)
        as? ZosmfChangeFileTagResponse
        ?: fail("Should be instance of ${ZosmfChangeFileTagResponse::class.java.name}")

      assertSoftly {
        changeFileTagResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"action\": \"list\""
        changeFileTagResponse.currentTagInfo shouldBe "t IBM-1047    T=on  /$filePath"
      }
    }

    should("changeFileTag set the tag of a USS directory recursively with the links option provided") {
      val dirPath = "u/CFTTEST4/testdir"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileTag_success_recursive_links",
        { it.requestLine.contains("/zosmf/restfiles/fs/$dirPath") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(200)
        }
      )

      val changeFileTagRequest = ZosmfChangeFileTagRequest(
        mockHttpConnection,
        dirPath,
        ZosmfChangeFileTagRequestBody(
          ZosmfChangeFileTagRequestBody.Action.SET,
          ZosmfChangeFileTagRequestBody.Type.BINARY,
          links = ZosmfChangeFileTagRequestBody.Links.SUPPRESS,
          recursive = true
        )
      )
      val changeFileTagResponse = filesApi.changeFileTag(changeFileTagRequest)
        as? ZosmfChangeFileTagResponse
        ?: fail("Should be instance of ${ZosmfChangeFileTagResponse::class.java.name}")

      assertSoftly {
        changeFileTagResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"type\": \"binary\""
        recordedBody shouldContain "\"links\": \"suppress\""
        recordedBody shouldContain "\"recursive\": true"
      }
    }

    should("changeFileTag build the correct URL for an absolute path with the characters to encode") {
      val filePath = "/u/CFTTEST5/test dir"
      var recordedRequest: RecordedRequest? = null

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileTag_success_path_encoding",
        { it.requestLine.contains("/zosmf/restfiles/fs/u/CFTTEST5/") },
        {
          recordedRequest = it
          MockResponse().setResponseCode(200)
        }
      )

      val changeFileTagRequest = ZosmfChangeFileTagRequest(
        mockHttpConnection,
        filePath,
        ZosmfChangeFileTagRequestBody(ZosmfChangeFileTagRequestBody.Action.LIST)
      )
      val changeFileTagResponse = filesApi.changeFileTag(changeFileTagRequest)
        as? ZosmfChangeFileTagResponse
        ?: fail("Should be instance of ${ZosmfChangeFileTagResponse::class.java.name}")

      assertSoftly {
        changeFileTagResponse.status.type shouldBe StatusType.SUCCESS
        recordedRequest?.requestLine shouldContain "/zosmf/restfiles/fs/u/CFTTEST5/test%20dir"
        recordedRequest?.requestLine?.contains("fs//") shouldBe false
      }
    }

    should("changeFileTag fail because the item to change the tag of is not found") {
      val filePath = "u/CFTTEST6/not_found.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileTag_fail_not_found",
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

      val changeFileTagRequest = ZosmfChangeFileTagRequest(
        mockHttpConnection,
        filePath,
        ZosmfChangeFileTagRequestBody(ZosmfChangeFileTagRequestBody.Action.LIST)
      )
      val changeFileTagResponse = filesApi.changeFileTag(changeFileTagRequest)
        as? ZosmfChangeFileTagResponse
        ?: fail("Should be instance of ${ZosmfChangeFileTagResponse::class.java.name}")

      assertSoftly {
        changeFileTagResponse.status.type shouldBe StatusType.ERROR
        changeFileTagResponse.status.text shouldContain "404"
        changeFileTagResponse.status.text shouldContain "Path name not found"
      }
    }

    should("changeFileTag fail because there are no permissions to change the tag of the item") {
      val filePath = "u/CFTTEST7/forbidden.txt"

      zosmfMockResponseDispatcher.injectResolver(
        "changeFileTag_fail_no_permissions",
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

      val changeFileTagRequest = ZosmfChangeFileTagRequest(
        mockHttpConnection,
        filePath,
        ZosmfChangeFileTagRequestBody(
          ZosmfChangeFileTagRequestBody.Action.SET,
          ZosmfChangeFileTagRequestBody.Type.TEXT,
          "IBM-1047"
        )
      )
      val changeFileTagResponse = filesApi.changeFileTag(changeFileTagRequest)
        as? ZosmfChangeFileTagResponse
        ?: fail("Should be instance of ${ZosmfChangeFileTagResponse::class.java.name}")

      assertSoftly {
        changeFileTagResponse.status.type shouldBe StatusType.ERROR
        changeFileTagResponse.status.text shouldContain "403"
        changeFileTagResponse.status.text shouldContain "EDC5139I Operation not permitted"
      }
    }
  }
})
