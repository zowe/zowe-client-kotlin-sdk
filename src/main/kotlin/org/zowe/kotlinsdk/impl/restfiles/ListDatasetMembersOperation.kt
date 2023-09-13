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
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.Operation
import retrofit2.Call

// TODO: doc
class ListDatasetMembersOperation(
  private val datasetName: String,
  private val listParams: ZOSListParams,
  private val connection: Connection,
  httpClient: OkHttpClient
) : Operation<RestfilesAPI, ListDatasetMembersResponse>(connection, httpClient, RestfilesAPI::class.java) {
  override fun buildCall(runnerAPI: RestfilesAPI): Call<ListDatasetMembersResponse> {
    return runnerAPI.listDatasetMembers(
      authorizationToken = connection.basicCredentials,
      xIBMAttr = XIBMAttr(listParams.attribute, listParams.returnTotalRows),
      xIBMMaxItems = listParams.maxLength ?: 0,
      xIBMMigratedRecall = listParams.recall,
      datasetName = datasetName,
      start = listParams.start ,
      pattern = listParams.pattern
    )
  }
}