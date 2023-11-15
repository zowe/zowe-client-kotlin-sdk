//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf

/** Represents connection information with a token as the auth method */
class TokenConnection(
  host: String, zosmfPort: String, protocol: String,
  /** z/OS host valid token as authentication method */
  private val token: String
) : Connection(host, zosmfPort, protocol) {

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