/*
 * Copyright (c) 2020-2024 IBA Group.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   IBA Group
 *   Zowe Community
 */

package common

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.zowe.kotlinsdk.DataAPI
import org.zowe.kotlinsdk.zowe.config.KeytarWrapper
import org.zowe.kotlinsdk.zowe.config.ZoweConfig
import org.zowe.kotlinsdk.zowe.config.getAuthEncoding
import org.zowe.kotlinsdk.zowe.config.withBasicPrefix

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZoweConfigParsingTest() {

  lateinit var mockServer: MockWebServer
  lateinit var proxyClient: OkHttpClient
  lateinit var keytarWrapper: KeytarWrapper
  lateinit var zoweConfig: ZoweConfig

  @Test
  fun readConfigAndListDatasets() {
    val authToken = zoweConfig.getAuthEncoding().withBasicPrefix()

    val dataApi = buildGsonApi<DataAPI>("http://${zoweConfig.host}:${zoweConfig.port}", proxyClient)
    val response = dataApi
      .listDataSets(
        authorizationToken = authToken,
        dsLevel = "TEST.*"
      )
      .execute()
    if (response.isSuccessful) {
      val datasetLists = response.body()
      Assertions.assertEquals(datasetLists?.items?.size, 4)
    } else {
      Assertions.fail("response must be successful.")
    }
  }

}
