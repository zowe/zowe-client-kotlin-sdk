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
import org.zowe.kotlinsdk.zowe.client.sdk.zosfiles.ZosDsnList
import org.zowe.kotlinsdk.zowe.client.sdk.zosfiles.input.ListParams
import okhttp3.OkHttpClient
import org.junit.jupiter.api.*
import java.net.InetSocketAddress
import java.net.Proxy
import kotlin.concurrent.thread


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZosDsnListTest {
  lateinit var mockServer: MockWebServer
  lateinit var proxyClient: OkHttpClient
  lateinit var responseDispatcher: MockResponseDispatcher

  @BeforeAll
  fun createMockServer () {
    mockServer = MockWebServer()
    responseDispatcher = MockResponseDispatcher()
    mockServer.dispatcher = responseDispatcher
    mockServer.start()
    val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(mockServer.hostName, mockServer.port))
    proxyClient = OkHttpClient.Builder().proxy(proxy).build()
  }

  @AfterAll
  fun stopMockServer () {
    mockServer.shutdown()
  }

  @Test
  fun testListDsn () {
    val conn = ZOSConnection(TEST_HOST, TEST_PORT, TEST_USER, TEST_PASSWORD, "http")
    val listParams = ListParams(
      volume = "TESTVOL"
    )
    val zosDsnList = ZosDsnList(conn, proxyClient)
    responseDispatcher.injectEndpoint({
      it?.requestLine?.matches(Regex("GET http://.*/zosmf/restfiles/ds.* HTTP/.*")) == true
    }, {
      MockResponse().setBody(responseDispatcher.readMockJson("listDatasets") ?: "")
    })
    val dsnList = zosDsnList.listDsn("TEST.**.TEST1", listParams)
    responseDispatcher.clearValidationList()
    Assertions.assertEquals(4, dsnList.items.size)
  }

  @Test
  fun testListDsnMembers () {
    val conn = ZOSConnection(TEST_HOST, TEST_PORT, TEST_USER, TEST_PASSWORD, "http")
    val listParams = ListParams()
    val zosDsnList = ZosDsnList(conn, proxyClient)
    responseDispatcher.injectEndpoint({
      it?.requestLine?.matches(Regex("GET http://.*/zosmf/restfiles/ds.* HTTP/.*")) == true
    }, {
      MockResponse().setBody(responseDispatcher.readMockJson("listDatasetMembers") ?: "")
    })
    val dsnMembersList = zosDsnList.listDsnMembers("SYS1.PROCLIB", listParams)
    Assertions.assertEquals(87, dsnMembersList.items.size)
    responseDispatcher.clearValidationList()
  }
}
