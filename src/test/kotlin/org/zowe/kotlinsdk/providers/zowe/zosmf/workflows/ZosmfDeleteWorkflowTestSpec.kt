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
import io.kotest.matchers.string.shouldContain
import io.kotest.provided.ProjectConfig.mockHttpConnection
import io.kotest.provided.ProjectConfig.zosmfMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import okhttp3.mockwebserver.MockResponse
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.workflows.api.WorkflowsAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfDeleteWorkflowRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfDeleteWorkflowResponse

class ZosmfDeleteWorkflowTestSpec : ShouldSpec({
  val workflowsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, WorkflowsAPI::class.java)
  val workflowKey = "d043b5f1-adab-48e7-b7c3-d41cd95fa4b0"
  val deletePath = "$TEST_WORKFLOWS_PATH/workflows/$workflowKey"

  afterEach {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("deleteWorkflow") {
    should("deleteWorkflow succeed with no content") {
      zosmfMockResponseDispatcher.injectResolver(
        "deleteWorkflow_success",
        { it.isWorkflowsRequest("DELETE", deletePath) },
        { MockResponse().setResponseCode(204) }
      )

      val response = workflowsApi.deleteWorkflow(ZosmfDeleteWorkflowRequest(mockHttpConnection, workflowKey))
        as? ZosmfDeleteWorkflowResponse
        ?: fail("Should be instance of ${ZosmfDeleteWorkflowResponse::class.java.name}")

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        response.status.text shouldContain "204"
      }
    }

    should("deleteWorkflow return an error status with the z/OSMF message when the automation is in progress") {
      zosmfMockResponseDispatcher.injectResolver(
        "deleteWorkflow_fail_409",
        { it.isWorkflowsRequest("DELETE", deletePath) },
        { workflowsErrorResponse(409, "IZUWF0145E", "The workflow automation is in progress.") }
      )

      val response = workflowsApi.deleteWorkflow(ZosmfDeleteWorkflowRequest(mockHttpConnection, workflowKey))
        as ZosmfDeleteWorkflowResponse

      response.status.shouldBeWorkflowsError(409, "IZUWF0145E")
    }
  }
})
