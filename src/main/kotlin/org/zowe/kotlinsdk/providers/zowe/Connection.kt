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

/**
 * Represents basic connection information
 * @property host z/OS host IP or root domain to connect to
 * @property port z/OS host port number to connect to
 * @property scheme connection scheme to use
 */
abstract class Connection(val host: String, val port: Int, val scheme: String) {
  /** Check if the connection has all the necessary parameters specified */
  open fun checkConnection() {
    check(host.isNotEmpty()) {
      "Connection data is not set properly. The host is not specified"
    }
  }
}