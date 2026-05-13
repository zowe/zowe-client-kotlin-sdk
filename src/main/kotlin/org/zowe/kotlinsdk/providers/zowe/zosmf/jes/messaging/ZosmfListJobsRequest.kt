/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.jes.messaging

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.core.jes.api.messaging.ListJobsRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfStatus
import org.zowe.kotlinsdk.providers.zowe.zosmf.jes.definitions.ZosmfJobItem

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-jobs-owner-prefix-job-id">List jobs by owner, prefix, or job ID</a>
 * @property jobPrefix the prefix query param (used when jobId is empty)
 * @property jobId the jobid query param (mutually exclusive with prefix + owner)
 * @property jobOwner the owner query param (used when jobId is empty)
 * @property maxJobsToReturn the max-jobs query param
 * @property isFetchExecData the exec-data query param
 */
class ZosmfListJobsRequest(
  override val connection: HttpConnection,
  override val jobPrefix: String,
  override val jobId: String,
  override val jobOwner: String,
  override val maxJobsToReturn: Int = 1000,
  val isFetchExecData: Boolean = false,
  override val headers: ZosmfGetJobRequestHeaders = ZosmfGetJobRequestHeaders()
) : ZosmfHttpRequest, ListJobsRequest {
  override val responseClass = ZosmfListJobsResponse::class.java

  override val method = HttpMethod.Get

  override val body = null

  override val path = "/zosmf/restjobs/jobs"

  // z/OSMF constraint: cannot combine prefix + owner with jobId in one request
  override val parameters: Map<String, String?> = buildMap {
    if (jobId.isNotEmpty()) {
      put("jobid", jobId)
    } else {
      if (jobPrefix.isNotEmpty()) put("prefix", jobPrefix)
      if (jobOwner.isNotEmpty()) put("owner", jobOwner)
    }
    if (isFetchExecData) put("exec-data", "Y")
    if (maxJobsToReturn != 1000) put("max-jobs", maxJobsToReturn.toString())
  }

  /**
   * Produce a [ZosmfListJobsResponse] object basing on the [clientResponse] from the HTTP request
   * and [errorReport] if present. The response body is a JSON array of job objects.
   */
  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfListJobsResponse {
    val responseSerializer = serializer<List<ZosmfJobItem>>()
    val isRequestSucceeded = clientResponse.status.isSuccess()
    val jobs = Json.decodeFromString(
      responseSerializer,
      if (isRequestSucceeded) clientResponse.bodyAsText() else "[]"
    )
    return ZosmfListJobsResponse(ZosmfStatus(clientResponse.status, errorReport), jobs)
  }
}
