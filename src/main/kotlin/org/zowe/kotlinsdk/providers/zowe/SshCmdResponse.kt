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

import net.schmizz.sshj.connection.channel.direct.Signal

/**
 * Representation of the SSH command resulting parameters
 * @param exitStatus the status the SSH command is finished with
 * @param exitSignal if present, the POSIX SIG-<signal> produced on the command exit
 * @param output the actual output of the SSH command
 * @param stderr the captured output of the STDERR
 */
class SshCmdResponse(
  val exitStatus: Int = 0,
  val exitSignal: Signal? = null,
  val output: String = "",
  val stderr: String = "",
)
