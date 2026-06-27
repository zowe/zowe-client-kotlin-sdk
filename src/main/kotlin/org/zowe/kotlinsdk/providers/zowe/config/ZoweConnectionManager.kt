/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.config

import io.github.cdimascio.dotenv.dotenv
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.core.connectivity.TokenHttpConnection
import org.zowe.kotlinsdk.core.connectivity.UserPassHttpConnection

/**
 * All-purpose connection manager. Provides a simplified mechanism of creating connections both directly or using
 * Zowe config and Zowe profiles
 */
class ZoweConnectionManager(val zoweProfileManager: ZoweProfileManager) {
  companion object {
    // To collect Zowe environment variables from .env file
    private val devEnv by lazy { dotenv { ignoreIfMissing = true } }

    private fun getEnv(): Map<String, String> {
      return devEnv.entries()
        .filter { entry -> entry.key.startsWith("ZOWE_OPT") }
        .associate { entry -> entry.key to (entry.value ?: "") }
        .mapKeys { (key, _) -> key.removePrefix("ZOWE_OPT_").lowercase() }
        .toMutableMap()
    }

    /**
     * Produce an [org.zowe.kotlinsdk.core.connectivity.HttpConnection] from the provided parameters. If the parameters are not set, will try to resolve
     * them from an environment. It is mandatory to provide either username, or token. If the token is provided, it is
     * used as a main way to connect to the machine. If the token is not provided, but the username is, it expects
     * the password to be provided as well, otherwise it fails to create the [org.zowe.kotlinsdk.core.connectivity.HttpConnection] object
     * @param host the host address to create a connection to
     * @param port the port of the machine to create a connection to
     * @param rejectUnauthorized to reject unauthorized access to the machine (to disallow self-signed certs)
     * @param user the username to connect to the host with
     * @param password the password to connect to the host with
     * @param token the token to connect to the host with
     * @return [org.zowe.kotlinsdk.core.connectivity.HttpConnection] compatible object, basing on the parameters available
     */
    fun produceHttpConnection(
      host: String? = null,
      port: Int? = null,
      rejectUnauthorized: Boolean? = null,
      user: String? = null,
      password: String? = null,
      token: String? = null
    ): HttpConnection {
      val envVars = getEnv()
      val resolvedHost = host ?: envVars["HOST"] ?: throw Exception("'host' must be provided to create an HTTP connection")
      val resolvedPort = port ?: envVars["PORT"]?.toInt() ?: 443
      val resolvedRejectUnauthorized = rejectUnauthorized ?: envVars["REJECT_UNAUTHORIZED"]?.toBoolean() ?: true
      val resolvedUser = user ?: envVars["USER"]
      val resolvedPassword = password ?: envVars["PASSWORD"]
      val resolvedToken = token ?: envVars["TOKEN"]

      return if (resolvedToken != null) {
        TokenHttpConnection(resolvedHost, resolvedPort, resolvedRejectUnauthorized, token = resolvedToken)
      } else if (resolvedUser != null) {
        val passwordOrToken = resolvedPassword
          ?: throw Exception("'password' must be provided with 'user' to create an HTTP connection")
        UserPassHttpConnection(
          resolvedHost,
          resolvedPort,
          resolvedRejectUnauthorized,
          user = resolvedUser,
          password = passwordOrToken
        )
      } else {
        throw Exception("At least 'user' or 'token' must be provided to create an HTTP connection")
      }
    }
  }

  /**
   * Produce an [HttpConnection] object from the selected profile. Tries to resolve the profile values first, and then
   * gathers all the necessary missing parameters from environment variables
   * @param profileName the profile name to get parameters for ("zosmf" by default)
   * @param shouldOverrideWithEnv are the profile values should be overridden with environment variables (true by default)
   * @return [HttpConnection] compatible object, basing on the profile parameters and environment variables
   */
  fun produceHttpConnection(profileName: String = "zosmf", shouldOverrideWithEnv: Boolean = true): HttpConnection {
    val profile = zoweProfileManager.load(profileName, shouldOverrideWithEnv = shouldOverrideWithEnv)
    return produceHttpConnection(
      profile["host"] as? String?,
      (profile["port"] as? Long?)?.toInt(),
      profile["rejectUnauthorized"] as? Boolean?,
      profile["user"] as? String?,
      profile["password"] as? String?,
      profile["token"] as? String
    )
  }
}