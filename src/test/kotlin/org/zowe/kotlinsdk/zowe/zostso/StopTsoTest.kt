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

package org.zowe.kotlinsdk.zowe.zostso

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.zowe.kotlinsdk.zowe.*
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import org.zowe.kotlinsdk.zowe.client.sdk.zostso.StopTso
import org.zowe.kotlinsdk.zowe.client.sdk.zostso.input.StopTsoParams
import okhttp3.OkHttpClient
import org.junit.jupiter.api.*
import java.net.InetSocketAddress
import java.net.Proxy
import kotlin.concurrent.thread

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StopTsoTest {
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
  fun stopTsoTest() {
    val connection = ZOSConnection(TEST_HOST, TEST_PORT, TEST_USER, TEST_PASSWORD, "http")
    val stopTso = StopTso(connection, proxyClient)

    val stopTsoParams = StopTsoParams(
      servletKey = "DLIS-121-aabcaaat"
    )

    responseDispatcher.injectEndpoint(
      {
        it?.requestLine?.matches(Regex("DELETE http://.*/zosmf/tsoApp/tso/DLIS-121-aabcaaat HTTP/.*")) == true
      },
      {
        MockResponse().setBody(responseDispatcher.readMockJson("stopTso") ?: "")
      }
    )

    val stopCommonResponse = stopTso.stopCommon(stopTsoParams)
    val stopResponse = stopTso.stop("DLIS-121-aabcaaat")

    Assertions.assertEquals("DLIS-121-aabcaaat", stopResponse.servletKey)
    Assertions.assertEquals("DLIS-121-aabcaaat", stopCommonResponse.servletKey)

    responseDispatcher.clearValidationList()
  }

}
