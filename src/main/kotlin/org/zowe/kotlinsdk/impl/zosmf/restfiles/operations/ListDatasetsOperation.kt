//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.restfiles.operations

import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.core.zosmf.restfiles.data.DataSetListDocument
import org.zowe.kotlinsdk.core.zosmf.restfiles.data.ListDatasetsRequest
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.Operation
import org.zowe.kotlinsdk.impl.zosmf.restfiles.RestfilesAPI
import org.zowe.kotlinsdk.impl.zosmf.restfiles.data.DataSetListDocumentGson
import retrofit2.Call

// TODO: doc
internal class ListDatasetsOperation(
  private val params: ListDatasetsRequest,
  private val connection: Connection,
  httpClient: OkHttpClient
) : Operation<RestfilesAPI, DataSetListDocumentGson>(connection, httpClient, RestfilesAPI::class.java) {
  override fun buildCall(runnerAPI: RestfilesAPI): Call<DataSetListDocumentGson> {
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
      dslevel = params.dslevel,
      volumeSerial = params.volumeSerial,
      start = params.start
    )
  }
}
