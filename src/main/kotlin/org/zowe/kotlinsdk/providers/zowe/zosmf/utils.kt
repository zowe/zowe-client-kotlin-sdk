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

import io.ktor.http.encodeURLPath

/**
 * Produce the USS path part of a z/OSMF REST API URL.
 * The path is appended to the "/zosmf/restfiles/fs" base path as is, so the leading slash
 * of an absolute USS path is removed to avoid the doubled slash in the resulting URL.
 * All the characters that are not allowed in a URL path (a space in a file name, e.g.) are encoded,
 * the path separators are kept as is
 * @param ussPath the USS file or directory path to process
 * @return the encoded path part, ready to be appended to the base path
 */
fun produceUssPathPart(ussPath: String): String {
  return ussPath.trimStart('/').encodeURLPath()
}

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