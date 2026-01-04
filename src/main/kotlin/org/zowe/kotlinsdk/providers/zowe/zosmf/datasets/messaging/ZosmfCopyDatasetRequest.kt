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
import org.zowe.kotlinsdk.core.datasets.api.messaging.CopyDatasetRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfStatus

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities">z/OS data set and member utilities: 'copy' request</a>
 * @property dsName to-dataset-name path param
 * @property volser to-volser path param
 * @property memberName member-name path param
 * @property body the request body to copy dataset
 */
class ZosmfCopyDatasetRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) val volser: String?,
  @property:AvailableSince(ZVersion.ZOS_2_1) val memberName: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val body: ZosmfCopyDatasetRequestBody,
  override val headers: ZosmfDatasetUtilitiesRequestHeaders = ZosmfDatasetUtilitiesRequestHeaders()
) : ZosmfDatasetUtilitiesRequest(headers), CopyDatasetRequest {
  override val responseClass = ZosmfCopyDatasetResponse::class.java

  private val dsAndMemberPath = if (memberName?.isNotEmpty() == true) "$dsName($memberName)" else dsName
  override val fullDsPath = if (volser?.isNotEmpty() == true) "-$volser/$dsAndMemberPath" else dsAndMemberPath

  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfCopyDatasetResponse {
    return ZosmfCopyDatasetResponse(ZosmfStatus(clientResponse.status, errorReport))
  }
}
