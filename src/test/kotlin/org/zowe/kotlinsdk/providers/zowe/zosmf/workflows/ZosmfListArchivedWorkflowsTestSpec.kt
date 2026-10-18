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
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfListArchivedWorkflowsRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfListArchivedWorkflowsResponse

class ZosmfListArchivedWorkflowsTestSpec : ShouldSpec({
  val workflowsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, WorkflowsAPI::class.java)
  val listPath = "$TEST_WORKFLOWS_PATH/archivedworkflows"

  afterEach {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("listArchivedWorkflows") {
    should("listArchivedWorkflows return all the archived workflows when no filters are provided") {
      zosmfMockResponseDispatcher.injectResolver(
        "listArchivedWorkflows_success_no_filters",
        { it.isWorkflowsRequest("GET", listPath) },
        { workflowsJsonResponse(200, readWorkflowsMock("listArchivedWorkflows")) }
      )

      val response = workflowsApi.listArchivedWorkflows(ZosmfListArchivedWorkflowsRequest(mockHttpConnection))
        as? ZosmfListArchivedWorkflowsResponse
        ?: fail("Should be instance of ${ZosmfListArchivedWorkflowsResponse::class.java.name}")

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        response.archivedWorkflows.size shouldBe 2
        response.archivedWorkflows[0].key shouldBe "2535b19e-a8c3-4a52-9d77-e30bb920f912"
        response.archivedWorkflows[0].name shouldBe "ZOWE_WF_OLD"
        response.archivedWorkflows[0].archivedInstanceUri shouldBe
          "/zosmf/workflow/rest/1.0/archivedworkflows/2535b19e-a8c3-4a52-9d77-e30bb920f912"
        response.archivedWorkflows[1].name shouldBe "ZOWE_WF_OLDER"
      }
    }

    should("listArchivedWorkflows send only the orderBy filter when it is the only one provided") {
      zosmfMockResponseDispatcher.injectResolver(
        "listArchivedWorkflows_success_order",
        { it.isWorkflowsRequest("GET", listPath, mapOf("orderBy" to "asc")) },
        { workflowsJsonResponse(200, readWorkflowsMock("listArchivedWorkflows")) }
      )

      val request = ZosmfListArchivedWorkflowsRequest(
        mockHttpConnection,
        orderBy = ZosmfListArchivedWorkflowsRequest.OrderBy.ASC
      )
      val response = workflowsApi.listArchivedWorkflows(request) as ZosmfListArchivedWorkflowsResponse

      response.status.type shouldBe StatusType.SUCCESS
    }

    should("listArchivedWorkflows send only the view filter when it is the only one provided") {
      zosmfMockResponseDispatcher.injectResolver(
        "listArchivedWorkflows_success_view",
        { it.isWorkflowsRequest("GET", listPath, mapOf("view" to "domain")) },
        { workflowsJsonResponse(200, readWorkflowsMock("listArchivedWorkflows")) }
      )

      val request = ZosmfListArchivedWorkflowsRequest(
        mockHttpConnection,
        view = ZosmfListArchivedWorkflowsRequest.View.DOMAIN
      )
      val response = workflowsApi.listArchivedWorkflows(request) as ZosmfListArchivedWorkflowsResponse

      response.status.type shouldBe StatusType.SUCCESS
    }

    should("listArchivedWorkflows send all the filters when all of them are provided") {
      zosmfMockResponseDispatcher.injectResolver(
        "listArchivedWorkflows_success_all",
        { it.isWorkflowsRequest("GET", listPath, mapOf("orderBy" to "desc", "view" to "user")) },
        { workflowsJsonResponse(200, readWorkflowsMock("listArchivedWorkflows")) }
      )

      val request = ZosmfListArchivedWorkflowsRequest(
        mockHttpConnection,
        orderBy = ZosmfListArchivedWorkflowsRequest.OrderBy.DESC,
        view = ZosmfListArchivedWorkflowsRequest.View.USER
      )
      val response = workflowsApi.listArchivedWorkflows(request) as ZosmfListArchivedWorkflowsResponse

      response.status.type shouldBe StatusType.SUCCESS
    }

    should("listArchivedWorkflows return an empty list when there are no archived workflows") {
      zosmfMockResponseDispatcher.injectResolver(
        "listArchivedWorkflows_success_empty",
        { it.isWorkflowsRequest("GET", listPath) },
        { workflowsJsonResponse(200, readWorkflowsMock("listArchivedWorkflowsEmpty")) }
      )

      val response = workflowsApi.listArchivedWorkflows(ZosmfListArchivedWorkflowsRequest(mockHttpConnection))
        as ZosmfListArchivedWorkflowsResponse

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        response.archivedWorkflows.shouldBeEmpty()
      }
    }

    should("listArchivedWorkflows return an error status with the z/OSMF message when the user is not a domain owner") {
      zosmfMockResponseDispatcher.injectResolver(
        "listArchivedWorkflows_fail_403",
        { it.isWorkflowsRequest("GET", listPath, mapOf("view" to "domain")) },
        { workflowsErrorResponse(403, "IZUWF0170E", "The user is not a domain owner.") }
      )

      val request = ZosmfListArchivedWorkflowsRequest(
        mockHttpConnection,
        view = ZosmfListArchivedWorkflowsRequest.View.DOMAIN
      )
      val response = workflowsApi.listArchivedWorkflows(request) as ZosmfListArchivedWorkflowsResponse

      assertSoftly {
        response.status.shouldBeWorkflowsError(403, "IZUWF0170E")
        response.archivedWorkflows.shouldBeEmpty()
      }
    }
  }
})
