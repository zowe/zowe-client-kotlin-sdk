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

import eu.ibagroup.r2z.zowe.config.ZoweConfig
import eu.ibagroup.r2z.zowe.config.ZoweConnection
import eu.ibagroup.r2z.zowe.config.parseConfigJson
import eu.ibagroup.r2z.zowe.config.parseConfigYaml
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.InputStream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZoweConfigParsingTest {

  lateinit var stringConfigJson: String
  lateinit var stringConfigYaml: String
  lateinit var streamConfigJson: InputStream
  lateinit var streamConfigYaml: InputStream

  @BeforeEach
  fun readConfigs() {
    stringConfigJson = javaClass.classLoader.getResource("zowe.config.json")?.readText()
      ?: Assertions.fail("zowe config json cannot be empty.")
    stringConfigYaml = javaClass.classLoader.getResource("config.yaml")?.readText()
      ?: Assertions.fail("zowe config yaml cannot be empty.")
    streamConfigJson = javaClass.classLoader.getResourceAsStream("zowe.config.json")
      ?: Assertions.fail("zowe config json stream cannot be null.")
    streamConfigYaml = javaClass.classLoader.getResourceAsStream("config.yaml")
      ?: Assertions.fail("zowe config yaml stream cannot be null.")
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
