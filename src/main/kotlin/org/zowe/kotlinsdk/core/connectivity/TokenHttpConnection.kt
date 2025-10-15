/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.core.connectivity

/** Represents connection information with a token as the auth method */
class TokenHttpConnection(
  host: String,
  port: Int,
  rejectUnauthorized: Boolean = true,
  scheme: String = "https",
  /** z/OS host valid token as authentication method */
  private val token: String
) : HttpConnection(host, port, rejectUnauthorized, scheme) {

  /** Check if a token is specified, as well as a host */
  override fun checkConnection() {
    super.checkConnection()
    check(token.isNotEmpty()) {
      "Connection data is not set properly. Check if you specified a token"
    }
  }

  /** Get formed basic credentials token as authentication parameter */
  override fun getAuthParam(): String {
    return token
  }
}