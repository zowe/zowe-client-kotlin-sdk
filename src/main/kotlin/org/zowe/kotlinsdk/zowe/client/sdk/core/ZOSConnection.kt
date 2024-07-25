// Copyright © 2020 IBA Group, a.s. All rights reserved. Use of this source code is governed by Eclipse Public License – v 2.0 that can be found at: https://www.eclipse.org/legal/epl-2.0/

package org.zowe.kotlinsdk.zowe.client.sdk.core

/**
 * z/OS Connection information placeholder
 *
 * @author Frank Giordano
 * @author Uladzislau Kalesnikau
 */
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
  // TODO: doc
  fun checkConnection() {
    if (host.isEmpty() || password.isEmpty() || user.isEmpty()) {
      throw IllegalStateException("Connection data not setup properly")
    }
  }
  override fun toString() =
    "ZOSConnection{host='$host', zosmfPort='$zosmfPort', user='$user', password='$password', rejectUnauthorized='$rejectUnauthorized', profileName='$profileName'}"
}
