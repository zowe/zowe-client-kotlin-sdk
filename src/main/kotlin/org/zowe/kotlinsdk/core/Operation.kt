//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core

import okhttp3.OkHttpClient
import retrofit2.Call

// TODO: doc
abstract class Operation<API, R>(
  connection: Connection,
  private val httpClient: OkHttpClient,
  private val apiClass: Class<out API>,
  private val customCallFactory: okhttp3.Call.Factory? = null
// TODO: should it be like this?
//  var httpClient: OkHttpClient = UnsafeOkHttpClient.unsafeOkHttpClient
) {
  var url: String

  init {
    connection.checkConnection()
    url = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
  }

  // TODO: doc
  private fun retrieveAPI(): API {
    // TODO: implement API Set to get already built API in case if connection is the same as already provided one
    return buildApi(url, httpClient, apiClass, customCallFactory=customCallFactory)
  }

  // TODO: doc
  protected open fun buildCall(runnerAPI: API): Call<R> {
    throw NotImplementedError("buildCall should be implemented by the operation class")
  }

  // TODO: doc
  fun runOperation(): R {
    val runnerAPI = this.retrieveAPI()
    val call = this.buildCall(runnerAPI)
    val response = call.execute()
    if (!response.isSuccessful) {
      throw Exception(response.errorBody()?.string())
    }
    return response.body() ?: throw Exception("No body returned")
  }
}