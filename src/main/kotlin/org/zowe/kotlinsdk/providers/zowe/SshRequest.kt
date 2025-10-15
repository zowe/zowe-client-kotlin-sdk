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

import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.connection.channel.direct.Session
import org.zowe.kotlinsdk.core.Request
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import java.io.ByteArrayOutputStream

/**
 * A basic representation of an SSH request object
 * @property connection the [org.zowe.kotlinsdk.core.connectivity.Connection] instance that suppose to hold all the necessary auth info to make the request
 * @property sshCommand the actual SSH TSO command to perform
 */
interface SshRequest : Request {
  val connection: SshConnection
  var sshCommand: String

  // TODO: doc
  fun performSshPlainRequest(client: SSHClient, session: Session): SshStatus {
    val cmd = session.exec(sshCommand)

    val output = ByteArrayOutputStream()
    cmd.inputStream.copyTo(output)

    val stderr = ByteArrayOutputStream()
    cmd.errorStream.copyTo(stderr)

    cmd.join()

    val outputStr = output.toString()
    return SshStatus(cmd.exitStatus, cmd.exitSignal, outputStr, stderr.toString())
  }

  /**
   * Execute the SSH request with the provided SSH client
   * @param client the SSHj client to exec the request with
   * @return [SshResponse] object
   */
  fun execRequest(client: SSHClient): SshResponse
}