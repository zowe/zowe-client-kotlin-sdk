/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging

import io.ktor.http.HttpStatusCode
import org.zowe.kotlinsdk.core.datasets.api.messaging.GetDatasetInfoResponse
import org.zowe.kotlinsdk.core.datasets.data.DatasetItem
import org.zowe.kotlinsdk.providers.zowe.HttpResponse

/**
 * [HttpResponse]-compatible representation of a getDatasetInfo response
 * @param status the [HttpStatusCode] of the listDatasets request
 * @param dataset the dataset info, fetched by the listDatasets request
 */
class ZosmfGetDatasetInfoResponse(
  override var status: HttpStatusCode,
  override val dataset: DatasetItem
) : GetDatasetInfoResponse, HttpResponse
