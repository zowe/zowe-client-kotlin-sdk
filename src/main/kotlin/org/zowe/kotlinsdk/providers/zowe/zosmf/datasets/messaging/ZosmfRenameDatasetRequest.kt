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
import org.zowe.kotlinsdk.core.datasets.api.messaging.RenameDatasetRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfStatus

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-data-set-member-utilities">z/OS data set and member utilities: 'rename' request</a>
 * @property dsName to-dataset-name path param
 * @property memberName member-name path param
 * @property body the request body to rename dataset
 */
class ZosmfRenameDatasetRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) val memberName: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val body: ZosmfRenameDatasetRequestBody,
  override val headers: ZosmfDatasetUtilitiesRequestHeaders = ZosmfDatasetUtilitiesRequestHeaders()
) : ZosmfDatasetUtilitiesRequest(headers), RenameDatasetRequest {
  override val responseClass = ZosmfRenameDatasetResponse::class.java

  override val fullDsPath = if (memberName?.isNotEmpty() == true) "$dsName($memberName)" else dsName

  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfRenameDatasetResponse {
    return ZosmfRenameDatasetResponse(ZosmfStatus(clientResponse.status, errorReport))
  }
}
