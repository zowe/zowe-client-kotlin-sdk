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
import org.zowe.kotlinsdk.core.datasets.api.messaging.CreateDatasetRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfStatus

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-create-sequential-partitioned-data-set">Create a sequential or partitioned data set</a>
 * @property dsName the dataset-name path param
 * @property body the request body to create a sequential or partitioned dataset
 */
class ZosmfCreateDatasetRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val body: ZosmfCreateDatasetRequestBody,
  override val headers: ZosmfCreateDatasetRequestHeaders = ZosmfCreateDatasetRequestHeaders()
) : ZosmfHttpRequest, CreateDatasetRequest {
  override val responseClass = ZosmfCreateDatasetResponse::class.java
  override val method = HttpMethod.Post
  override val path = "/zosmf/restfiles/ds/$dsName"
  override val parameters = emptyMap<String, String>()

  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfCreateDatasetResponse {
    return ZosmfCreateDatasetResponse(ZosmfStatus(clientResponse.status, errorReport))
  }
}