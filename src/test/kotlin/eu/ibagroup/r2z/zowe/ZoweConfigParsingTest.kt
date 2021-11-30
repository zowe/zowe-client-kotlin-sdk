/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright © 2021 IBA Group, a.s.
 */

package eu.ibagroup.r2z.zowe

import com.squareup.okhttp.mockwebserver.MockWebServer
import eu.ibagroup.r2z.DataAPI
import eu.ibagroup.r2z.buildGsonApi
import eu.ibagroup.r2z.zowe.config.*
import okhttp3.Credentials
import okhttp3.OkHttpClient
import org.junit.jupiter.api.*
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Proxy
import kotlin.concurrent.thread

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZoweConfigParsingTest {

  lateinit var stringConfigJson: String
  lateinit var stringConfigYaml: String
  lateinit var streamConfigJson: InputStream
  lateinit var streamConfigYaml: InputStream
  lateinit var mockServer: MockWebServer
  lateinit var proxyClient: OkHttpClient
  val JSON_CONFIG = "zowe.config.json"
  val YAML_CONFIG = "zowe.config.yaml"

  @BeforeAll
  fun createMockServer () {
    mockServer = MockWebServer()
    mockServer.setDispatcher(MockResponseDispatcher(JSON_CONFIG))
    thread(start = true) {
      mockServer.play()
    }

    val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(mockServer.hostName, mockServer.port))
    proxyClient = OkHttpClient.Builder().proxy(proxy).build()
  }

  @AfterAll
  fun stopMockServer () {
    mockServer.shutdown()
  }

  private fun getResourceTextOrAssertError(resourceName: String): String {
    return javaClass.classLoader.getResource(resourceName)?.readText()
      ?: Assertions.fail("$resourceName cannot be empty.")
  }

  private fun getResourceStreamOrAssertError(resourceName: String): InputStream {
    return javaClass.classLoader.getResourceAsStream(resourceName)
      ?: Assertions.fail("$resourceName stream cannot be null.")
  }

  @BeforeEach
  fun readConfigs() {
    stringConfigJson = getResourceTextOrAssertError(JSON_CONFIG)
    stringConfigYaml = getResourceTextOrAssertError(YAML_CONFIG)
    streamConfigJson = getResourceStreamOrAssertError(JSON_CONFIG)
    streamConfigYaml = getResourceStreamOrAssertError(YAML_CONFIG)
  }

  @Test
  fun testParsingJsonString() {
    val zoweConfig = parseConfigJson(stringConfigJson)
    checkZoweConfig(zoweConfig)
  }

  @Test
  fun testParsingJsonStream() {
    val zoweConfig = parseConfigJson(streamConfigJson)
    checkZoweConfig(zoweConfig)
  }

  @Test
  fun testParsingYamlString() {
    val zoweConnection = parseConfigYaml(stringConfigYaml)
    checkZoweConnection(zoweConnection)
  }

  @Test
  fun testParsingYamlStream() {
    val zoweConnection = parseConfigYaml(streamConfigYaml)
    checkZoweConnection(zoweConnection)
  }

  @Test
  fun testAuthTokenCreation() {
    val zoweConfig = parseConfigJson(stringConfigJson)
    zoweConfig.user = null
    Assertions.assertThrows(IllegalStateException::class.java){ zoweConfig.getAuthEncoding() }
    zoweConfig.user = "user"
    zoweConfig.password = null
    Assertions.assertThrows(IllegalStateException::class.java){ zoweConfig.getAuthEncoding() }
    zoweConfig.password = "password"
    zoweConfig.host = ""
    Assertions.assertThrows(IllegalStateException::class.java){ zoweConfig.getAuthEncoding() }
    zoweConfig.host = "example.host"
    zoweConfig.port = null
    Assertions.assertThrows(IllegalStateException::class.java){ zoweConfig.getAuthEncoding() }
    zoweConfig.port = 443
    Assertions.assertDoesNotThrow { zoweConfig.getAuthEncoding() }
  }


  @Test
  fun readConfigAndListDatasets() {
    val zoweConfig = parseConfigJson(stringConfigJson)
    val authToken = Credentials.basic(zoweConfig.user ?: "", zoweConfig.password ?: "")

    val dataApi = buildGsonApi<DataAPI>("http://${zoweConfig.host}:${zoweConfig.port}", proxyClient)
    val response = dataApi.listDataSets(
      authorizationToken = authToken,
      dsLevel = "TEST.*"
    ).execute()
    if (response.isSuccessful) {
      val datasetLists = response.body()
      Assertions.assertEquals(datasetLists?.items?.size, 4)
    } else {
      Assertions.fail("response must be successful.")
    }
  }

  fun checkZoweConfig(zoweConfig: ZoweConfig) {
    Assertions.assertEquals(zoweConfig.user, "user")
    Assertions.assertEquals(zoweConfig.password, "password")
    Assertions.assertEquals(zoweConfig.host, "example.host")
    Assertions.assertEquals(zoweConfig.rejectUnauthorized, true)
    Assertions.assertEquals(zoweConfig.port, 443)
    Assertions.assertEquals(zoweConfig.protocol, "http")
    Assertions.assertEquals(zoweConfig.basePath, "/")
    Assertions.assertEquals(zoweConfig.encoding, 1047)
    Assertions.assertEquals(zoweConfig.responseTimeout, 600)
  }

  fun checkZoweConnection(zoweConnection: ZoweConnection) {
    Assertions.assertEquals(zoweConnection.user, "exampleUser")
    Assertions.assertEquals(zoweConnection.password, "examplePassword")
    Assertions.assertEquals(zoweConnection.host, "host.example")
    Assertions.assertEquals(zoweConnection.rejectUnauthorized, false)
    Assertions.assertEquals(zoweConnection.port, 10443)
    Assertions.assertEquals(zoweConnection.protocol, "https")
    Assertions.assertEquals(zoweConnection.basePath, "/")
    Assertions.assertEquals(zoweConnection.encoding, 1047)
    Assertions.assertEquals(zoweConnection.responseTimeout, 600)
  }
}
