//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.datasets.operations

import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.Operation
import org.zowe.kotlinsdk.impl.zosmf.datasets.ZosmfDatasetsAPICalls
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.ZosmfListDatasetsRequest
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.ZosmfListDatasetsResponse
import retrofit2.Call

// TODO: doc
internal class ListDatasetsOperation(
  private val params: ZosmfListDatasetsRequest,
  private val connection: Connection,
  httpClient: OkHttpClient
) : Operation<ZosmfDatasetsAPICalls, ZosmfListDatasetsResponse>(connection, httpClient, ZosmfDatasetsAPICalls::class.java) {
  override fun buildCall(runnerAPI: ZosmfDatasetsAPICalls): Call<ZosmfListDatasetsResponse> {
    return runnerAPI.listDatasets(
      authorizationToken = connection.basicCredentials,
      asyncThreshold = params.asyncThreshold,
      responseTimeout = params.responseTimeout,
      sessionLimitWait = params.sessionLimitWait,
      requestAcctnum = params.requestAcctnum,
      requestProc = params.requestProc,
      requestRegion = params.requestRegion,
      targetSystem = params.targetSystem,
      maxItems = params.maxItems,
      attributes = params.attributes,
      targetSystemUser = params.targetSystemUser,
      targetSystemPassword = params.targetSystemPassword,
      dslevel = params.mask,
      volumeSerial = params.volumeSerial,
      start = params.start
    )
  }
}
