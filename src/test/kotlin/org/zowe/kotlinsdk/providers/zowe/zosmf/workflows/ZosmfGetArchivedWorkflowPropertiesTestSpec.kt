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
import io.kotest.provided.ProjectConfig.mockHttpConnection
import io.kotest.provided.ProjectConfig.zosmfMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.workflows.api.WorkflowsAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.definitions.ZosmfWorkflowAccess
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.definitions.ZosmfWorkflowCategory
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.definitions.ZosmfWorkflowScope
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.definitions.ZosmfWorkflowStatus
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.definitions.ZosmfWorkflowStep
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfGetArchivedWorkflowPropertiesRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfGetArchivedWorkflowPropertiesResponse

class ZosmfGetArchivedWorkflowPropertiesTestSpec : ShouldSpec({
  val workflowsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, WorkflowsAPI::class.java)
  val workflowKey = "2535b19e-a8c3-4a52-9d77-e30bb920f912"
  val archivedPath = "$TEST_WORKFLOWS_PATH/archivedworkflows/$workflowKey"

  afterEach {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("getArchivedWorkflowProperties") {
    should("getArchivedWorkflowProperties return the archived workflow properties without returnData when nothing additional is requested") {
      zosmfMockResponseDispatcher.injectResolver(
        "getArchivedWorkflowProperties_success",
        { it.isWorkflowsRequest("GET", archivedPath) },
        { workflowsJsonResponse(200, readWorkflowsMock("getArchivedWorkflowProperties")) }
      )

      val response = workflowsApi.getArchivedWorkflowProperties(
        ZosmfGetArchivedWorkflowPropertiesRequest(mockHttpConnection, workflowKey)
      ) as? ZosmfGetArchivedWorkflowPropertiesResponse
        ?: fail("Should be instance of ${ZosmfGetArchivedWorkflowPropertiesResponse::class.java.name}")
      val archived = response.archivedWorkflow

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        archived.key shouldBe workflowKey
        archived.name shouldBe "ZOWE_WF_OLD"
        archived.owner shouldBe "IBMUSER"
        archived.description shouldBe "Configure the Zowe instance"
        archived.workflowId shouldBe "ZoweConfigure"
        archived.workflowVersion shouldBe "1.0"
        archived.definitionFileMd5 shouldBe "a2e4b6c8d0f1e3a5b7c9d1e3f5a7b9c1"
        archived.vendor shouldBe "IBM"
        archived.system shouldBe "PLEX1.SYS1"
        archived.category shouldBe ZosmfWorkflowCategory.GENERAL
        archived.percentComplete shouldBe 100
        archived.isCallable shouldBe "false"
        archived.containsParallelSteps shouldBe false
        archived.scope shouldBe ZosmfWorkflowScope.NONE
        archived.statusName shouldBe ZosmfWorkflowStatus.COMPLETE
        archived.deleteCompletedJobs shouldBe false
        archived.autoDeleteOnCompletion shouldBe false
        archived.access shouldBe ZosmfWorkflowAccess.PUBLIC
        archived.archivedTime shouldBe "2026-10-01T12:00:00.000Z"
        archived.steps.shouldBeEmpty()
        archived.variables.shouldBeEmpty()
      }
    }

    should("getArchivedWorkflowProperties request only the steps when only the steps are requested") {
      zosmfMockResponseDispatcher.injectResolver(
        "getArchivedWorkflowProperties_success_steps",
        { it.isWorkflowsRequest("GET", archivedPath, mapOf("returnData" to "steps")) },
        { workflowsJsonResponse(200, readWorkflowsMock("getArchivedWorkflowProperties")) }
      )

      val request = ZosmfGetArchivedWorkflowPropertiesRequest(mockHttpConnection, workflowKey, isFetchSteps = true)
      val response = workflowsApi.getArchivedWorkflowProperties(request) as ZosmfGetArchivedWorkflowPropertiesResponse

      response.status.type shouldBe StatusType.SUCCESS
    }

    should("getArchivedWorkflowProperties request and parse the steps and the variables when both are requested") {
      zosmfMockResponseDispatcher.injectResolver(
        "getArchivedWorkflowProperties_success_steps_variables",
        { it.isWorkflowsRequest("GET", archivedPath, mapOf("returnData" to "steps,variables")) },
        { workflowsJsonResponse(200, readWorkflowsMock("getArchivedWorkflowPropertiesWithStepsAndVariables")) }
      )

      val request = ZosmfGetArchivedWorkflowPropertiesRequest(
        mockHttpConnection,
        workflowKey,
        isFetchSteps = true,
        isFetchVariables = true
      )
      val response = workflowsApi.getArchivedWorkflowProperties(request) as ZosmfGetArchivedWorkflowPropertiesResponse

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        response.archivedWorkflow.steps.size shouldBe 1
        response.archivedWorkflow.steps[0].state shouldBe ZosmfWorkflowStep.ZosmfStepState.COMPLETE
        response.archivedWorkflow.variables.size shouldBe 1
        response.archivedWorkflow.variables[0].value shouldBe "IBMUSER.ZOWE"
      }
    }

    should("getArchivedWorkflowProperties return an error status with the z/OSMF message when the archived workflow is not found") {
      val missingKey = "00000000-0000-0000-0000-000000000000"
      zosmfMockResponseDispatcher.injectResolver(
        "getArchivedWorkflowProperties_fail_404",
        { it.isWorkflowsRequest("GET", "$TEST_WORKFLOWS_PATH/archivedworkflows/$missingKey") },
        { workflowsErrorResponse(404, "IZUWF0101E", "The workflow key does not exist.") }
      )

      val response = workflowsApi.getArchivedWorkflowProperties(
        ZosmfGetArchivedWorkflowPropertiesRequest(mockHttpConnection, missingKey)
      ) as ZosmfGetArchivedWorkflowPropertiesResponse

      assertSoftly {
        response.status.shouldBeWorkflowsError(404, "IZUWF0101E")
        response.archivedWorkflow.key shouldBe ""
      }
    }
  }
})
