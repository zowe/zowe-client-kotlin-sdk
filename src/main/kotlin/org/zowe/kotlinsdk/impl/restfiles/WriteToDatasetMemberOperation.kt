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

import okhttp3.Credentials
import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.core.Connection
import org.zowe.kotlinsdk.core.Operation
import org.zowe.kotlinsdk.core.restfiles.RestfilesAPI
import retrofit2.Call

// TODO: doc
class WriteToDatasetMemberOperation (
  private val datasetName: String,
  private val memberName: String,
  private val content: ByteArray,
  private val connection: Connection,
  httpClient: OkHttpClient
) : Operation<RestfilesAPI, Void>(connection, httpClient, RestfilesAPI::class.java)  {
  override fun buildCall(runnerAPI: RestfilesAPI): Call<Void> {
    // TODO: writeToDatasetMember with a specified volser???
    return runnerAPI.writeToDatasetMember(
      authorizationToken = connection.basicCredentials,
      datasetName = datasetName,
      content = content,
      memberName = memberName
    )
  }
}