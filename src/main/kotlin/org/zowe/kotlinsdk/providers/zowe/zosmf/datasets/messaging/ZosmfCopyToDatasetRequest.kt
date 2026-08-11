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
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.CopyToDatasetRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.core.datasets.data.FromEntity
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfStatus

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-data-set-member-utilities">z/OS data set and member utilities: 'copy' request</a>
 * @property fromEntity [ZosmfCopyToDatasetRequestBody.FromFile] or [ZosmfCopyToDatasetRequestBody.FromDataset]
 * @property toDsName to-dataset-name path param
 * @property toMemberName member-name path param
 * @property toVolser to-volser path param
 * @property enq [ZosmfCopyToDatasetRequestBody.enq]
 * @property replace [ZosmfCopyToDatasetRequestBody.replace]
 */
class ZosmfCopyToDatasetRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val fromEntity: FromEntity,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val toDsName: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val toMemberName: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val toVolser: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val enq: ZosmfCopyToDatasetRequestBody.Enq? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val replace: Boolean? = null,
  override val headers: ZosmfDatasetUtilitiesRequestHeaders = ZosmfDatasetUtilitiesRequestHeaders()
) : ZosmfDatasetUtilitiesRequest(headers), CopyToDatasetRequest {
  override val responseClass = ZosmfCopyToDatasetResponse::class.java

  private val toDsAndMemberPath = if (toMemberName?.isNotEmpty() == true) "$toDsName($toMemberName)" else toDsName
  override val fullDsPath = if (toVolser?.isNotEmpty() == true) "-($toVolser)/$toDsAndMemberPath" else toDsAndMemberPath

  override val body = when (fromEntity) {
    is ZosmfCopyToDatasetRequestBody.FromFile ->
      ZosmfCopyToDatasetRequestBody(fromFile = fromEntity, enq = enq, replace = replace)

    is ZosmfCopyToDatasetRequestBody.FromDataset ->
      ZosmfCopyToDatasetRequestBody(fromDataset = fromEntity, enq = enq, replace = replace)

    else -> throw Exception("Unknown entity type to copy from: $fromEntity (${fromEntity::class.java})")
  }

  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfCopyToDatasetResponse {
    return ZosmfCopyToDatasetResponse(ZosmfStatus(clientResponse.status, errorReport))
  }
}
