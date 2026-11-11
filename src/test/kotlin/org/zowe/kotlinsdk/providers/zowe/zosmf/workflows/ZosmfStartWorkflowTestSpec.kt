/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.workflows

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.provided.ProjectConfig.mockHttpConnection
import io.kotest.provided.ProjectConfig.zosmfMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.workflows.api.WorkflowsAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfStartWorkflowRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfStartWorkflowRequestBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfStartWorkflowResponse

class ZosmfStartWorkflowTestSpec : ShouldSpec({
  val workflowsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, WorkflowsAPI::class.java)
  val workflowKey = "d043b5f1-adab-48e7-b7c3-d41cd95fa4b0"
  val startPath = "$TEST_WORKFLOWS_PATH/workflows/$workflowKey/operations/start"

  afterEach {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("startWorkflow") {
    should("startWorkflow send an empty JSON object when no automation parameters are provided") {
      var recordedRequest: RecordedRequest? = null
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "startWorkflow_success_defaults",
        { it.isWorkflowsRequest("PUT", startPath) },
        {
          recordedRequest = it
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(202)
        }
      )

      val response = workflowsApi.startWorkflow(ZosmfStartWorkflowRequest(mockHttpConnection, workflowKey))
        as? ZosmfStartWorkflowResponse
        ?: fail("Should be instance of ${ZosmfStartWorkflowResponse::class.java.name}")

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        response.status.text shouldContain "202"
        recordedRequest?.getHeader("Content-Type") shouldContain "application/json"
        recordedBody.toJsonObject().keys.shouldBeEmpty()
      }
    }

    should("startWorkflow send all the automation parameters with the z/OSMF names when all of them are provided") {
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "startWorkflow_success_all",
        { it.isWorkflowsRequest("PUT", startPath) },
        {
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(202)
        }
      )

      val request = ZosmfStartWorkflowRequest(
        mockHttpConnection,
        workflowKey,
        ZosmfStartWorkflowRequestBody(
          resolveConflictByUsing = ZosmfStartWorkflowRequestBody.ResolveConflictByUsing.LEAVE_CONFLICT,
          stepName = "step2",
          performSubsequent = false,
          notificationUrl = "https://example.com/notify",
          targetSystemUser = "REMOTEU",
          targetSystemPassword = "REMOTEP"
        )
      )
      val response = workflowsApi.startWorkflow(request) as ZosmfStartWorkflowResponse
      val body = recordedBody.toJsonObject()

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        body.keys shouldBe setOf(
          "resolveConflictByUsing", "stepName", "performSubsequent", "notificationUrl",
          "targetSystemuid", "targetSystempwd"
        )
        body["resolveConflictByUsing"]?.jsonPrimitive?.content shouldBe "leaveConflict"
        body["stepName"]?.jsonPrimitive?.content shouldBe "step2"
        body["performSubsequent"]?.jsonPrimitive?.boolean shouldBe false
        body["notificationUrl"]?.jsonPrimitive?.content shouldBe "https://example.com/notify"
        body["targetSystemuid"]?.jsonPrimitive?.content shouldBe "REMOTEU"
        body["targetSystempwd"]?.jsonPrimitive?.content shouldBe "REMOTEP"
      }
    }

    should("startWorkflow return an error status with the z/OSMF message when the automation is already in progress") {
      zosmfMockResponseDispatcher.injectResolver(
        "startWorkflow_fail_409",
        { it.isWorkflowsRequest("PUT", startPath) },
        { workflowsErrorResponse(409, "IZUWF0145E", "The workflow automation is in progress.") }
      )

      val response = workflowsApi.startWorkflow(ZosmfStartWorkflowRequest(mockHttpConnection, workflowKey))
        as ZosmfStartWorkflowResponse

      response.status.shouldBeWorkflowsError(409, "IZUWF0145E")
    }

    should("startWorkflow request body not expose the target system password in its string representation") {
      val body = ZosmfStartWorkflowRequestBody(targetSystemPassword = "SECRETPWD")

      body.toString() shouldNotContain "SECRETPWD"
    }
  }
})
