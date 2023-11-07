//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.restfiles

import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.Operation
import retrofit2.Call

// TODO: doc
class GetDatasetInfoOperation (
  private val datasetName: String,
  private val connection: Connection,
  private val httpClient: OkHttpClient
) : Operation<RestfilesAPI, DatasetInfo>(connection, httpClient, RestfilesAPI::class.java) {
  override fun buildCall(runnerAPI: RestfilesAPI): Call<DatasetInfo> {
    val emptyDataSet = DatasetInfo(datasetName)
    val tokens: List<String> = datasetName.split(".")
    val length = tokens.size - 1
    if (length < 0) {
      return MockedCall(emptyDataSet)
    }
    val str = StringBuilder()
    for (i in 0 until length) {
      str.append(tokens[i])
      str.append(".")
    }
    val dataSetSearchStr = str.toString().substring(0, str.length - 1)
    val listParams = ZOSListParams(attribute = XIBMAttr.Type.BASE)
    val dsListResponse = ListDatasetsOperation(dataSetSearchStr, listParams, connection, httpClient).runOperation()
    val dataSet: DatasetInfo? = dsListResponse
      .items
      .filter { el -> el.name.contains(datasetName.uppercase()) }
      .getOrNull(0)
    return MockedCall(dataSet ?: emptyDataSet)
  }
}