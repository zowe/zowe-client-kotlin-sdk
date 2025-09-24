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
    Assertions.assertEquals(zoweConfig.host, "example.host1")
    Assertions.assertEquals(zoweConfig.rejectUnauthorized, true)
    Assertions.assertEquals(zoweConfig.port, 10443)
    Assertions.assertEquals(zoweConfig.protocol, "https")
    Assertions.assertEquals(zoweConfig.basePath, "/")
    Assertions.assertEquals(zoweConfig.encoding, 1047)
    Assertions.assertEquals(zoweConfig.responseTimeout, 600)
    Assertions.assertEquals(zoweConfig.toJson(), stringConfigJson)
    val newZoweConfig = ZoweConfig("./zowe.schema.json",zoweConfig.profiles,zoweConfig.defaults)
    Assertions.assertEquals(newZoweConfig.toJson(), stringConfigJson)
  }

  fun fullProfileName(prof: ZoweConfigProfile?, separator: String = "."): String {
    var v: ZoweConfigProfile? = prof
    val currProfile = mutableListOf<String>()
    while (v != null) {
      currProfile.add(v.name.toString())
      v = v.parentProfile
    }
    currProfile.reverse()
    return currProfile.fold("") { curr, next -> "$curr$separator$next" }.substring(separator.length)
  }

  fun checkZosmfProfile(zoweConfig: ZoweConfig) {
    Assertions.assertEquals(fullProfileName(zoweConfig.zosmfProfile), "lpar1.zosmf")
    zoweConfig.setProfile("lpar1.section1.testParametersProfile")
    Assertions.assertEquals(fullProfileName(zoweConfig.zosmfProfile), "lpar1.section1.testParametersProfile")
    zoweConfig.rejectUnauthorized = null
    Assertions.assertEquals(zoweConfig.rejectUnauthorized, true)
    zoweConfig.rejectUnauthorized = true
    Assertions.assertEquals(zoweConfig.rejectUnauthorized, true)
    zoweConfig.protocol = "http"
    Assertions.assertEquals(zoweConfig.protocol, "http")
    zoweConfig.basePath = "/"
    Assertions.assertEquals(zoweConfig.basePath, "/")
    zoweConfig.encoding = 1037
    Assertions.assertEquals(zoweConfig.encoding, 1037)
    zoweConfig.responseTimeout = 300
    Assertions.assertEquals(zoweConfig.responseTimeout, 300)
    Assertions.assertEquals(zoweConfig.user, "zosmfUser")
    zoweConfig.user = null
    Assertions.assertEquals(zoweConfig.user, "")
    zoweConfig.user = "zUser"
    Assertions.assertEquals(zoweConfig.user, "zUser")
    Assertions.assertEquals(zoweConfig.password, "zosmfPassword")
    zoweConfig.password = null
    Assertions.assertEquals(zoweConfig.password, "")
    zoweConfig.password = "zPassword"
    Assertions.assertEquals(zoweConfig.password, "zPassword")
    zoweConfig.restoreProfile()
    Assertions.assertEquals(fullProfileName(zoweConfig.zosmfProfile), "lpar1.zosmf")
    Assertions.assertEquals(zoweConfig.sshProfile?.name, "ssh")
    Assertions.assertEquals(zoweConfig.tsoProfile?.name, "tso")
    Assertions.assertNull(zoweConfig.profile(null))
    Assertions.assertNull(zoweConfig.profile("."))
    zoweConfig.setProfile("lpar1.section1.section2.emptyZosmfProfile")
    Assertions.assertEquals(zoweConfig.user, "testUser")
    zoweConfig.user = "zUser1"
    Assertions.assertEquals(zoweConfig.user, "zUser1")
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
