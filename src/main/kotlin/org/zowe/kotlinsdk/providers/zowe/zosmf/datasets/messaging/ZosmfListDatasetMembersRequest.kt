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
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetMembersRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.core.datasets.AttributesLevel
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-list-members-zos-data-set">List the members of a z/OS data set</a>
 * @property connection the [HttpConnection] to work with
 * @property dsName dataset-name path param
 * @property start start query param
 * @property pattern pattern query param
 */
class ZosmfListDatasetMembersRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) val start: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val pattern: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val attributesLevel: AttributesLevel = AttributesLevel.FULL,
  private val returnTotalRows: Boolean = true,
  override val headers: ZosmfListDatasetMembersRequestHeaders =
    ZosmfListDatasetMembersRequestHeaders(attributesLevel = attributesLevel, returnTotalRows = returnTotalRows)
) : ZosmfHttpRequest, ListDatasetMembersRequest {
  override val responseClass = ZosmfListDatasetMembersResponse::class.java
  override val method = HttpMethod.Get
  override val path = "/zosmf/restfiles/ds/$dsName/member"
  override val parameters = mutableMapOf("start" to start, "pattern" to pattern)
  override val body = null
}
