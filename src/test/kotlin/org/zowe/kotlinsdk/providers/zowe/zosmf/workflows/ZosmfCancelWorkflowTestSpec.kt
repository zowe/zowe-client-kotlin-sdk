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
import io.kotest.matchers.shouldBe
import io.kotest.provided.ProjectConfig.mockHttpConnection
import io.kotest.provided.ProjectConfig.zosmfMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.workflows.api.WorkflowsAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfCancelWorkflowRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfCancelWorkflowResponse

class ZosmfCancelWorkflowTestSpec : ShouldSpec({
  val workflowsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, WorkflowsAPI::class.java)
  val workflowKey = "d043b5f1-adab-48e7-b7c3-d41cd95fa4b0"
  val cancelPath = "$TEST_WORKFLOWS_PATH/workflows/$workflowKey/operations/cancel"

  afterEach {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("cancelWorkflow") {
    should("cancelWorkflow return the new name of the canceled workflow") {
      var recordedBodySize: Long? = null

      zosmfMockResponseDispatcher.injectResolver(
        "cancelWorkflow_success",
        { it.isWorkflowsRequest("PUT", cancelPath) },
        {
          recordedBodySize = it.bodySize
          workflowsJsonResponse(200, readWorkflowsMock("cancelWorkflow"))
        }
      )

      val response = workflowsApi.cancelWorkflow(ZosmfCancelWorkflowRequest(mockHttpConnection, workflowKey))
        as? ZosmfCancelWorkflowResponse
        ?: fail("Should be instance of ${ZosmfCancelWorkflowResponse::class.java.name}")

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        response.workflowName shouldBe "ZOWE_WF_CONFIGURE|Canceled|1423679433714"
        // The cancel request has no body
        recordedBodySize shouldBe 0L
      }
    }

    should("cancelWorkflow return an error status with the z/OSMF message when the requester is not the owner") {
      zosmfMockResponseDispatcher.injectResolver(
        "cancelWorkflow_fail_403",
        { it.isWorkflowsRequest("PUT", cancelPath) },
        { workflowsErrorResponse(403, "IZUWF0103E", "The requester is not the workflow owner.") }
      )

      val response = workflowsApi.cancelWorkflow(ZosmfCancelWorkflowRequest(mockHttpConnection, workflowKey))
        as ZosmfCancelWorkflowResponse

      assertSoftly {
        response.status.shouldBeWorkflowsError(403, "IZUWF0103E")
        response.workflowName shouldBe ""
      }
    }
  }
})
