/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.providers.zowe

import net.schmizz.sshj.userauth.method.AuthMethod

// TODO: doc
class SshConnection(
  host: String,
  port: Int = 22,
  scheme: String = "ssh",
  /** Username to use during the connection */
  val username: String,
  /** Ordered authentication methods list with all the necessary information provided to connect */
  val authMethods: List<AuthMethod>
) : Connection(host, port, scheme)
