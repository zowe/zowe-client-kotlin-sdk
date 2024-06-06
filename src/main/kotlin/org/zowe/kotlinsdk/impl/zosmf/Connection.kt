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

/** Represents basic connection information */
abstract class Connection(
  /** z/OS host IP or root domain */
  val host: String,
  /** z/OS host port number */
  val zosmfPort: String,
  /** z/OS host protocol to use during a connection */
  val protocol: String = "https"
) {

  /** Check if the connection has all the necessary parameters specified */
  open fun checkConnection() {
    check(host.isNotEmpty()) {
      "Connection data is not set properly. The host is not specified"
    }
  }

  /** Get authentication parameter to provide during a request */
  abstract fun getAuthParam(): String
}
