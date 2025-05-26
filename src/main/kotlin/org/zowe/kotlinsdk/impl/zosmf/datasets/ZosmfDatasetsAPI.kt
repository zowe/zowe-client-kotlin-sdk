//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.datasets

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.DatasetsAPI
import org.zowe.kotlinsdk.core.datasets.data.*
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.RequestCanceller
import org.zowe.kotlinsdk.impl.zosmf.RequestRunner
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.*

/**
 * IBA Group's z/OSMF [DatasetsAPI] implementation to provide functions to work with datasets
 * @param client KTor initialized [HttpClient] to make requests with
 * @param connection [Connection] instance to make requests using the connection data
 * @param requestCanceller [RequestCanceller] instance to cancel requests from the outside
 */
internal class ZosmfDatasetsAPI(
  private val client: HttpClient,
  private val connection: Connection,
  private val requestCanceller: RequestCanceller
) : DatasetsAPI {
  @AvailableSince(ZVersion.ZOS_2_1)
  override fun listDatasets(params: ListDatasetsRequest): ListDatasetsResponse {
    return runBlocking {
      RequestRunner(client, connection, requestCanceller)
        .runRequest(params as ZosmfListDatasetsRequest)
        .body<ZosmfListDatasetsResponse>()
    }
  }

  @AvailableSince(ZVersion.ZOS_2_1)
  override fun getDatasetInfo(params: GetDatasetInfoRequest): GetDatasetInfoResponse {
    val zosmfParams = (params as ZosmfGetDatasetInfoRequest).toListDatasetsRequest()
    val listDatasetsResponse = listDatasets(zosmfParams)
    val dataset = listDatasetsResponse.dsItems.firstOrNull()
    return if (dataset?.datasetName?.uppercase() == zosmfParams.mask.uppercase())
      GetDatasetInfoResponse(dataset)
    else
      throw Exception("Dataset for the specified mask: '${zosmfParams.mask}' is not found")
  }

  @AvailableSince(ZVersion.ZOS_2_1)
  override fun listDatasetMembers(params: ListDatasetMembersRequest): ListDatasetMembersResponse {
    return runBlocking {
      RequestRunner(client, connection, requestCanceller)
        .runRequest(params as ZosmfListDatasetMembersRequest)
        .body<ZosmfListDatasetMembersResponse>()
    }
  }

  /**
   * Retrieve the dataset's content in binary or text format
   * @param params [RetrieveDatasetContentRequest] instance to get parameters for the request from
   * @return [RetrieveDatasetContentResponse] instance with the succeeded request result
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override fun retrieveDatasetContent(params: RetrieveDatasetContentRequest): RetrieveDatasetContentResponse {
    val zosmfParams = params as ZosmfRetrieveDatasetContentRequest
    val isBinary = zosmfParams.xIBMDataType?.type == XIBMDataType.Type.BINARY
    val (fetchedText, fetchedBytes) = runBlocking {
      val response = RequestRunner(client, connection, requestCanceller).runRequest(zosmfParams)
      if (isBinary) {
        val lengthLong = response.contentLength() ?: throw Exception("The dataset is empty")
        val length = lengthLong.toInt()
        if (length.toLong() != lengthLong) throw Exception("The dataset is too large to read")
        val byteArray = ByteArray(length)
        var offset = 0

        do {
          val currentRead = response.bodyAsChannel().readAvailable(byteArray, offset, byteArray.size)
          offset += currentRead
        } while (currentRead > 0 && offset != length)
        null to byteArray
      } else {
        response.bodyAsText() to null
      }
    }
    return ZosmfRetrieveDatasetContentResponse(fetchedText, fetchedBytes)
  }
}