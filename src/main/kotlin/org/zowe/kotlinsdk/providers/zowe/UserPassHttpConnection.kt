/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe

import okhttp3.Credentials

/** Represents connection information with user and password as the auth method */
class UserPassHttpConnection(
  host: String,
  port: Int,
  scheme: String = "https",
  /** z/OS host valid username with access to HTTP REST API */
  val user: String,
  /** z/OS host user\'s password with access to HTTP REST API */
  val password: String
) : HttpConnection(host, port, scheme) {

  private val basicCredentials: String = Credentials.basic(user, password)

  /** Check if a password and a username are specified, as well as a host */
  override fun checkConnection() {
    super.checkConnection()
    check(password.isNotEmpty() && user.isNotEmpty()) {
      "Connection data is not set properly. Check if you specified user and password"
    }
  }

  /** Get formed basic credentials token as authentication parameter */
  override fun getAuthParam(): String {
    return basicCredentials
  }
}
