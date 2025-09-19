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

import net.schmizz.sshj.connection.channel.direct.Signal

/** The SSH command execution status */
data class SshStatus(
  var exitStatus: Int? = 0,
  var exitSignal: Signal? = null,
  var output: String = "",
  var stderr: String = "",
  var channel: SshChannel? = null
) {
  companion object {
    val OK = SshStatus()
    val INCOMPLETE = SshStatus(null)
  }
}