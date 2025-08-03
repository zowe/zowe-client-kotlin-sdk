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

import net.schmizz.sshj.SSHClient
import org.zowe.kotlinsdk.core.Request

/**
 * A basic representation of an SSH request object
 * @property connection the [Connection] instance that suppose to hold all the necessary auth info to make the request
 * @property sshCommand the actual SSH TSO command to perform
 */
interface SshRequest : Request {
  val connection: SshConnection
  val sshCommand: String

  // TODO: doc
  fun execRequest(client: SSHClient): SshResponse
}