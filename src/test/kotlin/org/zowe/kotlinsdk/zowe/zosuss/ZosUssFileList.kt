// Copyright © 2020 IBA Group, a.s. All rights reserved. Use of this source code is governed by Eclipse Public License – v 2.0 that can be found at: https://www.eclipse.org/legal/epl-2.0/

package org.zowe.kotlinsdk.zowe.zosfiles

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.zowe.kotlinsdk.zowe.*
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import org.zowe.kotlinsdk.zowe.client.sdk.zosfiles.ZosDsnList
import org.zowe.kotlinsdk.zowe.client.sdk.zosfiles.input.ListParams
import org.zowe.kotlinsdk.zowe.client.sdk.zosuss.ZosUssFileList
import org.zowe.kotlinsdk.zowe.client.sdk.zosuss.input.UssListParams
import okhttp3.OkHttpClient
import org.junit.jupiter.api.*
import java.net.InetSocketAddress
import java.net.Proxy
import kotlin.concurrent.thread


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZosUssFileList {
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
        val listParams = UssListParams()
        val zosUssFileList = ZosUssFileList(conn, proxyClient)
        responseDispatcher.injectEndpoint({
            it?.requestLine?.matches(Regex("GET http://.*/zosmf/restfiles/fs.* HTTP/.*")) == true
        }, {
            MockResponse().setBody(responseDispatcher.readMockJson("listDatasets") ?: "")
        })
        val fileList = zosUssFileList.listFiles("/u/STV", listParams)
        responseDispatcher.clearValidationList()
        Assertions.assertEquals(4, fileList.items.size)
    }
}
