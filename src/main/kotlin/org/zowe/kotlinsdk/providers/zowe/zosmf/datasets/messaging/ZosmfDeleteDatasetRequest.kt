/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.DeleteDatasetRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.HttpRequest

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-sequential-partitioned-data-set">Delete a sequential and partitioned dataset</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-partitioned-data-set-member">Delete a partitioned dataset member</a>
 */
class ZosmfDeleteDatasetRequest(
  override val connection: HttpConnection,

  /** dataset-name path param */
  @property:AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,

  /** volume path param */
  @property:AvailableSince(ZVersion.ZOS_2_1) val volume: String?,

  /** member-name path param */
  @property:AvailableSince(ZVersion.ZOS_2_1) val memberName: String?,

  /** X-IBM-Dsname-Encoding custom header */
  @property:AvailableSince(ZVersion.ZOS_2_5) override val xIBMDsNameEncoding: String? = null,

  /** X-IBM-Target-System default header */
  @property:AvailableSince(ZVersion.ZOS_2_4) override val targetSystem: String? = null,

  /** X-IBM-Target-System-User custom header */
  @property:AvailableSince(ZVersion.ZOS_2_4) override val targetSystemUser: String? = null,

  /** X-IBM-Target-System-Password custom header */
  @property:AvailableSince(ZVersion.ZOS_2_4) override val targetSystemPassword: String? = null,

  /** X-IBM-Async-Threshold default header */
  @property:AvailableSince(ZVersion.ZOS_2_1) override val asyncThreshold: Int? = null,

  /** X-IBM-Response-Timeout default header */
  @property:AvailableSince(ZVersion.ZOS_2_1) override val responseTimeout: Int? = null,

  /** X-IBM-Session-Limit-Wait default header */
  @property:AvailableOnly(ZVersion.ZOS_2_4) override val sessionLimitWait: Int? = null,

  /** X-IBM-Request-Acctnum default header */
  @property:AvailableSince(ZVersion.ZOS_2_5) override val requestAcctnum: String? = null,

  /** X-IBM-Request-Proc default header */
  @property:AvailableSince(ZVersion.ZOS_2_5) override val requestProc: String? = null,

  /** X-IBM-Request-Region default header */
  @property:AvailableSince(ZVersion.ZOS_2_5) override val requestRegion: String? = null,
) : HttpRequest, DeleteDatasetRequest, ZosmfDeleteDatasetRequestHeaders {

  override val method = HttpMethod.Delete

  private val dsAndMemberPath = if (memberName?.isNotEmpty() == true) "$dsName($memberName)" else dsName
  private val fullDsPath = if (volume?.isNotEmpty() == true) "-$volume/$dsAndMemberPath" else dsAndMemberPath

  override val path = "/zosmf/restfiles/ds/$fullDsPath"

  override val headers = getHeadersMap()

  override val parameters = emptyMap<String, String>()

  override val body = null

  override suspend fun produceHttpResponse(clientResponse: HttpResponse): org.zowe.kotlinsdk.providers.zowe.HttpResponse? {
    val response = ZosmfDeleteDatasetResponse(clientResponse.status)
      // TODO: log warning?
//      if (clientResponse.status != HttpStatusCode.NoContent) {
//      }
    return response
  }

}
