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
import org.zowe.kotlinsdk.core.*
import org.zowe.kotlinsdk.core.restfiles.RestfilesAPI
import org.zowe.kotlinsdk.core.restfiles.XIBMAttr
import retrofit2.Call

// TODO: doc
class ListDatasetsOperation(
  private val datasetName: String,
  private val listParams: DsListParams,
  private val connection: Connection,
  httpClient: OkHttpClient
) : Operation<RestfilesAPI, ListDatasetsResponse>(connection, httpClient, RestfilesAPI::class.java) {
  override fun buildCall(runnerAPI: RestfilesAPI): Call<ListDatasetsResponse> {
    return runnerAPI.listDatasets(
      authorizationToken = connection.basicCredentials,
      xIBMAttr = XIBMAttr(listParams.attribute, listParams.returnTotalRows),
      xIBMMaxItems = listParams.maxLength ?: 0,
      xIBMResponseTimeout = listParams.responseTimeout,
      dsLevel = datasetName,
      volser = listParams.volume,
      start = listParams.start
    )
  }
}