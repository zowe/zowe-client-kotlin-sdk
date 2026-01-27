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
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.jes.api.messaging.GetJobRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfStatus
import org.zowe.kotlinsdk.providers.zowe.zosmf.jes.definitions.ZosmfJobItem

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-obtain-status-job">Obtain the status of a job</a>
 * @property jobName the jobname path param
 * @property jobId the jobid path param
 * @property jobCorellator the correlator path param
 * @property isFetchStepData the step-data query param
 * @property userCorrelator the user-correlator query param
 * @property isFetchStepData the exec-data query param
 */
class ZosmfGetJobRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val jobName: String = "",
  @property:AvailableSince(ZVersion.ZOS_2_1) override val jobId: String = "",
  @property:AvailableSince(ZVersion.ZOS_2_1) val jobCorellator: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val isFetchStepData: Boolean = false,
  @property:AvailableSince(ZVersion.ZOS_2_4) val userCorrelator: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_4) val isFetchExecData: Boolean = false,
  override val headers: ZosmfGetJobRequestHeaders = ZosmfGetJobRequestHeaders()
) : ZosmfHttpRequest, GetJobRequest {
  override val responseClass = ZosmfGetJobResponse::class.java

  override val method = HttpMethod.Get

  private val jobLocator =
    if (jobName.isNotEmpty() && jobId.isNotEmpty()) "$jobName/$jobId"
    else jobCorellator
      ?: throw Exception("Either job name and job id or job correlator should be provided")

  override val path = "/zosmf/restjobs/jobs/${jobLocator}"

  override val parameters = mutableMapOf(
    "step-data" to if (isFetchStepData) "Y" else "N",
    "exec-data" to if (isFetchExecData) "Y" else "N"
  )
    .apply {
      userCorrelator?.let { this["user-correlator"] = userCorrelator }
    }

  override val body = null

  /**
   * Produce a [ZosmfGetJobResponse] object basing on the [clientResponse] from the HTTP request
   * and [errorReport] if present
   */
  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfGetJobResponse {
    val responseSerializer = serializer<ZosmfJobItem>()
    val isRequestSucceeded = clientResponse.status.isSuccess()
    val job = Json.decodeFromString(
      responseSerializer,
      if (isRequestSucceeded) clientResponse.bodyAsText() else "{}"
    )
    return ZosmfGetJobResponse(ZosmfStatus(clientResponse.status, errorReport), job)
  }

}