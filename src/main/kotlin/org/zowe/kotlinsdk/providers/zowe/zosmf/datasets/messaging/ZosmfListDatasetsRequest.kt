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

import io.ktor.http.HttpMethod
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.AttributesLevel
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetsRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-zos-data-sets-system">List the z/OS datasets on a system</a>
 * @property mask the dslevel query params
 * @property volumeSerial the volser query param
 * @property start the start query param
 */
class ZosmfListDatasetsRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val mask: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) val volumeSerial: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val start: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val attributesLevel: AttributesLevel = AttributesLevel.FULL,
  private val returnTotalRows: Boolean = true,
  override val headers: ZosmfListDatasetsRequestHeaders =
    ZosmfListDatasetsRequestHeaders(attributesLevel = attributesLevel, returnTotalRows = returnTotalRows)
) : ZosmfHttpRequest, ListDatasetsRequest {
  override val responseClass = ZosmfListDatasetsResponse::class.java
  override val method = HttpMethod.Get
  override val path = "/zosmf/restfiles/ds"
  override val parameters = mutableMapOf("dslevel" to mask, "volser" to volumeSerial, "start" to start)
  override val body = null
}