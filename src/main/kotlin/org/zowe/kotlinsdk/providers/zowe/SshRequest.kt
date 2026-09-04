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
import org.zowe.kotlinsdk.core.Request
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import java.io.ByteArrayOutputStream

/**
 * A basic representation of an SSH request object
 * @property connection the [org.zowe.kotlinsdk.core.connectivity.Connection] instance that suppose to hold all the necessary auth info to make the request
 * @property sshCommand the actual SSH command to perform
 * @property multilineContent the content to send to the SSH server together with the SSH command in the multiline style
 */
interface SshRequest : Request {
  val connection: SshConnection
  var sshCommand: String
  val multilineContent: ByteArray?
    get() = null

  /** Perform an SSH plain request with the provided [SSHClient], returning an [SshCmdResponse] object */
  fun performSshPlainRequest(client: SSHClient): SshCmdResponse {
    if (!client.isConnected) client.connect(connection.host, connection.port)
    if (!client.isAuthenticated) client.auth(connection.username, connection.authMethods)
    client
      .startSession()
      .use { session ->
        val cmd = session.exec(sshCommand)
        multilineContent?.let { nonNullMultilineContent ->
          cmd.outputStream.write(nonNullMultilineContent)
          cmd.outputStream.close()
        }

        val output = ByteArrayOutputStream()
        cmd.inputStream.copyTo(output)

        val stderr = ByteArrayOutputStream()
        cmd.errorStream.copyTo(stderr)

        cmd.join()

        val outputStr = output.toString()
        return SshCmdResponse(cmd.exitStatus, cmd.exitSignal, outputStr, stderr.toString())
      }
  }

  /**
   * [SshResponse] object producer to return a respective created instance,
   * basing on the request and the [SshCmdResponse] data
   */
  override suspend fun produceResponseObject(clientResponse: Any): SshResponse

  /**
   * Execute the SSH request with the provided [SSHClient]
   * @return [SshResponse] object
   */
  suspend fun execSshRequest(client: SSHClient): SshResponse {
    val clientResponse = performSshPlainRequest(client)
    return produceResponseObject(clientResponse)
  }

  override suspend fun execRequest(payload: Any): SshResponse {
    return execSshRequest(payload as SSHClient)
  }
}