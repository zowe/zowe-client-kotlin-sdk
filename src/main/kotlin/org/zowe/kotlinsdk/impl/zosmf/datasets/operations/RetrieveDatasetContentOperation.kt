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
import okhttp3.ResponseBody
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.data.ListDatasetMembersResponse
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.Operation
import org.zowe.kotlinsdk.impl.zosmf.datasets.ZosmfDatasetsAPICalls
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.ZosmfListDatasetMembersRequest
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.ZosmfListDatasetMembersResponse
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.ZosmfRetrieveDatasetContentRequest
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.ZosmfRetrieveDatasetContentResponse
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Query

// TODO: doc
internal class RetrieveDatasetContentOperation(
  private val params: ZosmfRetrieveDatasetContentRequest,
  private val connection: Connection,
  httpClient: OkHttpClient
) : Operation<ZosmfDatasetsAPICalls, ResponseBody>(
  connection,
  httpClient,
  ZosmfDatasetsAPICalls::class.java
) {
  override fun buildCall(runnerAPI: ZosmfDatasetsAPICalls): Call<ResponseBody> {
    return runnerAPI.retrieveDatasetContent(
      authorizationToken = connection.basicCredentials,
      asyncThreshold = params.asyncThreshold,
      responseTimeout = params.responseTimeout,
      sessionLimitWait = params.sessionLimitWait,
      targetSystem = params.targetSystem,
      requestAcctnum = params.requestAcctnum,
      requestProc = params.requestProc,
      requestRegion = params.requestRegion,
      ifNoneMatch = params.ifNoneMatch,
      xIBMDataType = params.xIBMDataType,
      xIBMReturnEtag = params.xIBMReturnEtag,
      xIBMMigratedRecall = params.xIBMMigratedRecall,
      xIBMRecordRange = params.xIBMRecordRange,
      xIBMObtainENQ = params.xIBMObtainENQ,
      xIBMSessionRef = params.xIBMSessionRef,
      xIBMReleaseENQ = params.xIBMReleaseENQ,
      xIBMDsNameEncoding = params.xIBMDsNameEncoding,
      targetSystemUser = params.targetSystemUser,
      targetSystemPassword = params.targetSystemPassword,
      datasetName = params.dsName,
      search = params.search,
      research = params.research,
      insensitive = params.insensitive,
      maxReturnSize = params.maxReturnSize
    )
  }
}