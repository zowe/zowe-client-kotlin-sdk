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
 * HTTP connection abstraction. Provides the way to store and gather specific HTTP parameters.
 * Scheme is "https" by default
 * @see [Connection]
 */
abstract class HttpConnection(host: String, port: Int, scheme: String = "https") : Connection(host, port, scheme) {
  /** Get authentication parameter to provide during a request */
  abstract fun getAuthParam(): String
}
