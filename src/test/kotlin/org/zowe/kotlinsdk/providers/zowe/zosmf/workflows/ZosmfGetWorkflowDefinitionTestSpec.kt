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
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.definitions.ZosmfWorkflowCategory
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.definitions.ZosmfWorkflowScope
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfGetWorkflowDefinitionRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfGetWorkflowDefinitionResponse

class ZosmfGetWorkflowDefinitionTestSpec : ShouldSpec({
  val workflowsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, WorkflowsAPI::class.java)
  val definitionPath = "$TEST_WORKFLOWS_PATH/workflowDefinition"
  val definitionFile = "/u/ibmuser/workflow.xml"

  afterEach {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("getWorkflowDefinition") {
    should("getWorkflowDefinition send only the definition file path when nothing else is provided") {
      zosmfMockResponseDispatcher.injectResolver(
        "getWorkflowDefinition_success_required",
        { it.isWorkflowsRequest("GET", definitionPath, mapOf("definitionFilePath" to definitionFile)) },
        { workflowsJsonResponse(200, readWorkflowsMock("getWorkflowDefinition")) }
      )

      val request = ZosmfGetWorkflowDefinitionRequest(mockHttpConnection, definitionFile)
      val response = workflowsApi.getWorkflowDefinition(request) as? ZosmfGetWorkflowDefinitionResponse
        ?: fail("Should be instance of ${ZosmfGetWorkflowDefinitionResponse::class.java.name}")
      val definition = response.definition

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        request.definitionFilePath shouldBe definitionFile
        definition.id shouldBe "ZoweConfigure"
        definition.version shouldBe "1.0"
        definition.defaultName shouldBe "Configure Zowe"
        definition.description shouldBe "Configure the Zowe instance"
        definition.vendor shouldBe "IBM"
        definition.definitionFileMd5 shouldBe "a2e4b6c8d0f1e3a5b7c9d1e3f5a7b9c1"
        definition.isCallable shouldBe "sysplex"
        definition.containsParallelSteps shouldBe false
        definition.scope shouldBe ZosmfWorkflowScope.SYSPLEX
        definition.jobsOutputDirectory shouldBe "/u/ibmuser/jobs"
        definition.category shouldBe ZosmfWorkflowCategory.CONFIGURATION
        definition.productId shouldBe "5650-ZOS"
        definition.productName shouldBe "z/OS"
        definition.productVersion shouldBe "3.1"
        definition.globalVariableGroup shouldBe "ZOWE"
        definition.isInstanceVariableWithoutPrefix shouldBe false
        definition.steps.shouldBeEmpty()
        definition.variables.shouldBeEmpty()
      }
    }

    should("getWorkflowDefinition send the definition file system when it is provided") {
      zosmfMockResponseDispatcher.injectResolver(
        "getWorkflowDefinition_success_file_system",
        {
          it.isWorkflowsRequest(
            "GET",
            definitionPath,
            mapOf("definitionFilePath" to "SYS1.WORKFLOW(ZOWE)", "workflowDefinitionFileSystem" to "PLEX1.SYS2")
          )
        },
        { workflowsJsonResponse(200, readWorkflowsMock("getWorkflowDefinition")) }
      )

      val request = ZosmfGetWorkflowDefinitionRequest(
        mockHttpConnection,
        "SYS1.WORKFLOW(ZOWE)",
        workflowDefinitionFileSystem = "PLEX1.SYS2"
      )
      val response = workflowsApi.getWorkflowDefinition(request) as ZosmfGetWorkflowDefinitionResponse

      response.status.type shouldBe StatusType.SUCCESS
    }

    should("getWorkflowDefinition request and parse the steps and the variables when both are requested") {
      zosmfMockResponseDispatcher.injectResolver(
        "getWorkflowDefinition_success_steps_variables",
        {
          it.isWorkflowsRequest(
            "GET",
            definitionPath,
            mapOf("definitionFilePath" to definitionFile, "returnData" to "steps,variables")
          )
        },
        { workflowsJsonResponse(200, readWorkflowsMock("getWorkflowDefinitionWithStepsAndVariables")) }
      )

      val request = ZosmfGetWorkflowDefinitionRequest(
        mockHttpConnection,
        definitionFile,
        isFetchSteps = true,
        isFetchVariables = true
      )
      val response = workflowsApi.getWorkflowDefinition(request) as ZosmfGetWorkflowDefinitionResponse
      val steps = response.definition.steps
      val variables = response.definition.variables

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        steps.size shouldBe 3
        steps[0].name shouldBe "step1"
        // A leaf step returns "steps": null, which is coerced to an empty list
        steps[0].steps.shouldBeEmpty()
        steps[0].title shouldBe "Allocate data sets"
        steps[0].description shouldBe "Allocates the data sets for Zowe"
        steps[0].optional shouldBe false
        steps[0].autoEnable shouldBe true
        steps[0].isRestStep shouldBe false
        steps[0].submitAs shouldBe "JCL"
        steps[0].skills shouldBe "System programmer"
        steps[0].weight shouldBe 2
        steps[0].variableSpecifications.size shouldBe 1
        steps[0].variableSpecifications[0].name shouldBe "hlq"
        steps[0].variableSpecifications[0].scope shouldBe "instance"
        steps[0].variableSpecifications[0].required shouldBe true
        steps[1].prereqStep shouldBe listOf("step1")
        steps[1].calledWorkflowId shouldBe "ZoweSub"
        steps[1].calledWorkflowVersion shouldBe "1.0"
        steps[1].calledWorkflowDefinitionFile shouldBe "/u/ibmuser/sub.xml"
        steps[2].steps.size shouldBe 2
        steps[2].steps[0].name shouldBe "step3a"
        steps[2].steps[0].steps.shouldBeEmpty()
        steps[2].steps[1].title shouldBe "Verify the started task"
        steps[2].steps[1].prereqStep shouldBe listOf("step3a")
        variables.size shouldBe 2
        variables[0].name shouldBe "hlq"
        variables[0].scope shouldBe "instance"
        variables[0].abstract shouldBe "High-level qualifier"
        variables[0].category shouldBe "General"
        variables[0].default shouldBe "IBMUSER"
        variables[0].description shouldBe "The HLQ for the data sets"
        variables[0].type shouldBe "string"
        variables[0].visibility shouldBe "public"
        variables[0].maxLength shouldBe 8
        variables[0].minLength shouldBe 1
        variables[0].regularExpression shouldBe "[A-Z]+"
        variables[0].validationType shouldBe "DSNAME"
        variables[0].exposeToUser shouldBe true
        variables[0].promptAtCreate shouldBe true
        variables[0].requiredAtCreate shouldBe true
        variables[0].valueMustBeChoice shouldBe false
        variables[1].choice shouldBe listOf("dev", "prod")
        variables[1].valueMustBeChoice shouldBe true
      }
    }

    should("getWorkflowDefinition return an error status with the z/OSMF message when the definition file is not found") {
      zosmfMockResponseDispatcher.injectResolver(
        "getWorkflowDefinition_fail_404",
        { it.isWorkflowsRequest("GET", definitionPath, mapOf("definitionFilePath" to "/u/missing.xml")) },
        { workflowsErrorResponse(404, "IZUWF0106E", "The workflow definition file is not found.") }
      )

      val response = workflowsApi.getWorkflowDefinition(
        ZosmfGetWorkflowDefinitionRequest(mockHttpConnection, "/u/missing.xml")
      ) as ZosmfGetWorkflowDefinitionResponse

      assertSoftly {
        response.status.shouldBeWorkflowsError(404, "IZUWF0106E")
        response.definition.id shouldBe ""
      }
    }
  }
})
