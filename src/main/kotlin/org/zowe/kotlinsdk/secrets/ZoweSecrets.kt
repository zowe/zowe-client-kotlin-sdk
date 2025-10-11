/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.secrets

/** Exception thrown by secrets operations */
class ZoweSecretsException(message: String, cause: Throwable? = null) : Exception(message, cause)

/** Credential data class */
data class ZoweCredential(val account: String, val password: String)

/**
 * High-level API for secure credential storage using "Zowe Keyring" library.
 *
 * Supported platforms:
 * - **Windows**: Windows Credential Manager
 * - **macOS**: Keychain
 * - **Linux**: libsecret (Secret Service API)
 *
 * Example usage:
 * ```kotlin
 * // Store credentials
 * Secrets.setPassword("zowe", "user@example.com", "myPassword123")
 *
 * // Retrieve credentials
 * val password = Secrets.getPassword("zowe", "user@example.com")
 *
 * // Delete credentials
 * Secrets.deletePassword("zowe", "user@example.com")
 *
 * // Find all credentials for a service
 * val allCreds = Secrets.findAllCredentials("zowe")
 * ```
 */
object ZoweSecrets {
  /**
   * Store a password securely in the system keyring
   * @param service Service identifier (e.g., "zowe", "myapp")
   * @param account Account/username identifier
   * @param password Password to store securely
   */
  @JvmStatic
  fun setPassword(service: String, account: String, password: String) {
    require(service.isNotBlank()) { "Service name cannot be blank" }
    require(account.isNotBlank()) { "Account name cannot be blank" }
    require(password.isNotEmpty()) { "Password cannot be empty" }

    try {
      NativeSecretsLoader.setPassword(service, account, password)
    } catch (e: Exception) {
      throw ZoweSecretsException("Failed to set password for $service/$account", e)
    }
  }

  /**
   * Retrieve a password from the system keyring
   * @param service Service identifier
   * @param account Account/username identifier
   * @return Password or null if not found
   */
  @JvmStatic
  fun getPassword(service: String, account: String): String? {
    require(service.isNotBlank()) { "Service name cannot be blank" }
    require(account.isNotBlank()) { "Account name cannot be blank" }

    return try {
      NativeSecretsLoader.getPassword(service, account)
    } catch (e: Exception) {
      throw ZoweSecretsException("Failed to get password for $service/$account", e)
    }
  }

  /**
   * Delete a password from the system keyring
   * @param service Service identifier
   * @param account Account/username identifier
   * @return true if the password was deleted, false if it didn't exist
   * @throws ZoweSecretsException if the operation fails
   */
  @JvmStatic
  fun deletePassword(service: String, account: String): Boolean {
    require(service.isNotBlank()) { "Service name cannot be blank" }
    require(account.isNotBlank()) { "Account name cannot be blank" }

    return try {
      NativeSecretsLoader.deletePassword(service, account)
    } catch (e: Exception) {
      throw ZoweSecretsException("Failed to delete password for $service/$account", e)
    }
  }

  /**
   * Find any password associated with a service
   * @param service Service identifier
   * @return First password found or null
   */
  @JvmStatic
  fun findPassword(service: String): String? {
    require(service.isNotBlank()) { "Service name cannot be blank" }

    return try {
      NativeSecretsLoader.findPassword(service)
    } catch (e: Exception) {
      throw ZoweSecretsException("Failed to find password for $service", e)
    }
  }

  /**
   * Find all credentials stored for a service
   * @param service Service identifier
   * @return List of credentials (account and password pairs)
   */
  @JvmStatic
  fun findAllCredentials(service: String): List<ZoweCredential> {
    require(service.isNotBlank()) { "Service name cannot be blank" }

    return try {
      NativeSecretsLoader.findCredentials(service)
        .map { (account, password) -> ZoweCredential(account, password) }
      } catch (e: Exception) {
        throw ZoweSecretsException("Failed to find credentials for $service", e)
      }
  }

  /**
   * Check if a password exists for the given service and account
   * @param service Service identifier
   * @param account Account/username identifier
   * @return true if a password exists
   */
  @JvmStatic
  fun hasPassword(service: String, account: String): Boolean {
    return getPassword(service, account) != null
  }
}
