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

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import okhttp3.Call
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.X509TrustManager

/** To decide, which converter factory to use */
enum class ConverterFactory { SCALARS, BYTES }

val gson: Gson = GsonBuilder()
  .setLenient()
  .disableHtmlEscaping()
  .create()

/**
 * TODO: review the doc
 * Build and create the specified API, wrapped with Retrofit2 functionality
 * @param baseUrl the base URL to send requests to and retrieve responses from
 * @param httpClient the [OkHttpClient] to use during request/response process
 * @param convFactory a parameter to specify, which ConverterFactory to use, [ConverterFactory.SCALARS] by default
 * @return the wrapped API
 */

// TODO: doc
fun <API> buildApi(
  baseUrl: String,
  httpClient: OkHttpClient,
  apiClass: Class<out API>,
  convFactory: ConverterFactory = ConverterFactory.SCALARS
): API {
  var retrofitSetup = Retrofit.Builder().baseUrl(baseUrl).client(httpClient)

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

// TODO: doc
fun getUnsafeKTorHttpClient(): HttpClient {
  return HttpClient(OkHttp) {
    install(ContentNegotiation) {
      gson()
    }
    engine {
      config {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<X509TrustManager>(
          object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

            override fun getAcceptedIssuers(): Array<X509Certificate> {
              return arrayOf()
            }
          }
        )

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("TLSv1.2")
        sslContext.init(null, trustAllCerts, SecureRandom())

        // Create an ssl socket factory with our all-trusting manager
        sslSocketFactory(sslContext.socketFactory, trustAllCerts[0])
        hostnameVerifier { hostname: String?, session: SSLSession? -> true }
      }
    }
  }
}
