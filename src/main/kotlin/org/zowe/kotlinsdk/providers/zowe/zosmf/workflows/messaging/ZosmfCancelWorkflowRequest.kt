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
import org.zowe.kotlinsdk.core.workflows.api.messaging.CancelWorkflowRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfStatus
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.ZOSMF_WORKFLOWS_BASE_PATH
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.produceWorkflowKeyPathPart
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.produceWorkflowsResponseBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.workflowsJson

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-cancel-workflow">Cancel a workflow</a>
 * @property workflowKey the workflowKey path param
 */
class ZosmfCancelWorkflowRequest(
  override val connection: HttpConnection,
  override val workflowKey: String
) : ZosmfHttpRequest, CancelWorkflowRequest {
  override val responseClass = ZosmfCancelWorkflowResponse::class.java

  override val method = HttpMethod.Put

  override val path = "$ZOSMF_WORKFLOWS_BASE_PATH/workflows/${produceWorkflowKeyPathPart(workflowKey)}/operations/cancel"

  override val parameters = emptyMap<String, String>()

  override val headers = ZosmfWorkflowsRequestHeaders()

  override val body = null

  /**
   * Produce a [ZosmfCancelWorkflowResponse] object basing on the [clientResponse] from the HTTP request
   * and [errorReport] if present
   */
  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfCancelWorkflowResponse {
    val response = workflowsJson.decodeFromString<ZosmfCancelWorkflowResponse>(
      produceWorkflowsResponseBody(clientResponse)
    )
    response.applyStatus(ZosmfStatus(clientResponse.status, errorReport))
    return response
  }
}
