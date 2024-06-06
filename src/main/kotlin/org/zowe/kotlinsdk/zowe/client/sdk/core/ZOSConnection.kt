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

package org.zowe.kotlinsdk.zowe.client.sdk.core

/**
 * z/OS Connection information placeholder
 *
 * @author Frank Giordano
 * @author Uladzislau Kalesnikau
 */
@Deprecated(
  "Scheduled for removal since v1.0.0",
  ReplaceWith("Connection", "org.zowe.kotlinsdk.core")
)
data class ZOSConnection(
  /**
   * machine host pointing to backend z/OS instance
   */
  val host: String,
  /**
   * machine host z/OSMF port number pointing to backend z/OS instance
   */
  val zosmfPort: String,
  /**
   * machine host username with access to backend z/OS instance
   */
  val user: String,
  /**
   * machine host username\'s password with access to backend z/OS instance
   */
  val password: String,
  /**
   * machine host z/OSMF protocol to connect to z/OS instance
   */
  val protocol: String = "https",
  /**
   * rejects self-signed certificates and essentially bypasses the certificate requirement
   */
  val rejectUnauthorized: Boolean? = true,
  /**
   * indicates the base path of the API ML instance that you want to access.
   */
  val basePath: String = "/",
  /**
   * encoding
   */
  val encoding: Long = 1047,
  /**
   * maximum amount of time for the TSO servlet to wait for a response before returning an error
   */
  val responseTimeout: Long = 600,
  /**
   * profile name from zowe.config.json
   */
  val profileName: String = "zosmf"
) {
  fun checkConnection() {
    if (host.isEmpty() || password.isEmpty() || user.isEmpty()) {
      throw IllegalStateException("Connection data not setup properly")
    }
  }
  override fun toString() =
    "ZOSConnection{host='$host', zosmfPort='$zosmfPort', user='$user', password='$password', rejectUnauthorized='$rejectUnauthorized', profileName='$profileName'}"
}
