// Copyright © 2020 IBA Group, a.s. All rights reserved. Use of this source code is governed by Eclipse Public License – v 2.0 that can be found at: https://www.eclipse.org/legal/epl-2.0/

package org.zowe.kotlinsdk.zowe.zosfiles

import com.google.gson.Gson
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.zowe.kotlinsdk.CreateDataset
import org.zowe.kotlinsdk.DatasetOrganization
import org.zowe.kotlinsdk.RecordFormat
import org.zowe.kotlinsdk.SpaceUnits
import org.zowe.kotlinsdk.zowe.*
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import org.zowe.kotlinsdk.zowe.client.sdk.zosfiles.ZosDsn
import okhttp3.OkHttpClient
import org.junit.jupiter.api.*
import java.net.InetSocketAddress
import java.net.Proxy
import kotlin.concurrent.thread

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZosDsnTest {
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
  fun getExistentDatasetInfo() {
    val connection = ZOSConnection(TEST_HOST, TEST_PORT, TEST_USER, TEST_PASSWORD, "http")
    val zosDsn = ZosDsn(connection, proxyClient)

    responseDispatcher.injectEndpoint(
      { it?.requestLine?.matches(Regex("GET http://.*/zosmf/restfiles/ds.* HTTP/.*")) == true },
      { MockResponse().setBody(responseDispatcher.readMockJson("listDatasets") ?: "") }
    )
    val datasetInfo = zosDsn.getDatasetInfo("TEST.IJMP.DATASET1")
    Assertions.assertEquals(4500, datasetInfo.blockSize)
    Assertions.assertEquals(150, datasetInfo.recordLength)
    Assertions.assertEquals(SpaceUnits.TRACKS, datasetInfo.spaceUnits)

    responseDispatcher.clearValidationList()
  }

  @Test
  fun getNonExistentDatasetInfo() {
    val connection = ZOSConnection(TEST_HOST, TEST_PORT, TEST_USER, TEST_PASSWORD, "http")
    val zosDsn = ZosDsn(connection, proxyClient)

    responseDispatcher.injectEndpoint(
      { it?.requestLine?.matches(Regex("GET http://.*/zosmf/restfiles/ds.* HTTP/.*")) == true },
      { MockResponse().setBody(responseDispatcher.readMockJson("listDatasets") ?: "") }
    )
    val datasetInfo = zosDsn.getDatasetInfo("TEST.IJMP.DATASET5")
    Assertions.assertEquals(null, datasetInfo.blockSize)

    responseDispatcher.clearValidationList()
  }

  @Test
  fun deleteDsn() {
    val connection = ZOSConnection(TEST_HOST, TEST_PORT, TEST_USER, TEST_PASSWORD, "http")
    val zosDsn = ZosDsn(connection, proxyClient)

    responseDispatcher.injectEndpoint(
      {
        it?.requestLine?.matches(Regex("DELETE http://.*/zosmf/restfiles/ds/TEST.IJMP.DATASET HTTP/.*")) == true
      },
      { MockResponse().setResponseCode(204) }
    )
    val response = zosDsn.deleteDsn("TEST.IJMP.DATASET")
    Assertions.assertEquals(204, response.code())

    responseDispatcher.clearValidationList()
  }

  @Test
  fun deleteDsnMember() {
    val connection = ZOSConnection(TEST_HOST, TEST_PORT, TEST_USER, TEST_PASSWORD, "http")
    val zosDsn = ZosDsn(connection, proxyClient)

    responseDispatcher.injectEndpoint(
      { it?.requestLine?.matches(Regex("DELETE http://.*/zosmf/restfiles/ds/TEST.IJMP.DATASET\\(TESTMEM\\) HTTP/.*")) == true },
      { MockResponse().setResponseCode(204) }
    )
    val response = zosDsn.deleteDsn("TEST.IJMP.DATASET", "TESTMEM")
    Assertions.assertEquals(204, response.code())

    responseDispatcher.clearValidationList()
  }

  @Test
  fun writeDsn() {
    val conn = ZOSConnection(TEST_HOST, TEST_PORT, TEST_USER, TEST_PASSWORD, "http")
    val zosDsn = ZosDsn(conn, proxyClient)
    val memberText = "member"
    val dsnText = "dataset"
    responseDispatcher.injectEndpoint(
      {
        it?.requestLine?.matches(Regex("PUT http://.*/zosmf/restfiles/ds/TEST\\.IJMP\\.DATASET1(\\(TEST\\))? HTTP/.*")) == true
      },
      {
        val textToCheck = if (it?.requestLine?.contains(Regex("TEST.IJMP.DATASET1\\(TEST\\)")) == true) memberText else dsnText
        Assertions.assertEquals(it?.body?.readUtf8(), textToCheck)
        MockResponse().setResponseCode(204)
      }
    )
    val datasetResponse = zosDsn.writeDsn("TEST.IJMP.DATASET1", dsnText.toByteArray())
    Assertions.assertEquals(204, datasetResponse.code())
    val memberResponse = zosDsn.writeDsn("TEST.IJMP.DATASET1", "TEST", memberText.toByteArray())
    Assertions.assertEquals(204, memberResponse.code())

    responseDispatcher.clearValidationList()
  }

  @Test
  fun createDsn() {
    val connection = ZOSConnection(TEST_HOST, TEST_PORT, TEST_USER, TEST_PASSWORD, "http")
    val zosDsn = ZosDsn(connection, proxyClient)
    val params = CreateDataset(
      datasetOrganization = DatasetOrganization.PO,
      primaryAllocation = 10,
      secondaryAllocation = 5,
      recordFormat = RecordFormat.FB
    )

    responseDispatcher.injectEndpoint(
      {
        it?.requestLine?.matches(Regex("POST http://.*/zosmf/restfiles/ds/TEST.IJMP.DATASET HTTP/.*")) == true &&
            Gson().fromJson(it.body.readUtf8(), CreateDataset::class.java) == params
      },
      { MockResponse().setResponseCode(201) }
    )
    val response = zosDsn.createDsn("TEST.IJMP.DATASET", params)
    Assertions.assertEquals(201, response.code())

    responseDispatcher.clearValidationList()
  }
}
