/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBA Group 2020
 */

package org.zowe.kotlinsdk.zowe

import org.zowe.kotlinsdk.zowe.config.WINDOWS_MAX_PASSWORD_LENGTH
import org.junit.jupiter.api.TestInstance
import org.zowe.kotlinsdk.zowe.config.ZoweConfig
import org.zowe.kotlinsdk.zowe.config.parseConfigJson
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZoweCredentialsTest: ZoweConfigTestBase() {

  @Test
  fun testExtractCredentials () {
    val zoweConfig = parseConfigJson(stringConfigJson)
    Assertions.assertEquals(null, zoweConfig.user)
    Assertions.assertEquals(null, zoweConfig.password)
    zoweConfig.extractSecureProperties(TEST_ZOWE_CONFIG_PATH, DefaultMockKeytarWrapper())
    Assertions.assertEquals(TEST_USER, zoweConfig.user)
    Assertions.assertEquals(TEST_PASSWORD, zoweConfig.password)
  }

  @Test
  fun testSaveCredentials () {
    val zoweConfig = parseConfigJson(stringConfigJson)
    val testKeytarWrapper = object: DefaultMockKeytarWrapper() {
      override fun setPassword(service: String, account: String, password: String) {
        Assertions.assertEquals(ZoweConfig.ZOWE_SERVICE_BASE, service)
        Assertions.assertEquals(ZoweConfig.ZOWE_SECURE_ACCOUNT, account)
        Assertions.assertEquals(createSinglePassword(TEST_ZOWE_CONFIG_PATH, "testU", "testP"), password)
      }
    }
    zoweConfig.extractSecureProperties(TEST_ZOWE_CONFIG_PATH, testKeytarWrapper)
    zoweConfig.user = "testU"
    zoweConfig.password = "testP"
    zoweConfig.saveSecureProperties(TEST_ZOWE_CONFIG_PATH, testKeytarWrapper)
  }

  @Test
  fun testExtractAndSaveTooLargeCredentials () {
    System.setProperty("os.name", "Windows")
    var chunksAmount: Int

    val testKeytarWrapper = object: DefaultMockKeytarWrapper() {
      val credentialsMap: MutableMap<String, String>

      init {
        val filePaths = arrayOfNulls<String?>(70).mapIndexedNotNull { i, _ -> "${TEST_ZOWE_CONFIG_PATH}-${i+1}" }
        credentialsMap = createMultiplePasswords(filePaths, TEST_USER, TEST_PASSWORD)
          .chunked(WINDOWS_MAX_PASSWORD_LENGTH)
          .mapIndexed { i, chunk -> Pair("${ZoweConfig.ZOWE_SECURE_ACCOUNT}-${i+1}", chunk) }
          .associateBy({ it.first }, { it.second })
          .toMutableMap()
        chunksAmount = credentialsMap.size
      }

      override fun getCredentials(service: String): Map<String, String> {
        val accountName = service.split("/").last()
        val accountChunk = credentialsMap[accountName] ?: return emptyMap()
        return mapOf(Pair(accountName, accountChunk))
      }

      override fun setPassword(service: String, account: String, password: String) {
        Assertions.assertEquals(credentialsMap[account], password)
        credentialsMap.remove(account)
        --chunksAmount
      }
    }
    val zoweConfig = parseConfigJson(stringConfigJson)
    zoweConfig.extractSecureProperties("$TEST_ZOWE_CONFIG_PATH-1", testKeytarWrapper)
    zoweConfig.saveSecureProperties("$TEST_ZOWE_CONFIG_PATH-1", testKeytarWrapper)
    Assertions.assertEquals(0, chunksAmount)
  }

}
