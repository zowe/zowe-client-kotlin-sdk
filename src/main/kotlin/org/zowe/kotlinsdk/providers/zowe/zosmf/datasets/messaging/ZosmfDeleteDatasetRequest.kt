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
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.DeleteDatasetRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-delete-sequential-partitioned-data-set">Delete a sequential and partitioned dataset</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-delete-partitioned-data-set-member">Delete a partitioned dataset member</a>
 * @property dsName the dataset-name path param
 * @property volume the volume path param
 * @property memberName the member-name path param
 */
class ZosmfDeleteDatasetRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) val volume: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val memberName: String? = null,
  override val headers: ZosmfDeleteDatasetRequestHeaders = ZosmfDeleteDatasetRequestHeaders()
) : ZosmfHttpRequest, DeleteDatasetRequest {
  override val responseClass = ZosmfDeleteDatasetResponse::class.java

  private val dsAndMemberPath = if (memberName?.isNotEmpty() == true) "$dsName($memberName)" else dsName
  private val fullDsPath = if (volume?.isNotEmpty() == true) "-($volume)/$dsAndMemberPath" else dsAndMemberPath

  override val method = HttpMethod.Delete
  override val path = "/zosmf/restfiles/ds/$fullDsPath"
  override val parameters = emptyMap<String, String>()
  override val body = null

  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfDeleteDatasetResponse {
    return ZosmfDeleteDatasetResponse(ZosmfDeleteDatasetStatus(clientResponse.status, errorReport))
  }
}
