/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.core

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.LoggerFactory
import org.zowe.kotlinsdk.secrets.NativeSecretsLoader
import org.zowe.kotlinsdk.secrets.ZoweSecrets
import java.util.Base64

const val ZOWE_SERVICE_NAME = "Zowe"
const val ZOWE_ACCOUNT_NAME = "secure_config_props"
const val WINDOWS_CRED_MAX_STRING_LENGTH = 2560

/**
 * Exception to produce when secure profile load is failed
 * @param reasonFailed the reason the load is failed
 * @param originException the original [Exception] produced during a load try
 */
class SecureProfileLoadException(
  reasonFailed: String,
  originException: Exception
) : Exception(reasonFailed, originException)

/**
 * A class including static functions for managing credentials
 * Based on the https://github.com/zowe/zowe-client-python-sdk/blob/main/src/core/zowe/core_for_zowe_sdk/credential_manager.py.
 * Uses https://www.npmjs.com/package/@zowe/secrets-for-zowe-sdk
 */
open class ZoweCredentialManager {
  companion object {
    private val logger = LoggerFactory.getLogger(ZoweCredentialManager::class.java)
    var secureProps: MutableMap<String, Any> = mutableMapOf()

    /** Load [secureProps] stored for the given config file */
    fun loadSecureProps() {
      if (!NativeSecretsLoader.HAS_KEYRING) {
        return
      }

      val encodedSecretValue = try {
        getCredential() ?: return
      } catch (e: Exception) {
        val secureProfileLoadFailedMsg = "Failed to load secure profile Zowe: ${e.message}"
        val secureProfileLoadFailedException = SecureProfileLoadException(
          secureProfileLoadFailedMsg,
          e
        )
        logger.error(secureProfileLoadFailedMsg, secureProfileLoadFailedException)
        throw secureProfileLoadFailedException
      }
      val encodedSecretValueAsBytes = encodedSecretValue.toByteArray()
      val decodedSecretValue = String(Base64.getDecoder().decode(encodedSecretValueAsBytes))
      val securePropsAsJsonElement = Json.parseToJsonElement(decodedSecretValue)
      secureProps = securePropsAsJsonElement.jsonObject
        .toMap()
        .mapValues { (_, configSecretValues) ->
          configSecretValues.jsonObject
            .toMap()
            .mapValues { (_, propValueAsJson) -> propValueAsJson.jsonPrimitive.content }
        }
        .toMutableMap()
    }

    /** Set [secureProps] for the given config file */
    fun saveSecureProps() {
      if (!NativeSecretsLoader.HAS_KEYRING) {
        return
      }

      if (secureProps.isNotEmpty()) {
        val securePropsAsJsonBytes = Json.encodeToString(secureProps).toByteArray()
        val encodedSecretValue = Base64.getEncoder().encodeToString(securePropsAsJsonBytes)
        if (System.getProperty("os.name").contains("Windows")) {
          // Delete the existing credential
          deleteCredential()
        }
        setCredential(encodedSecretValue)
      }
    }

    /**
     * Retrieve the credential from the keyring or storage (in parts after maximum length)
     * @return the retrieved encoded credential
     */
    private fun getCredential(): String? {
      val serviceName = ZOWE_SERVICE_NAME
      val accountName = ZOWE_ACCOUNT_NAME
      return ZoweSecrets.getPassword(serviceName, accountName)
        ?: let {
          if (System.getProperty("os.name").contains("Windows")) {
            // Retrieve the secure value with an index
            var encodedCredential: String? = null
            var index = 1
            var tempValue = ZoweSecrets.getPassword(serviceName, "$accountName-$index")

            while (tempValue != null) {
              if (encodedCredential == null) {
                encodedCredential = tempValue
              } else {
                encodedCredential += tempValue
              }
              index++
              tempValue = ZoweSecrets.getPassword(serviceName, "$accountName-$index")
            }

            if (encodedCredential != null && encodedCredential.endsWith("\u0000")) {
              encodedCredential = encodedCredential.dropLast(1)
            }
            encodedCredential
          } else null
        }
    }

    /** Set the encoded credential to the keyring or storage */
    private fun setCredential(encodedCredential: String) {
      val serviceName = ZOWE_SERVICE_NAME
      val accountName = ZOWE_ACCOUNT_NAME

      // Check if the encoded credential exceeds the maximum length for Windows
      if (
        System.getProperty("os.name").contains("Windows")
        && encodedCredential.length > WINDOWS_CRED_MAX_STRING_LENGTH
      ) {
        // Split the encoded credential string into chunks of maximum length
        val chunkSize = WINDOWS_CRED_MAX_STRING_LENGTH
        val encodedCredentialForWindows = encodedCredential + "\u0000"
        // Set the individual chunks as separate keyring entries
        encodedCredentialForWindows
          .chunked(chunkSize)
          .forEachIndexed { index, chunk ->
            ZoweSecrets.setPassword(serviceName, "f$accountName-${index + 1}", chunk)
          }
      } else {
        // Credential length is within the maximum limit or not on Windows, set it as a single keyring entry
        ZoweSecrets.setPassword(serviceName, accountName, encodedCredential)
      }
    }

    /** Delete the credential from the keyring or storage */
    private fun deleteCredential() {
      val serviceName = ZOWE_SERVICE_NAME
      val accountName = ZOWE_ACCOUNT_NAME

      ZoweSecrets.deletePassword(serviceName, accountName)

      // Handling multiple credentials stored when the operating system is Windows
      if (System.getProperty("os.name").contains("Windows")) {
        var index = 1
        while (true) {
          val fieldName = "$accountName-$index"
          if (!ZoweSecrets.deletePassword(serviceName, fieldName)) {
            break
          }
          index++
        }
      }
    }
  }
}
