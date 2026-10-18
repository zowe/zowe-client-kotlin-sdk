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
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfGetWorkflowPropertiesRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfGetWorkflowPropertiesResponse

class ZosmfGetWorkflowPropertiesTestSpec : ShouldSpec({
  val workflowsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, WorkflowsAPI::class.java)
  val workflowKey = "d043b5f1-adab-48e7-b7c3-d41cd95fa4b0"
  val workflowPath = "$TEST_WORKFLOWS_PATH/workflows/$workflowKey"

  afterEach {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("getWorkflowProperties") {
    should("getWorkflowProperties return the workflow properties without returnData when nothing additional is requested") {
      zosmfMockResponseDispatcher.injectResolver(
        "getWorkflowProperties_success",
        { it.isWorkflowsRequest("GET", workflowPath) },
        { workflowsJsonResponse(200, readWorkflowsMock("getWorkflowProperties")) }
      )

      val response = workflowsApi.getWorkflowProperties(ZosmfGetWorkflowPropertiesRequest(mockHttpConnection, workflowKey))
        as? ZosmfGetWorkflowPropertiesResponse
        ?: fail("Should be instance of ${ZosmfGetWorkflowPropertiesResponse::class.java.name}")
      val workflow = response.workflow

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        workflow.key shouldBe workflowKey
        workflow.name shouldBe "ZOWE_WF_CONFIGURE"
        workflow.owner shouldBe "IBMUSER"
        workflow.description shouldBe "Configure the Zowe instance"
        workflow.workflowId shouldBe "ZoweConfigure"
        workflow.workflowVersion shouldBe "1.0"
        workflow.definitionFileMd5 shouldBe "a2e4b6c8d0f1e3a5b7c9d1e3f5a7b9c1"
        workflow.vendor shouldBe "IBM"
        workflow.workflowArchiveSafId shouldBe "IBMUSER"
        workflow.system shouldBe "PLEX1.SYS1"
        workflow.jobsOutputDirectory shouldBe null
        workflow.category shouldBe ZosmfWorkflowCategory.CONFIGURATION
        workflow.productId shouldBe "5650-ZOS"
        workflow.productName shouldBe "z/OS"
        workflow.productVersion shouldBe "3.1"
        workflow.percentComplete shouldBe 25
        // Boolean in this response, but a string in the workflow definition response
        workflow.isCallable shouldBe "false"
        workflow.containsParallelSteps shouldBe false
        workflow.scope shouldBe ZosmfWorkflowScope.SYSTEM
        workflow.statusName shouldBe ZosmfWorkflowStatus.AUTOMATION_IN_PROGRESS
        workflow.deleteCompletedJobs shouldBe true
        workflow.autoDeleteOnCompletion shouldBe false
        workflow.access shouldBe ZosmfWorkflowAccess.RESTRICTED
        workflow.accountInfo shouldBe "ACCT#"
        workflow.jobStatement shouldBe "//IBMUSERA JOB (ACCT#),'ZOWE'"
        workflow.globalVariableGroup shouldBe "ZOWE"
        // A quoted boolean is accepted
        workflow.isInstanceVariableWithoutPrefix shouldBe true
        workflow.automationStatus?.startUser shouldBe "IBMUSER"
        // A number is accepted for the string property
        workflow.automationStatus?.startedTime shouldBe "1423679433714"
        workflow.automationStatus?.stoppedTime shouldBe null
        workflow.automationStatus?.currentStepName shouldBe "step1"
        workflow.automationStatus?.currentStepNumber shouldBe "1"
        workflow.automationStatus?.currentStepTitle shouldBe "Allocate data sets"
        workflow.automationStatus?.messageId shouldBe "IZUWF0145I"
        workflow.automationStatus?.messageText shouldBe "Automation is in progress."
        workflow.steps.shouldBeEmpty()
        workflow.variables.shouldBeEmpty()
      }
    }

    should("getWorkflowProperties request and parse the nested steps when steps are requested") {
      zosmfMockResponseDispatcher.injectResolver(
        "getWorkflowProperties_success_steps",
        { it.isWorkflowsRequest("GET", workflowPath, mapOf("returnData" to "steps")) },
        { workflowsJsonResponse(200, readWorkflowsMock("getWorkflowPropertiesWithSteps")) }
      )

      val request = ZosmfGetWorkflowPropertiesRequest(mockHttpConnection, workflowKey, isFetchSteps = true)
      val response = workflowsApi.getWorkflowProperties(request) as ZosmfGetWorkflowPropertiesResponse
      val parentStep = response.workflow.steps.first()

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        response.workflow.statusName shouldBe ZosmfWorkflowStatus.IN_PROGRESS
        response.workflow.steps.size shouldBe 1
        parentStep.name shouldBe "parent"
        parentStep.state shouldBe ZosmfWorkflowStep.ZosmfStepState.IN_PROGRESS
        parentStep.optional shouldBe false
        parentStep.prereqStep.shouldBeEmpty()
        parentStep.steps.size shouldBe 2
        parentStep.steps[0].name shouldBe "child1"
        parentStep.steps[0].title shouldBe "Allocate data sets"
        parentStep.steps[0].stepNumber shouldBe "1.1"
        parentStep.steps[0].state shouldBe ZosmfWorkflowStep.ZosmfStepState.COMPLETE_OVERRIDE
        parentStep.steps[0].owner shouldBe "IBMUSER"
        parentStep.steps[0].autoEnable shouldBe true
        parentStep.steps[0].isRestStep shouldBe false
        parentStep.steps[0].submitAs shouldBe "JCL"
        parentStep.steps[0].returnCode shouldBe "0000"
        parentStep.steps[0].weight shouldBe 3
        parentStep.steps[0].variableReferences.size shouldBe 1
        parentStep.steps[0].variableReferences[0].name shouldBe "hlq"
        parentStep.steps[0].variableReferences[0].scope shouldBe "instance"
        // A step state unknown to the SDK is coerced to null instead of failing the whole response
        parentStep.steps[1].state shouldBe null
        parentStep.steps[1].prereqStep shouldBe listOf("child1")
        response.workflow.variables.shouldBeEmpty()
      }
    }

    should("getWorkflowProperties request only the variables when only the variables are requested") {
      zosmfMockResponseDispatcher.injectResolver(
        "getWorkflowProperties_success_variables",
        { it.isWorkflowsRequest("GET", workflowPath, mapOf("returnData" to "variables")) },
        { workflowsJsonResponse(200, readWorkflowsMock("getWorkflowProperties")) }
      )

      val request = ZosmfGetWorkflowPropertiesRequest(mockHttpConnection, workflowKey, isFetchVariables = true)
      val response = workflowsApi.getWorkflowProperties(request) as ZosmfGetWorkflowPropertiesResponse

      response.status.type shouldBe StatusType.SUCCESS
    }

    should("getWorkflowProperties request and parse the steps and the variables when both are requested") {
      zosmfMockResponseDispatcher.injectResolver(
        "getWorkflowProperties_success_steps_variables",
        { it.isWorkflowsRequest("GET", workflowPath, mapOf("returnData" to "steps,variables")) },
        { workflowsJsonResponse(200, readWorkflowsMock("getWorkflowPropertiesWithStepsAndVariables")) }
      )

      val request = ZosmfGetWorkflowPropertiesRequest(
        mockHttpConnection,
        workflowKey,
        isFetchSteps = true,
        isFetchVariables = true
      )
      val response = workflowsApi.getWorkflowProperties(request) as ZosmfGetWorkflowPropertiesResponse
      val variables = response.workflow.variables

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        response.workflow.steps.size shouldBe 1
        response.workflow.steps[0].state shouldBe ZosmfWorkflowStep.ZosmfStepState.READY
        variables.size shouldBe 2
        variables[0].name shouldBe "hlq"
        variables[0].scope shouldBe "instance"
        variables[0].type shouldBe "string"
        variables[0].value shouldBe "IBMUSER.ZOWE"
        variables[0].visibility shouldBe "public"
        variables[1].name shouldBe "port"
        variables[1].value shouldBe "7554"
      }
    }

    should("getWorkflowProperties encode the workflow key in the URL path") {
      zosmfMockResponseDispatcher.injectResolver(
        "getWorkflowProperties_success_encoded_key",
        { it.isWorkflowsRequest("GET", "$TEST_WORKFLOWS_PATH/workflows/key%20with%20space") },
        { workflowsJsonResponse(200, readWorkflowsMock("getWorkflowProperties")) }
      )

      val request = ZosmfGetWorkflowPropertiesRequest(mockHttpConnection, "key with space")
      val response = workflowsApi.getWorkflowProperties(request) as ZosmfGetWorkflowPropertiesResponse

      response.status.type shouldBe StatusType.SUCCESS
    }

    should("getWorkflowProperties return an error status with the z/OSMF message when the workflow is not found") {
      val missingKey = "00000000-0000-0000-0000-000000000000"
      zosmfMockResponseDispatcher.injectResolver(
        "getWorkflowProperties_fail_404",
        { it.isWorkflowsRequest("GET", "$TEST_WORKFLOWS_PATH/workflows/$missingKey") },
        { workflowsErrorResponse(404, "IZUWF0101E", "The workflow key does not exist.") }
      )

      val response = workflowsApi.getWorkflowProperties(ZosmfGetWorkflowPropertiesRequest(mockHttpConnection, missingKey))
        as ZosmfGetWorkflowPropertiesResponse

      assertSoftly {
        response.status.shouldBeWorkflowsError(404, "IZUWF0101E")
        response.workflow.key shouldBe ""
        response.workflow.name shouldBe ""
      }
    }
  }
})
