/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.core.workflows.api.messaging.ListWorkflowsRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfStatus
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.ZOSMF_WORKFLOWS_BASE_PATH
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.definitions.ZosmfWorkflowCategory
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.definitions.ZosmfWorkflowStatus
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.produceWorkflowsResponseBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.workflowsJson

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-list-workflows-system-sysplex">List the workflows for a system or sysplex</a>
 * @property workflowName the workflowName query param (a regular expression can be used)
 * @property category the category query param
 * @property system the system query param
 * @property statusName the statusName query param
 * @property owner the owner query param
 * @property vendor the vendor query param
 */
class ZosmfListWorkflowsRequest(
  override val connection: HttpConnection,
  override val workflowName: String? = null,
  val category: ZosmfWorkflowCategory? = null,
  override val system: String? = null,
  val statusName: ZosmfWorkflowStatus? = null,
  override val owner: String? = null,
  val vendor: String? = null
) : ZosmfHttpRequest, ListWorkflowsRequest {
  override val responseClass = ZosmfListWorkflowsResponse::class.java

  override val method = HttpMethod.Get

  override val path = "$ZOSMF_WORKFLOWS_BASE_PATH/workflows"

  override val parameters = mapOf(
    "workflowName" to workflowName,
    "category" to category?.toString(),
    "system" to system,
    "statusName" to statusName?.toString(),
    "owner" to owner,
    "vendor" to vendor
  )

  override val headers = ZosmfWorkflowsRequestHeaders()

  override val body = null

  /**
   * Produce a [ZosmfListWorkflowsResponse] object basing on the [clientResponse] from the HTTP request
   * and [errorReport] if present
   */
  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfListWorkflowsResponse {
    val response = workflowsJson.decodeFromString<ZosmfListWorkflowsResponse>(
      produceWorkflowsResponseBody(clientResponse)
    )
    response.applyStatus(ZosmfStatus(clientResponse.status, errorReport))
    return response
  }
}
