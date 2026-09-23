/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk

import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.time.Duration
import java.util.logging.Logger
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Provides the [OkHttpClient] instances the SDK client classes use to talk to z/OSMF.
 *
 * By default, the client validates the server certificate chain against the JVM trust store and
 * verifies the host name. Certificate validation is skipped only when the caller explicitly asks
 * for it by setting [ZOSConnection.rejectUnauthorized] to `false`.
 */
object ZosmfOkHttpClient {

  private val log = Logger.getLogger(ZosmfOkHttpClient::class.java.name)

  private val timeout: Duration = Duration.ofMinutes(1)

  /**
   * Certificate and host name validating client. This is the default for all the SDK client classes.
   */
  val secureOkHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
      .readTimeout(timeout)
      .connectTimeout(timeout)
      .build()
  }

  /**
   * Client that accepts any certificate chain and any host name.
   * Only to be used when the connection explicitly opts out of certificate validation.
   */
  val insecureOkHttpClient: OkHttpClient by lazy {
    val trustAllCerts = arrayOf<TrustManager>(
      object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
      }
    )
    val sslContext = SSLContext.getInstance("TLSv1.2")
    sslContext.init(null, trustAllCerts, SecureRandom())
    OkHttpClient.Builder()
      .readTimeout(timeout)
      .connectTimeout(timeout)
      .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
      .hostnameVerifier { _, _ -> true }
      .build()
  }

  /**
   * Returns the HTTP client to be used for the provided connection.
   *
   * @param connection connection to build the client for
   * @return validating client, unless [ZOSConnection.rejectUnauthorized] is explicitly `false`
   */
  @JvmStatic
  fun getOkHttpClient(connection: ZOSConnection): OkHttpClient {
    return if (connection.rejectUnauthorized == false) {
      log.warning(
        "TLS certificate and host name validation is disabled for ${connection.protocol}://${connection.host}:" +
            "${connection.zosmfPort} as 'rejectUnauthorized' is set to false. The connection is vulnerable to " +
            "machine-in-the-middle attacks and must not be used in production."
      )
      insecureOkHttpClient
    } else {
      secureOkHttpClient
    }
  }
}
