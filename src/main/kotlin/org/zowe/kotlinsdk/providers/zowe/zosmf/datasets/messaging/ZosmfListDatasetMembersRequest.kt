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

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.AttributesLevel
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetMembersRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.HttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMAttributes
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMMigratedRecall

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-members-zos-data-set">List the members of a z/OS data set</a> */
class ZosmfListDatasetMembersRequest(
  override val connection: HttpConnection,

  /** dataset-name path param */
  @property:AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,

  /** start query param */
  @property:AvailableSince(ZVersion.ZOS_2_1) val start: String? = null,

  /** pattern query param */
  @property:AvailableSince(ZVersion.ZOS_2_1) val pattern: String? = null,

  /** X-IBM-Max-Items custom header */
  @property:AvailableSince(ZVersion.ZOS_2_1) override val maxItems: Int? = null,

  /** Level of attributes to be returned ([AttributesLevel.FULL] by default) */
  @property:AvailableSince(ZVersion.ZOS_2_1) override val attributesLevel: AttributesLevel = AttributesLevel.FULL,

  /** Additional parameter for X-IBM-Attributes. If true - it is set to "$attributes,total" */
  @property:AvailableSince(ZVersion.ZOS_2_1) val returnTotalRows: Boolean = true,

  /** X-IBM-Migrated-Recall custom header */
  @property:AvailableSince(ZVersion.ZOS_2_2) override val migratedRecall: XIBMMigratedRecall? = null,

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
) : HttpRequest, ListDatasetMembersRequest, ZosmfListDatasetMembersRequestHeaders {

  override val attributes: XIBMAttributes = XIBMAttributes(attributesLevel, isTotal=returnTotalRows)

  override val method = HttpMethod.Get

  override val path = "/zosmf/restfiles/ds/$dsName/member"

  override val headers = getHeadersMap()

  override val parameters = mutableMapOf(
    "start" to start,
    "pattern" to pattern
  )

  override val body = null

  override suspend fun produceHttpResponse(clientResponse: HttpResponse): org.zowe.kotlinsdk.providers.zowe.HttpResponse {
    val response = clientResponse.body<ZosmfListDatasetMembersResponse>()
    response.status = clientResponse.status
    return response
  }

}
