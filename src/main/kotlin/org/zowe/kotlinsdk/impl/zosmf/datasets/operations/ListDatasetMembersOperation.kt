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
import org.zowe.kotlinsdk.core.datasets.data.ListDatasetMembersResponse
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.Operation
import org.zowe.kotlinsdk.impl.zosmf.datasets.ZosmfDatasetsAPICalls
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.ZosmfListDatasetMembersRequest
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.ZosmfListDatasetMembersResponse
import retrofit2.Call

// TODO: doc
internal class ListDatasetMembersOperation(
  private val params: ZosmfListDatasetMembersRequest,
  private val connection: Connection,
  httpClient: OkHttpClient
) : Operation<ZosmfDatasetsAPICalls, ZosmfListDatasetMembersResponse>(
  connection,
  httpClient,
  ZosmfDatasetsAPICalls::class.java
) {
  override fun buildCall(runnerAPI: ZosmfDatasetsAPICalls): Call<ZosmfListDatasetMembersResponse> {
    return runnerAPI.listDatasetMembers(
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
      migratedRecall = params.migratedRecall,
      targetSystemUser = params.targetSystemUser,
      targetSystemPassword = params.targetSystemPassword,
      datasetName = params.datasetName,
      start = params.start,
      pattern = params.pattern
    )
  }
}