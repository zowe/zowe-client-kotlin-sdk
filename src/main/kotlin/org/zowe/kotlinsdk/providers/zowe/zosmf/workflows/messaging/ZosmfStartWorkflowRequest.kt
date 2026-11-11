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
import org.zowe.kotlinsdk.core.workflows.api.messaging.StartWorkflowRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfStatus
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.ZOSMF_WORKFLOWS_BASE_PATH
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.produceWorkflowKeyPathPart

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-start-workflow">Start a workflow</a>
 * @property workflowKey the workflowKey path param
 * @property body the request body with the automation parameters (all of them are optional)
 */
class ZosmfStartWorkflowRequest(
  override val connection: HttpConnection,
  override val workflowKey: String,
  override val body: ZosmfStartWorkflowRequestBody = ZosmfStartWorkflowRequestBody()
) : ZosmfHttpRequest, StartWorkflowRequest {
  override val responseClass = ZosmfStartWorkflowResponse::class.java

  override val method = HttpMethod.Put

  override val path = "$ZOSMF_WORKFLOWS_BASE_PATH/workflows/${produceWorkflowKeyPathPart(workflowKey)}/operations/start"

  override val parameters = emptyMap<String, String>()

  override val headers = ZosmfWorkflowsRequestHeaders(isJsonBody = true)

  /**
   * Produce a [ZosmfStartWorkflowResponse] object basing on the [clientResponse] from the HTTP request
   * and [errorReport] if present. A successful response has no body
   */
  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfStartWorkflowResponse {
    return ZosmfStartWorkflowResponse(ZosmfStatus(clientResponse.status, errorReport))
  }
}
