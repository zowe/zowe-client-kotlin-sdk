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

import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.core.datasets.DatasetsAPI
import org.zowe.kotlinsdk.core.datasets.data.*
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.*
import org.zowe.kotlinsdk.impl.zosmf.datasets.operations.ListDatasetMembersOperation
import org.zowe.kotlinsdk.impl.zosmf.datasets.operations.ListDatasetsOperation
import org.zowe.kotlinsdk.impl.zosmf.datasets.operations.RetrieveDatasetContentOperation
import org.zowe.kotlinsdk.impl.zosmf.datasets.operations.RetrieveDatasetContentWithVolserOperation

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/2.5.0?topic=services-zos-data-set-file-rest-interface
 */
class ZosmfDatasetsAPIImpl(val connection: Connection, val httpClient: OkHttpClient) : DatasetsAPI {
  // TODO: doc
  override fun listDatasets(params: ListDatasetsRequest): ListDatasetsResponse {
    return ListDatasetsOperation(params as ZosmfListDatasetsRequest, connection, httpClient).runOperation()
  }

  override fun getDatasetInfo(params: GetDatasetInfoRequest): GetDatasetInfoResponse {
    val zosmfParams = (params as ZosmfGetDatasetInfoRequest).toListDatasetsRequest()
    val listDatasetsResponse = ListDatasetsOperation(zosmfParams, connection, httpClient).runOperation()
    val dataset = listDatasetsResponse.items.firstOrNull()
    return if (dataset?.datasetName?.uppercase() == zosmfParams.mask.uppercase())
      GetDatasetInfoResponse(dataset)
    else
      throw Exception("Dataset for the specified mask: '${zosmfParams.mask}' is not found")
  }

  override fun listDatasetMembers(params: ListDatasetMembersRequest): ListDatasetMembersResponse {
    return ListDatasetMembersOperation(params as ZosmfListDatasetMembersRequest, connection, httpClient).runOperation()
  }

  override fun retrieveDatasetContent(params: RetrieveDatasetContentRequest): RetrieveDatasetContentResponse {
    val zosmfParams = params as ZosmfRetrieveDatasetContentRequest
    val responseBody = if (zosmfParams.volser?.isNotEmpty() == true)
      RetrieveDatasetContentWithVolserOperation(zosmfParams, connection, httpClient).runOperation()
    else
      RetrieveDatasetContentOperation(zosmfParams, connection, httpClient).runOperation()
    return ZosmfRetrieveDatasetContentResponse(fetchedContent = responseBody)
  }
}
