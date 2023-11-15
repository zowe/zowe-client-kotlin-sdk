//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf

import okhttp3.OkHttpClient
import retrofit2.Call

// TODO: doc
abstract class Operation<API, R>(
  connection: Connection,
  private val httpClient: OkHttpClient,
  private val apiClass: Class<out API>,
  private val callCustomizer: CallCustomizer? = null
) {
  var url: String

  init {
    connection.checkConnection()
    url = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
  }

  // TODO: doc
  private fun retrieveAPI(): API {
    // TODO: implement API Set to get already built API in case if connection is the same as already provided one
    return buildApi(url, httpClient, apiClass)
  }

  // TODO: doc
  protected abstract fun buildCall(runnerAPI: API): Call<R>

  // TODO: doc
  fun runOperation(callCustomizer: CallCustomizer? = null): R {
    val runnerAPI = this.retrieveAPI()
    var call = this.buildCall(runnerAPI)
    call = callCustomizer?.customizeCall(call) ?: call
    val response = call.execute()
    if (!response.isSuccessful) {
      throw Exception(response.errorBody()?.string())
    }
    return response.body() ?: throw Exception("No body returned")
  }
}
