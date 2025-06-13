/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf

// TODO: doc
// TODO: rework
//fun getUnsafeKTorHttpClient(): HttpClient {
//  return HttpClient(OkHttp) {
//    install(ContentNegotiation) {
//      gson()
//    }
//    engine {
//      config {
//        // Create a trust manager that does not validate certificate chains
//        val trustAllCerts = arrayOf<X509TrustManager>(
//          object : X509TrustManager {
//            @Throws(CertificateException::class)
//            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
//
//            @Throws(CertificateException::class)
//            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
//
//            override fun getAcceptedIssuers(): Array<X509Certificate> {
//              return arrayOf()
//            }
//          }
//        )
//
//        // Install the all-trusting trust manager
//        val sslContext = SSLContext.getInstance("TLSv1.2")
//        sslContext.init(null, trustAllCerts, SecureRandom())
//
//        // Create an ssl socket factory with our all-trusting manager
//        sslSocketFactory(sslContext.socketFactory, trustAllCerts[0])
//        hostnameVerifier { hostname: String?, session: SSLSession? -> true }
//      }
//    }
//  }
//}