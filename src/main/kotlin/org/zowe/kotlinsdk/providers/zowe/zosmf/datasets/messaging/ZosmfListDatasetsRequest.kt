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

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import kotlinx.coroutines.runBlocking
import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetsRequest
import org.zowe.kotlinsdk.providers.zowe.Connection
import org.zowe.kotlinsdk.providers.zowe.HttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMAttributes

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-zos-data-sets-system">List the z/OS datasets on a system</a> */
class ZosmfListDatasetsRequest(
  override val connection: Connection,

  /** dslevel query params */
  @AvailableSince(ZVersion.ZOS_2_1) override val mask: String,

  /** volser query param */
  @AvailableSince(ZVersion.ZOS_2_1) val volumeSerial: String? = null,

  /** start query param */
  @AvailableSince(ZVersion.ZOS_2_1) val start: String? = null,

  /** X-IBM-Max-Items custom header */
  @AvailableSince(ZVersion.ZOS_2_1) override val maxItems: Int? = null,

  /** X-IBM-Attributes custom header */
  @AvailableSince(ZVersion.ZOS_2_1) override val attributes: XIBMAttributes? = null,

  /** X-IBM-Target-System default header */
  @AvailableSince(ZVersion.ZOS_2_4) override val targetSystem: String? = null,

  /** X-IBM-Target-System-User custom header */
  @AvailableSince(ZVersion.ZOS_2_4) override val targetSystemUser: String? = null,

  /** X-IBM-Target-System-Password custom header */
  @AvailableSince(ZVersion.ZOS_2_4) override val targetSystemPassword: String? = null,

  /** X-IBM-Session-Limit-Wait default header */
  @AvailableOnly(ZVersion.ZOS_2_4) override val sessionLimitWait: Int? = null,

  /** X-IBM-Async-Threshold default header */
  @AvailableSince(ZVersion.ZOS_2_1) override val asyncThreshold: Int? = null,

  /** X-IBM-Response-Timeout default header */
  @AvailableSince(ZVersion.ZOS_2_1) override val responseTimeout: Int? = null,

  /** X-IBM-Request-Acctnum default header */
  @AvailableSince(ZVersion.ZOS_2_5) override val requestAcctnum: String? = null,

  /** X-IBM-Request-Proc default header */
  @AvailableSince(ZVersion.ZOS_2_5) override val requestProc: String?= null,

  /** X-IBM-Request-Region default header */
  @AvailableSince(ZVersion.ZOS_2_5) override val requestRegion: String? = null,
) : HttpRequest, ListDatasetsRequest, ZosmfListDatasetsRequestHeaders {

  override val method = HttpMethod.Get

  override val path = "/zosmf/restfiles/ds"

  override val headers = getHeadersMap()

  override val parameters = mutableMapOf(
    "dslevel" to mask,
    "volser" to volumeSerial,
    "start" to start
  )

  override val body = null

  override fun produceHttpResponse(clientResponse: HttpResponse): org.zowe.kotlinsdk.providers.zowe.HttpResponse? {
    return runBlocking {
      val response = clientResponse.body<ZosmfListDatasetsResponse>()
      response.status = clientResponse.status
      response
    }
  }

}