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

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Call
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/** To decide, which converter factory to use */
enum class ConverterFactory { SCALARS, BYTES }

val gson: Gson = GsonBuilder()
  .setLenient()
  .disableHtmlEscaping()
  .create()

/**
 * Build and create the specified API, wrapped with Retrofit2 functionality
 * @param baseUrl the base URL to send requests to and retrieve responses from
 * @param httpClient the [OkHttpClient] to use during request/response process
 * @param convFactory a parameter to specify, which ConverterFactory to use, [ConverterFactory.SCALARS] by default
 * @return the wrapped API
 */
//inline fun <reified API> buildApi(
//  baseUrl: String,
//  httpClient: OkHttpClient,
//  convFactory: ConverterFactory = ConverterFactory.SCALARS
//): API {
//  var retrofitSetup = Retrofit.Builder().baseUrl(baseUrl).client(httpClient)
//
//  retrofitSetup = when (convFactory) {
//    ConverterFactory.SCALARS ->
//      retrofitSetup
//        .addConverterFactory(ScalarsConverterFactory.create())
//        .addConverterFactory(GsonConverterFactory.create(gson))
//    ConverterFactory.BYTES ->
//      retrofitSetup
//        .addConverterFactory(BytesConverterFactory.create())
//  }
//
//  return retrofitSetup.build().create(API::class.java)
//}

// TODO: doc
fun <API> buildApi(
  baseUrl: String,
  httpClient: OkHttpClient,
  apiClass: Class<out API>,
  convFactory: ConverterFactory = ConverterFactory.SCALARS,
  customCallFactory: Call.Factory? = null
): API {
  var retrofitSetup = Retrofit.Builder().baseUrl(baseUrl).client(httpClient)

  retrofitSetup =
    if (customCallFactory != null) retrofitSetup.callFactory(customCallFactory) else retrofitSetup

  retrofitSetup = when (convFactory) {
    ConverterFactory.SCALARS ->
      retrofitSetup
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
    ConverterFactory.BYTES ->
      retrofitSetup
        .addConverterFactory(BytesConverterFactory.create())
  }

  return retrofitSetup.build().create(apiClass)
}
