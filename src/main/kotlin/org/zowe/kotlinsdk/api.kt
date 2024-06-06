/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBA Group 2020
 */

package org.zowe.kotlinsdk

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

@Deprecated(
  "Scheduled for removal since v1.0.0",
  ReplaceWith("gson", "org.zowe.kotlinsdk.core")
)
val gson: Gson = GsonBuilder()
  .setLenient()
  .disableHtmlEscaping()
  .create()

@Deprecated(
  "Scheduled for removal since v1.0.0",
  ReplaceWith("buildApi(baseUrl, httpClient)", "org.zowe.kotlinsdk.core")
)
inline fun <reified API> buildApi(baseUrl: String, httpClient: OkHttpClient): API {
  val retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(httpClient)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()
  return retrofit.create(API::class.java)
}

@Deprecated(
  "Scheduled for removal since v1.0.0",
  ReplaceWith("buildApi(baseUrl, httpClient)", "org.zowe.kotlinsdk.core")
)
fun <API> buildApi(baseUrl: String, httpClient: OkHttpClient, apiClass: Class<out API>): API {
  val retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(httpClient)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()
  return retrofit.create(apiClass)
}

@Deprecated(
  "Scheduled for removal since v1.0.0",
  ReplaceWith("buildApi(baseUrl, httpClient, ConverterFactory.BYTES)", "org.zowe.kotlinsdk.core")
)
inline fun <reified Api> buildApiWithBytesConverter(baseUrl: String, httpClient: OkHttpClient): Api {
  val retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .addConverterFactory(BytesConverterFactory.create())
    .client(httpClient)
    .build()
  return retrofit.create(Api::class.java)
}

@Deprecated(
  "Scheduled for removal since v1.0.0",
  ReplaceWith("buildApi(baseUrl, httpClient, ConverterFactory.BYTES)", "org.zowe.kotlinsdk.core")
)
fun <API> buildApiWithBytesConverter(baseUrl: String, httpClient: OkHttpClient, apiClass: Class<out API>): API {
  val retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .addConverterFactory(BytesConverterFactory.create())
    .client(httpClient)
    .build()
  return retrofit.create(apiClass)
}
