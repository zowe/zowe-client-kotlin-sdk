//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core

import okhttp3.Credentials

/** Represents z/OSMF connection information */
class Connection(
  /** z/OS host IP or root domain */
  val host: String,
  /** z/OS host z/OSMF port number */
  val zosmfPort: String,
  /** z/OS host valid username with access to z/OSMF REST API */
  val user: String,
  /** z/OS host user\'s password with access to z/OSMF REST API */
  val password: String,
  /** z/OS host z/OSMF protocol to use during a connection */
  val protocol: String = "https"
) {

  val basicCredentials: String = Credentials.basic(user, password)

  // TODO: doc
  fun checkConnection() {
    check(host.isEmpty() || password.isEmpty() || user.isEmpty()) {
      "Connection data is not set properly. Check if you specified host, user and password"
    }
  }
}