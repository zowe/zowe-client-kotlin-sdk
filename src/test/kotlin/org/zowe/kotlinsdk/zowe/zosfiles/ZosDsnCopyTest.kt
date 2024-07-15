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

package org.zowe.kotlinsdk.zowe.zosfiles

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*
import org.zowe.kotlinsdk.zowe.*
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import org.zowe.kotlinsdk.zowe.client.sdk.zosfiles.ZosDsnCopy
import org.zowe.kotlinsdk.zowe.client.sdk.zosfiles.input.CopyParams
import java.net.InetSocketAddress
import java.net.Proxy

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZosDsnCopyTest {
  lateinit var mockServer: MockWebServer
  lateinit var proxyClient: OkHttpClient
  lateinit var responseDispatcher: MockResponseDispatcher

  @BeforeAll
  fun createMockServer() {
    mockServer = MockWebServer()
    responseDispatcher = MockResponseDispatcher()
    mockServer.dispatcher = responseDispatcher
    mockServer.start()
    val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(mockServer.hostName, mockServer.port))
    proxyClient = OkHttpClient.Builder().proxy(proxy).build()
  }

  @AfterAll
  fun stopMockServer() {
    mockServer.shutdown()
  }

  @Test
  fun testCopyDsn() {
    val conn = ZOSConnection(TEST_HOST, TEST_PORT, TEST_USER, TEST_PASSWORD, "http")
    val zosDsnCopy = ZosDsnCopy(conn, proxyClient)
    val copyParams = CopyParams(
      fromDataSet = "TEST.JCL(TESTJOB)",
      toDataSet = "NBEL.TEST.DATA",
      replace = true
    )
    responseDispatcher.injectEndpoint(
      {
        it?.requestLine?.matches(Regex("PUT http://.*/zosmf/restfiles/ds/NBEL.TEST.DATA HTTP/.*")) == true
      },
      {
        MockResponse().setResponseCode(200)
      }
    )
    val response = zosDsnCopy.copy(copyParams)
    Assertions.assertEquals(200, response.code())

    responseDispatcher.clearValidationList()
  }

  @Test
  fun testCopy() {
    val conn = ZOSConnection(TEST_HOST, TEST_PORT, TEST_USER, TEST_PASSWORD, "http")
    val zosDsnCopy = ZosDsnCopy(conn, proxyClient)
    responseDispatcher.injectEndpoint(
      {
        it?.requestLine?.matches(Regex("PUT http://.*/zosmf/restfiles/ds/NBEL.TEST.DATA HTTP/.*")) == true
      }, {
        MockResponse().setResponseCode(200)
      }
    )
    val response = zosDsnCopy.copy("TEST.JCL(TESTJOB)", "NBEL.TEST.DATA", replace = true, copyAllMembers = false)
    Assertions.assertEquals(200, response.code())

    responseDispatcher.clearValidationList()
  }

}
