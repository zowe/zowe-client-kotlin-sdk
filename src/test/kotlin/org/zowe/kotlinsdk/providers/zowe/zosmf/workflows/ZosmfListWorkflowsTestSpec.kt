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
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.definitions.ZosmfWorkflowStatus
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfListWorkflowsRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfListWorkflowsResponse

class ZosmfListWorkflowsTestSpec : ShouldSpec({
  val workflowsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, WorkflowsAPI::class.java)
  val listPath = "$TEST_WORKFLOWS_PATH/workflows"

  afterEach {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("listWorkflows") {
    should("listWorkflows return all the workflows when no filters are provided") {
      zosmfMockResponseDispatcher.injectResolver(
        "listWorkflows_success_no_filters",
        { it.isWorkflowsRequest("GET", listPath) },
        { workflowsJsonResponse(200, readWorkflowsMock("listWorkflows")) }
      )

      val response = workflowsApi.listWorkflows(ZosmfListWorkflowsRequest(mockHttpConnection))
        as? ZosmfListWorkflowsResponse
        ?: fail("Should be instance of ${ZosmfListWorkflowsResponse::class.java.name}")

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        response.workflows.size shouldBe 3
        response.workflows[0].key shouldBe "d043b5f1-adab-48e7-b7c3-d41cd95fa4b0"
        response.workflows[0].name shouldBe "ZOWE_WF_CONFIGURE"
        response.workflows[0].owner shouldBe "IBMUSER"
        response.workflows[0].description shouldBe "Configure the Zowe instance"
        response.workflows[0].workflowId shouldBe "ZoweConfigure"
        response.workflows[0].workflowVersion shouldBe "1.0"
        response.workflows[0].definitionFileMd5 shouldBe "a2e4b6c8d0f1e3a5b7c9d1e3f5a7b9c1"
        response.workflows[0].instanceUri shouldBe
          "/zosmf/workflow/rest/1.0/workflows/d043b5f1-adab-48e7-b7c3-d41cd95fa4b0"
        response.workflows[0].vendor shouldBe "IBM"
        response.workflows[0].access shouldBe ZosmfWorkflowAccess.PUBLIC
        response.workflows[1].access shouldBe ZosmfWorkflowAccess.PRIVATE
        // An access type unknown to the SDK is coerced to null instead of failing the whole response
        response.workflows[2].access shouldBe null
        response.workflows[2].description shouldBe null
      }
    }

    listOf(
      Triple("workflowName", "ZOWE.*", ZosmfListWorkflowsRequest(mockHttpConnection, workflowName = "ZOWE.*")),
      Triple(
        "category",
        "configuration",
        ZosmfListWorkflowsRequest(mockHttpConnection, category = ZosmfWorkflowCategory.CONFIGURATION)
      ),
      Triple("system", "PLEX1.SYS1", ZosmfListWorkflowsRequest(mockHttpConnection, system = "PLEX1.SYS1")),
      Triple(
        "statusName",
        "automation-in-progress",
        ZosmfListWorkflowsRequest(mockHttpConnection, statusName = ZosmfWorkflowStatus.AUTOMATION_IN_PROGRESS)
      ),
      Triple("owner", "IBMUSER", ZosmfListWorkflowsRequest(mockHttpConnection, owner = "IBMUSER")),
      Triple("vendor", "IBM", ZosmfListWorkflowsRequest(mockHttpConnection, vendor = "IBM"))
    ).forEach { (paramName, paramValue, request) ->
      should("listWorkflows send only the $paramName filter when it is the only one provided") {
        zosmfMockResponseDispatcher.injectResolver(
          "listWorkflows_success_$paramName",
          { it.isWorkflowsRequest("GET", listPath, mapOf(paramName to paramValue)) },
          { workflowsJsonResponse(200, readWorkflowsMock("listWorkflows")) }
        )

        val response = workflowsApi.listWorkflows(request) as ZosmfListWorkflowsResponse

        assertSoftly {
          response.status.type shouldBe StatusType.SUCCESS
          response.workflows.size shouldBe 3
        }
      }
    }

    should("listWorkflows send all the filters when all of them are provided") {
      zosmfMockResponseDispatcher.injectResolver(
        "listWorkflows_success_all_filters",
        {
          it.isWorkflowsRequest(
            "GET",
            listPath,
            mapOf(
              "workflowName" to "ZOWE.*",
              "category" to "general",
              "system" to "PLEX1.SYS1",
              "statusName" to "complete",
              "owner" to "IBMUSER",
              "vendor" to "IBM"
            )
          )
        },
        { workflowsJsonResponse(200, readWorkflowsMock("listWorkflows")) }
      )

      val request = ZosmfListWorkflowsRequest(
        mockHttpConnection,
        workflowName = "ZOWE.*",
        category = ZosmfWorkflowCategory.GENERAL,
        system = "PLEX1.SYS1",
        statusName = ZosmfWorkflowStatus.COMPLETE,
        owner = "IBMUSER",
        vendor = "IBM"
      )
      val response = workflowsApi.listWorkflows(request) as ZosmfListWorkflowsResponse

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        response.workflows.size shouldBe 3
        request.workflowName shouldBe "ZOWE.*"
        request.system shouldBe "PLEX1.SYS1"
        request.owner shouldBe "IBMUSER"
      }
    }

    should("listWorkflows return an empty list when no workflows match") {
      zosmfMockResponseDispatcher.injectResolver(
        "listWorkflows_success_empty",
        { it.isWorkflowsRequest("GET", listPath, mapOf("owner" to "NOBODY")) },
        { workflowsJsonResponse(200, readWorkflowsMock("listWorkflowsEmpty")) }
      )

      val response = workflowsApi.listWorkflows(ZosmfListWorkflowsRequest(mockHttpConnection, owner = "NOBODY"))
        as ZosmfListWorkflowsResponse

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        response.workflows.shouldBeEmpty()
      }
    }

    should("listWorkflows return an error status with the z/OSMF message when the request fails") {
      zosmfMockResponseDispatcher.injectResolver(
        "listWorkflows_fail_500",
        { it.isWorkflowsRequest("GET", listPath, mapOf("system" to "BROKEN")) },
        { workflowsErrorResponse(500, "IZUWF9999E", "Internal server error") }
      )

      val response = workflowsApi.listWorkflows(ZosmfListWorkflowsRequest(mockHttpConnection, system = "BROKEN"))
        as ZosmfListWorkflowsResponse

      assertSoftly {
        response.status.shouldBeWorkflowsError(500, "IZUWF9999E")
        response.workflows.shouldBeEmpty()
      }
    }
  }
})
