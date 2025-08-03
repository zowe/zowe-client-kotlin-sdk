/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.jes.messaging

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import kotlinx.coroutines.runBlocking
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.jes.api.messaging.GetJobRequest
import org.zowe.kotlinsdk.providers.zowe.Connection
import org.zowe.kotlinsdk.providers.zowe.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.HttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfTargetSystemRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.jes.definitions.ZosmfJobItem

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-obtain-status-job">Obtain the status of a job</a> */
class ZosmfGetJobRequest(
  override val connection: HttpConnection,

  /** jobname path param */
  @AvailableSince(ZVersion.ZOS_2_1) val jobName: String? = null,

  /** jobnid path param */
  @AvailableSince(ZVersion.ZOS_2_1) val jobId: String? = null,

  /** correlator path param */
  @AvailableSince(ZVersion.ZOS_2_1) val jobCorellator: String? = null,

  /** step-data query param */
  @AvailableSince(ZVersion.ZOS_2_2) val isFetchStepData: Boolean = false,

  /** user-correlator query param */
  @AvailableSince(ZVersion.ZOS_2_4) val userCorrelator: String? = null,

  /** exec-data query param */
  @AvailableSince(ZVersion.ZOS_2_4) val isFetchExecData: Boolean = true,

  /** X-IBM-Target-System default header */
  @AvailableSince(ZVersion.ZOS_2_4) override val targetSystem: String? = null,

  /** X-IBM-Target-System-User custom header */
  @AvailableSince(ZVersion.ZOS_2_4) override val targetSystemUser: String? = null,

  /** X-IBM-Target-System-Password custom header */
  @AvailableSince(ZVersion.ZOS_2_4) override val targetSystemPassword: String? = null,
) : HttpRequest, GetJobRequest, ZosmfTargetSystemRequestHeaders {

  override val method = HttpMethod.Get

  private val jobLocator =
    if (jobName?.isNotEmpty() == true && jobId?.isNotEmpty() == true) "$jobName/$jobId"
    else jobCorellator
      ?: throw Exception("Either job name and job id or job correlator should be provided")

  override val path = "/zosmf/restjobs/jobs/${jobLocator}"

  override val headers = getHeadersMap()

  override val parameters = mutableMapOf(
    "step-data" to if (isFetchStepData) "Y" else "N",
    "exec-data" to if (isFetchExecData) "Y" else "N"
  )
    .apply {
      userCorrelator?.let { this["user-correlator"] = userCorrelator }
    }

  override val body = null

  override fun produceHttpResponse(clientResponse: HttpResponse): org.zowe.kotlinsdk.providers.zowe.HttpResponse? {
    return runBlocking {
      val jobItem = clientResponse.body<ZosmfJobItem>()
      ZosmfGetJobResponse(clientResponse.status, jobItem)
    }
  }

}