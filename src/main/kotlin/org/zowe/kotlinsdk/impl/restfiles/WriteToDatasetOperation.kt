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
class WriteToDatasetOperation (
  private val datasetName: String,
  private val content: ByteArray,
  private val connection: Connection,
  httpClient: OkHttpClient
) : Operation<RestfilesAPI, Void>(connection, httpClient, RestfilesAPI::class.java)  {
  override fun buildCall(runnerAPI: RestfilesAPI): Call<Void> {
    // TODO: writeToDataset with a specified volser???
    return runnerAPI.writeToDataset(
      authorizationToken = connection.basicCredentials,
      datasetName = datasetName,
      content = content
    )
  }
}