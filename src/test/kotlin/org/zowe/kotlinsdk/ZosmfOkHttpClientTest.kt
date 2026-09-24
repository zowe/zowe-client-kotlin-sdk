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

import org.junit.jupiter.api.Test
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import org.zowe.kotlinsdk.zowe.client.sdk.zosjobs.GetJobs

internal class ZosmfOkHttpClientTest {

  private fun connection(rejectUnauthorized: Boolean?) =
    ZOSConnection("test.host.com", "10443", "user", "password", rejectUnauthorized = rejectUnauthorized)

  @Test
  fun validatesCertificatesByDefault() {
    val client = ZosmfOkHttpClient.getOkHttpClient(connection(null))
    assert(client === ZosmfOkHttpClient.secureOkHttpClient)
    assert(client.hostnameVerifier !== ZosmfOkHttpClient.insecureOkHttpClient.hostnameVerifier)
  }

  @Test
  fun validatesCertificatesWhenRejectUnauthorizedIsTrue() {
    assert(ZosmfOkHttpClient.getOkHttpClient(connection(true)) === ZosmfOkHttpClient.secureOkHttpClient)
  }

  @Test
  fun skipsValidationOnlyWhenExplicitlyRequested() {
    assert(ZosmfOkHttpClient.getOkHttpClient(connection(false)) === ZosmfOkHttpClient.insecureOkHttpClient)
  }

  @Test
  fun clientClassesUseValidatingClientByDefault() {
    assert(GetJobs(connection(true)).httpClient === ZosmfOkHttpClient.secureOkHttpClient)
    assert(GetJobs(connection(null)).httpClient === ZosmfOkHttpClient.secureOkHttpClient)
    assert(GetJobs(connection(false)).httpClient === ZosmfOkHttpClient.insecureOkHttpClient)
  }
}
