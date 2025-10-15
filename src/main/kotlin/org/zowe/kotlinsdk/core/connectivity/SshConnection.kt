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

import net.schmizz.sshj.userauth.method.AuthMethod

/**
 * SSH connection class. Provides the way to store and gather specific SSH parameters.
 * Scheme is "ssh" by default
 * @see [Connection]
 * @property username the username to use during the connection
 * @property authMethods authentication methods list with all the necessary information provided to connect
 */
class SshConnection(
  host: String,
  port: Int = 22,
  scheme: String = "ssh",
  val username: String,
  val authMethods: List<AuthMethod>
) : Connection(host, port, scheme) {
  override fun checkConnection() {
    super.checkConnection()
    check(username.isNotEmpty()) {
      "Connection data is not set properly. The username is not specified"
    }
    check(authMethods.isNotEmpty()) {
      "Connection data is not set properly. There are no authentication methods specified"
    }
  }
}