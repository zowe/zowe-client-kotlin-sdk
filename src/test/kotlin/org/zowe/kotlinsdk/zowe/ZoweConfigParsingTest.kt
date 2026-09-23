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
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.zowe

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*
import org.zowe.kotlinsdk.exceptions.EmptyZoweConfigFileException
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import org.zowe.kotlinsdk.zowe.config.*
import java.net.InetSocketAddress
import java.net.Proxy

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZoweConfigParsingTest: ZoweConfigTestBase() {

  lateinit var mockServer: MockWebServer
  lateinit var proxyClient: OkHttpClient
  lateinit var keytarWrapper: KeytarWrapper
  lateinit var zoweConfig: ZoweConfig

  @BeforeAll
  fun createMockServer () {
    keytarWrapper = DefaultMockKeytarWrapper()
    mockServer = MockWebServer()
    mockServer.dispatcher = MockResponseDispatcher()
    mockServer.start()
    val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(mockServer.hostName, mockServer.port))
    proxyClient = OkHttpClient.Builder().proxy(proxy).build()
  }

  @AfterAll
  fun stopMockServer () {
    mockServer.shutdown()
  }

  @BeforeEach
  fun uploadConfig () {
    zoweConfig = parseConfigJson(stringConfigJson)
    zoweConfig.extractSecureProperties(TEST_ZOWE_CONFIG_PATH, keytarWrapper)
  }

  @Test
  fun testParsingJsonString() {
    checkZoweConfig(zoweConfig)
  }

  @Test
  fun testMainZosmfProfile() {
    checkZosmfProfile(zoweConfig)
  }

  @Test
  fun testParsingJsonStream() {
    val zoweConfig = parseConfigJson(streamConfigJson)
    zoweConfig.extractSecureProperties(TEST_ZOWE_CONFIG_PATH, keytarWrapper)
    checkZoweConfig(zoweConfig)
  }

  @Test
  fun testParsingEmptyJsonString() {
    Assertions.assertThrows(EmptyZoweConfigFileException::class.java){ parseConfigJson("") }
  }

  @Test
  fun testZOSConnection() {
    val zoweConfig = parseConfigJson(streamConfigJson)
    zoweConfig.extractSecureProperties(TEST_ZOWE_CONFIG_PATH, keytarWrapper)
    checkGetListOfZosmfConections(zoweConfig)
    checkToZosConnection(zoweConfig)
  }

  @Test
  fun testParsingYamlString() {
    val zoweConnection = parseConfigYaml(stringConfigYaml)
    checkZoweConnection(zoweConnection)
  }

  @Test
  fun testParsingMinimalYamlUsesSecureDefaults() {
    val zoweConnection = parseConfigYaml(
      """
      host: host.example
      port: 10443
      user: exampleUser
      password: examplePassword
      """.trimIndent()
    )
    Assertions.assertEquals("https", zoweConnection.protocol)
    Assertions.assertEquals(true, zoweConnection.rejectUnauthorized)
  }

  @Test
  fun testParsingYamlStream() {
    val zoweConnection = parseConfigYaml(streamConfigYaml)
    checkZoweConnection(zoweConnection)
  }

  @Test
  fun testAuthTokenCreation() {
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

  fun checkZoweConfig(zoweConfig: ZoweConfig) {
    Assertions.assertEquals(zoweConfig.user, TEST_USER)
    Assertions.assertEquals(zoweConfig.password, TEST_PASSWORD)
    Assertions.assertEquals("example.host1", zoweConfig.host)
    Assertions.assertEquals(true, zoweConfig.rejectUnauthorized)
    Assertions.assertEquals(10443, zoweConfig.port)
    Assertions.assertEquals("https", zoweConfig.protocol)
    Assertions.assertEquals("/", zoweConfig.basePath)
    Assertions.assertEquals(1047, zoweConfig.encoding)
    Assertions.assertEquals(600, zoweConfig.responseTimeout)
    Assertions.assertEquals(zoweConfig.toJson(), stringConfigJson)
    val newZoweConfig = ZoweConfig("./zowe.schema.json",zoweConfig.profiles,zoweConfig.defaults)
    Assertions.assertEquals(newZoweConfig.toJson(), stringConfigJson)
  }

  fun fullProfileName(prof: ZoweConfigProfile?, separator: String = "."): String {
    var v: ZoweConfigProfile? = prof
    val currProfile = mutableListOf<String>()
    while (v != null) {
      currProfile.add(v.name)
      v = v.parentProfile
    }
    currProfile.reverse()
    return currProfile.fold("") { curr, next -> "$curr$separator$next" }.substring(separator.length)
  }

  fun checkZosmfProfile(zoweConfig: ZoweConfig) {
    Assertions.assertEquals("lpar1.zosmf", fullProfileName(zoweConfig.zosmfProfile))
    zoweConfig.setProfile("lpar1.section1.testParametersProfile")
    Assertions.assertEquals("lpar1.section1.testParametersProfile", fullProfileName(zoweConfig.zosmfProfile))
    zoweConfig.rejectUnauthorized = null
    Assertions.assertEquals(true, zoweConfig.rejectUnauthorized)
    zoweConfig.rejectUnauthorized = true
    Assertions.assertEquals(true, zoweConfig.rejectUnauthorized)
    zoweConfig.protocol = "http"
    Assertions.assertEquals("http", zoweConfig.protocol)
    zoweConfig.basePath = "/"
    Assertions.assertEquals("/", zoweConfig.basePath)
    zoweConfig.encoding = 1037
    Assertions.assertEquals(1037, zoweConfig.encoding)
    zoweConfig.responseTimeout = 300
    Assertions.assertEquals(300, zoweConfig.responseTimeout)
    Assertions.assertEquals("zosmfUser", zoweConfig.user)
    zoweConfig.user = null
    Assertions.assertEquals("", zoweConfig.user)
    zoweConfig.user = "zUser"
    Assertions.assertEquals("zUser", zoweConfig.user)
    Assertions.assertEquals("zosmfPassword", zoweConfig.password)
    zoweConfig.password = null
    Assertions.assertEquals("", zoweConfig.password)
    zoweConfig.password = "zPassword"
    Assertions.assertEquals("zPassword", zoweConfig.password)
    zoweConfig.restoreProfile()
    Assertions.assertEquals("lpar1.zosmf", fullProfileName(zoweConfig.zosmfProfile))
    Assertions.assertEquals("ssh", zoweConfig.sshProfile?.name)
    Assertions.assertEquals("tso", zoweConfig.tsoProfile?.name)
    Assertions.assertNull(zoweConfig.profile(null))
    Assertions.assertNull(zoweConfig.profile("."))
    zoweConfig.setProfile("lpar1.section1.section2.emptyZosmfProfile")
    Assertions.assertEquals("testUser", zoweConfig.user)
    zoweConfig.user = "zUser1"
    Assertions.assertEquals("zUser1", zoweConfig.user)
    zoweConfig.extractSecureProperties("/wrong/zowe/config/path", keytarWrapper)
  }

  fun checkGetListOfZosmfConections(zoweConfig: ZoweConfig) {
    val allZosConn = mutableListOf<ZOSConnection>()
    allZosConn.add(ZOSConnection("example.host", "443", "testUser", "testPassword", profileName = "zosmf"))
    allZosConn.add(
      ZOSConnection(
        "example.host2",
        "443",
        "zosmfUser",
        "zosmfPassword",
        profileName = "lpar1.section1.testParametersProfile",
        rejectUnauthorized = false,
        basePath = "/api",
        encoding = 37
      )
    )
    allZosConn.add(ZOSConnection("example.host1", "10443", "testUser", "testPassword", profileName = "lpar1.zosmf"))
    Assertions.assertArrayEquals(zoweConfig.getListOfZosmfConnections().toTypedArray(), allZosConn.toTypedArray())
  }

  fun checkToZosConnection(zoweConfig: ZoweConfig) {
    zoweConfig.user = "user"
    zoweConfig.host = ""
    Assertions.assertThrows(IllegalStateException::class.java) { zoweConfig.toZosConnection() }
    zoweConfig.host = "example.host"
    zoweConfig.port = null
    Assertions.assertThrows(IllegalStateException::class.java) { zoweConfig.toZosConnection() }
    zoweConfig.port = 443
    zoweConfig.host = null
    zoweConfig.password = null
    Assertions.assertDoesNotThrow { zoweConfig.toZosConnection() }
  }

  fun checkZoweConnection(zoweConnection: ZoweConnection) {
    Assertions.assertEquals("exampleUser", zoweConnection.user)
    Assertions.assertEquals("examplePassword", zoweConnection.password)
    Assertions.assertEquals("host.example", zoweConnection.host)
    Assertions.assertEquals(false, zoweConnection.rejectUnauthorized)
    Assertions.assertEquals(10443, zoweConnection.port)
    Assertions.assertEquals("https", zoweConnection.protocol)
    Assertions.assertEquals("/", zoweConnection.basePath)
    Assertions.assertEquals(1047, zoweConnection.encoding)
    Assertions.assertEquals(600, zoweConnection.responseTimeout)
  }
}
