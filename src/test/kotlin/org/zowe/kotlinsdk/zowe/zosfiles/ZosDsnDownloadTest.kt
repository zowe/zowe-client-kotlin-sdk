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

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.zowe.kotlinsdk.zowe.*
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import org.zowe.kotlinsdk.zowe.client.sdk.zosfiles.ZosDsnDownload
import org.zowe.kotlinsdk.zowe.client.sdk.zosfiles.input.DownloadParams
import okhttp3.OkHttpClient
import org.junit.jupiter.api.*
import java.net.InetSocketAddress
import java.net.Proxy
import kotlin.concurrent.thread

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZosDsnDownloadTest {
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
  fun testDownloadDsn() {
    val conn = ZOSConnection(TEST_HOST, TEST_PORT, TEST_USER, TEST_PASSWORD, "http")
    val zosDsnDownload = ZosDsnDownload(conn, proxyClient)
    val downloadParams = DownloadParams(
    )
    val responseBody = javaClass.classLoader.getResource("mock/downloadDsnMember.txt")?.readText() ?: ""
    responseDispatcher.injectEndpoint(
      {
        it?.requestLine?.matches(Regex("GET http://.*/zosmf/restfiles/ds/TEST.JCL\\(TESTJOB\\) HTTP/.*")) == true
      },
      { MockResponse().setBody(responseBody) }
    )
    val stream = zosDsnDownload.downloadDsn("TEST.JCL(TESTJOB)", downloadParams)
    val content = stream.bufferedReader().use { it.readText() }
    Assertions.assertEquals(responseBody, content)

    responseDispatcher.clearValidationList()
  }

}
