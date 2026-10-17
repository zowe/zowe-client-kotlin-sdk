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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.core.workflows.api.messaging.ListArchivedWorkflowsRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfStatus
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.ZOSMF_WORKFLOWS_BASE_PATH
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.produceWorkflowsResponseBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.workflowsJson

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-list-archived-workflows-system">List the archived workflows for a system</a>
 * @property orderBy the orderBy query param to sort the archived workflows by time
 * @property view the view query param to select the archived workflows by
 */
class ZosmfListArchivedWorkflowsRequest(
  override val connection: HttpConnection,
  val orderBy: OrderBy? = null,
  val view: View? = null
) : ZosmfHttpRequest, ListArchivedWorkflowsRequest {
  override val responseClass = ZosmfListArchivedWorkflowsResponse::class.java

  override val method = HttpMethod.Get

  override val path = "$ZOSMF_WORKFLOWS_BASE_PATH/archivedworkflows"

  override val parameters = mapOf(
    "orderBy" to orderBy?.toString(),
    "view" to view?.toString()
  )

  override val headers = ZosmfWorkflowsRequestHeaders()

  override val body = null

  /**
   * Produce a [ZosmfListArchivedWorkflowsResponse] object basing on the [clientResponse] from the HTTP request
   * and [errorReport] if present
   */
  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfListArchivedWorkflowsResponse {
    val response = workflowsJson.decodeFromString<ZosmfListArchivedWorkflowsResponse>(
      produceWorkflowsResponseBody(clientResponse)
    )
    response.applyStatus(ZosmfStatus(clientResponse.status, errorReport))
    return response
  }

  @Serializable
  enum class OrderBy(private val type: String) {
    @SerialName("asc") ASC("asc"),
    @SerialName("desc") DESC("desc");

    override fun toString(): String {
      return this.type
    }
  }

  @Serializable
  enum class View(private val type: String) {
    @SerialName("user") USER("user"),
    @SerialName("domain") DOMAIN("domain");

    override fun toString(): String {
      return this.type
    }
  }
}
