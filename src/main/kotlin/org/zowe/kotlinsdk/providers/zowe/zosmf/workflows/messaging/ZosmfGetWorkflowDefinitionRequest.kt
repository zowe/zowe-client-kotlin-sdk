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
import org.zowe.kotlinsdk.core.workflows.api.messaging.GetWorkflowDefinitionRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfStatus
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.ZOSMF_WORKFLOWS_BASE_PATH
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.definitions.ZosmfWorkflowDefinition
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.produceReturnDataParam
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.produceWorkflowsResponseBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.workflowsJson

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-retrieve-workflow-definition">Retrieve a workflow definition</a>
 * @property definitionFilePath the definitionFilePath query param: a UNIX path name or a fully qualified data set name
 * @property workflowDefinitionFileSystem the workflowDefinitionFileSystem query param
 * @property isFetchSteps the "steps" value of the returnData query param
 * @property isFetchVariables the "variables" value of the returnData query param
 */
class ZosmfGetWorkflowDefinitionRequest(
  override val connection: HttpConnection,
  override val definitionFilePath: String,
  val workflowDefinitionFileSystem: String? = null,
  val isFetchSteps: Boolean = false,
  val isFetchVariables: Boolean = false
) : ZosmfHttpRequest, GetWorkflowDefinitionRequest {
  override val responseClass = ZosmfGetWorkflowDefinitionResponse::class.java

  override val method = HttpMethod.Get

  override val path = "$ZOSMF_WORKFLOWS_BASE_PATH/workflowDefinition"

  override val parameters = mapOf(
    "definitionFilePath" to definitionFilePath,
    "workflowDefinitionFileSystem" to workflowDefinitionFileSystem,
    "returnData" to produceReturnDataParam(isFetchSteps, isFetchVariables)
  )

  override val headers = ZosmfWorkflowsRequestHeaders()

  override val body = null

  /**
   * Produce a [ZosmfGetWorkflowDefinitionResponse] object basing on the [clientResponse] from the HTTP request
   * and [errorReport] if present
   */
  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfGetWorkflowDefinitionResponse {
    val definition = workflowsJson.decodeFromString<ZosmfWorkflowDefinition>(
      produceWorkflowsResponseBody(clientResponse)
    )
    return ZosmfGetWorkflowDefinitionResponse(ZosmfStatus(clientResponse.status, errorReport), definition)
  }
}
