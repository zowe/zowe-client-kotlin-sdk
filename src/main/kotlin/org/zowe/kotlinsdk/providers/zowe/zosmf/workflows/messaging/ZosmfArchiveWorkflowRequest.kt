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
import org.zowe.kotlinsdk.core.workflows.api.messaging.ArchiveWorkflowRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfStatus
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.ZOSMF_WORKFLOWS_BASE_PATH
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.produceWorkflowKeyPathPart
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.produceWorkflowsResponseBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.workflowsJson

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-archive-workflow-instance">Archive a workflow instance</a>
 * @property workflowKey the workflowKey path param
 */
class ZosmfArchiveWorkflowRequest(
  override val connection: HttpConnection,
  override val workflowKey: String
) : ZosmfHttpRequest, ArchiveWorkflowRequest {
  override val responseClass = ZosmfArchiveWorkflowResponse::class.java

  override val method = HttpMethod.Post

  override val path = "$ZOSMF_WORKFLOWS_BASE_PATH/workflows/${produceWorkflowKeyPathPart(workflowKey)}/operations/archive"

  override val parameters = emptyMap<String, String>()

  override val headers = ZosmfWorkflowsRequestHeaders()

  override val body = null

  /**
   * Produce a [ZosmfArchiveWorkflowResponse] object basing on the [clientResponse] from the HTTP request
   * and [errorReport] if present
   */
  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfArchiveWorkflowResponse {
    val response = workflowsJson.decodeFromString<ZosmfArchiveWorkflowResponse>(
      produceWorkflowsResponseBody(clientResponse)
    )
    response.applyStatus(ZosmfStatus(clientResponse.status, errorReport))
    return response
  }
}
