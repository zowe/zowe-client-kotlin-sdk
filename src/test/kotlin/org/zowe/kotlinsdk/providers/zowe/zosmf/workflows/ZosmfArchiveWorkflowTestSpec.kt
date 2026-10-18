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
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.workflows.api.WorkflowsAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfArchiveWorkflowRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfArchiveWorkflowResponse

class ZosmfArchiveWorkflowTestSpec : ShouldSpec({
  val workflowsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, WorkflowsAPI::class.java)
  val workflowKey = "d043b5f1-adab-48e7-b7c3-d41cd95fa4b0"
  val archivePath = "$TEST_WORKFLOWS_PATH/workflows/$workflowKey/operations/archive"

  afterEach {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("archiveWorkflow") {
    should("archiveWorkflow return the key of the archived workflow") {
      zosmfMockResponseDispatcher.injectResolver(
        "archiveWorkflow_success",
        { it.isWorkflowsRequest("POST", archivePath) },
        { workflowsJsonResponse(201, readWorkflowsMock("archiveWorkflow")) }
      )

      val response = workflowsApi.archiveWorkflow(ZosmfArchiveWorkflowRequest(mockHttpConnection, workflowKey))
        as? ZosmfArchiveWorkflowResponse
        ?: fail("Should be instance of ${ZosmfArchiveWorkflowResponse::class.java.name}")

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        response.status.text shouldContain "201"
        response.workflowKey shouldBe "2535b19e-a8c3-4a52-9d77-e30bb920f912"
      }
    }

    should("archiveWorkflow return an error status with the z/OSMF message when the workflow is not eligible") {
      zosmfMockResponseDispatcher.injectResolver(
        "archiveWorkflow_fail_409",
        { it.isWorkflowsRequest("POST", archivePath) },
        { workflowsErrorResponse(409, "IZUWF0160E", "The workflow is not eligible for archiving.") }
      )

      val response = workflowsApi.archiveWorkflow(ZosmfArchiveWorkflowRequest(mockHttpConnection, workflowKey))
        as ZosmfArchiveWorkflowResponse

      assertSoftly {
        response.status.shouldBeWorkflowsError(409, "IZUWF0160E")
        response.workflowKey shouldBe ""
      }
    }
  }
})
