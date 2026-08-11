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
import org.zowe.kotlinsdk.core.datasets.api.messaging.DeleteDatasetBackupVersionRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfStatus

// TODO: unit tests when possible
/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-data-set-member-utilities">z/OS data set and member utilities: 'hdelete' request</a>
 * @property dsName to-dataset-name path param
 * @property memberName member-name path param
 */
class ZosmfDeleteDatasetBackupVersionRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) val memberName: String?,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val body: ZosmfDeleteDatasetBackupVersionRequestBody,
  override val headers: ZosmfDatasetUtilitiesRequestHeaders = ZosmfDatasetUtilitiesRequestHeaders()
) : ZosmfDatasetUtilitiesRequest(headers), DeleteDatasetBackupVersionRequest {
  override val responseClass = ZosmfDeleteDatasetBackupVersionResponse::class.java

  override val fullDsPath = dsName

  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfDeleteDatasetBackupVersionResponse {
    return ZosmfDeleteDatasetBackupVersionResponse(ZosmfStatus(clientResponse.status, errorReport))
  }
}
