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

import io.ktor.http.HttpStatusCode
import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.datasets.api.messaging.GetDatasetInfoResponse
import org.zowe.kotlinsdk.core.datasets.data.DatasetItem
import org.zowe.kotlinsdk.providers.zowe.HttpResponse
import org.zowe.kotlinsdk.providers.zowe.HttpStatus
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse

/**
 * [HttpResponse]-compatible representation of a getDatasetInfo response
 * @param clientResponseStatus the [HttpStatusCode] of the listDatasets request to transform into [HttpStatus]
 * @param dataset the dataset info, fetched by the listDatasets request
 */
class ZosmfGetDatasetInfoResponse(
  clientResponseStatus: HttpStatusCode,
  override val dataset: DatasetItem
) : ZosmfHttpResponse(), GetDatasetInfoResponse {
  override var status: Status = HttpStatus(clientResponseStatus)
}
