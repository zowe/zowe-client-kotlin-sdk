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
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfDeleteArchivedWorkflowRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfDeleteArchivedWorkflowResponse

class ZosmfDeleteArchivedWorkflowTestSpec : ShouldSpec({
  val workflowsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, WorkflowsAPI::class.java)
  val workflowKey = "2535b19e-a8c3-4a52-9d77-e30bb920f912"
  val deletePath = "$TEST_WORKFLOWS_PATH/archivedworkflows/$workflowKey"

  afterEach {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("deleteArchivedWorkflow") {
    should("deleteArchivedWorkflow succeed with no content") {
      zosmfMockResponseDispatcher.injectResolver(
        "deleteArchivedWorkflow_success",
        { it.isWorkflowsRequest("DELETE", deletePath) },
        { MockResponse().setResponseCode(204) }
      )

      val response = workflowsApi.deleteArchivedWorkflow(
        ZosmfDeleteArchivedWorkflowRequest(mockHttpConnection, workflowKey)
      ) as? ZosmfDeleteArchivedWorkflowResponse
        ?: fail("Should be instance of ${ZosmfDeleteArchivedWorkflowResponse::class.java.name}")

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        response.status.text shouldContain "204"
      }
    }

    should("deleteArchivedWorkflow return an error status with the z/OSMF message when the archived workflow is not found") {
      zosmfMockResponseDispatcher.injectResolver(
        "deleteArchivedWorkflow_fail_404",
        { it.isWorkflowsRequest("DELETE", deletePath) },
        { workflowsErrorResponse(404, "IZUWF0101E", "The workflow key does not exist.") }
      )

      val response = workflowsApi.deleteArchivedWorkflow(
        ZosmfDeleteArchivedWorkflowRequest(mockHttpConnection, workflowKey)
      ) as ZosmfDeleteArchivedWorkflowResponse

      response.status.shouldBeWorkflowsError(404, "IZUWF0101E")
    }
  }
})
