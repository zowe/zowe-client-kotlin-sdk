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

import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.datasets.AttributesLevel
import org.zowe.kotlinsdk.core.datasets.api.messaging.GetDatasetInfoRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.core.datasets.data.DatasetItem
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.ZosmfDatasetItem

/**
 * Get dataset info request parameters holder.
 * Stores all necessary info to get a dataset information by the specified parameters
 * @property dsName the data set name
 * @property volumeSerial the volser query param
 */
class ZosmfGetDatasetInfoRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) val volumeSerial: String? = null,
  override val headers: ZosmfGetDatasetInfoRequestHeaders = ZosmfGetDatasetInfoRequestHeaders()
) : GetDatasetInfoRequest, ZosmfHttpRequest {
  /** Main HTTP request building parameters are omitted as they are not needed in this request */
  override val method: HttpMethod = HttpMethod.Get
  override val path: String = ""
  override val parameters: Map<String, String?> = emptyMap()
  override val body: Any? = null

  override val responseClass = ZosmfGetDatasetInfoResponse::class.java

  /**
   * The actual data set, that is found by listDatasets HTTP request.
   * If the data set is not found, it is defined as an _INERROR data set
   */
  private var foundDataset: DatasetItem = ZosmfDatasetItem("_IN_ERROR")

  fun produceResponseObjectFromStatus(clientResponseStatus: HttpStatusCode): ZosmfGetDatasetInfoResponse {
    return ZosmfGetDatasetInfoResponse(clientResponseStatus, foundDataset)
  }

  /**
   * Execute the listDatasets HTTP request to get the attributes of the requested data set.
   * If there are no data sets returned, a 404 HTTP error is produced.
   * If there is an error during a listDatasets request, a 500 HTTP error is produced
   * @param client the [HttpClient] to execute the listDatasets request with
   * @return the [ZosmfGetDatasetInfoResponse] object with the requested data set attributes
   */
  override suspend fun execHttpRequest(client: HttpClient): ZosmfGetDatasetInfoResponse {
    val attributesLevel = AttributesLevel.FULL
    val listDatasetsRequest = ZosmfListDatasetsRequest(
      connection = connection,
      mask = this.dsName,
      attributesLevel = attributesLevel,
      headers = ZosmfListDatasetsRequestHeaders(
        maxItems = 1,
        attributesLevel = attributesLevel,
        returnTotalRows = false,
        sessionLimitWait = headers.sessionLimitWait,
        asyncThreshold = headers.asyncThreshold,
        responseTimeout = headers.responseTimeout,
        requestAcctnum = headers.requestAcctnum,
        requestProc = headers.requestProc,
        requestRegion = headers.requestRegion,
        targetSystem = headers.targetSystem,
        targetSystemUser = headers.targetSystemUser,
        targetSystemPassword = headers.targetSystemPassword
      ),
      volumeSerial = this.volumeSerial,
      start = this.dsName
    )
    val listDatasetsResponse = listDatasetsRequest.execHttpRequest(client) as ZosmfListDatasetsResponse
    val listDatasetsStatus = listDatasetsResponse.status
    return if (listDatasetsResponse.status.type == StatusType.SUCCESS) {
      val dataset = listDatasetsResponse.dsItems.firstOrNull()
      if (dataset != null && dataset.datasetName.equals(listDatasetsRequest.mask, ignoreCase = true)) {
        foundDataset = dataset
        produceResponseObjectFromStatus(HttpStatusCode.OK)
      } else {
        produceResponseObjectFromStatus(
          HttpStatusCode(
            404,
            "Dataset for the specified mask: '${listDatasetsRequest.mask}' is not found"
          )
        )
      }
    } else {
      produceResponseObjectFromStatus(
        HttpStatusCode(
          500,
          "Failed to list data sets by name '${listDatasetsRequest.mask}': ${listDatasetsStatus.text}"
        )
      )
    }
  }
}