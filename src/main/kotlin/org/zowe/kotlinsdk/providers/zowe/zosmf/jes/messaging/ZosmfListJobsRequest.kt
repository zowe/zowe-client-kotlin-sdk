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
import io.ktor.http.HttpMethod
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.core.jes.api.messaging.ListJobsRequest
import org.zowe.kotlinsdk.providers.zowe.HttpRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest

// TODO: implement
// TODO: doc
class ZosmfListJobsRequest(
  override val connection: HttpConnection,
  override val jobPrefix: String,
  override val jobId: String,
  override val jobOwner: String,
  override val maxJobsToReturn: Int = 1000,
  val isFetchExecData: Boolean = false,
  override val headers: HttpRequestHeaders = TODO("Not yet implemented"),
) : ZosmfHttpRequest, ListJobsRequest {
  override val responseClass = ZosmfGetJobResponse::class.java

  override val method = HttpMethod.Get

  override val body = null

  override val path: String
    get() = TODO("Not yet implemented")

  override val parameters: Map<String, String?>
    get() = TODO("Not yet implemented")

  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfListJobsResponse {
    TODO("Not yet implemented")
  }

}