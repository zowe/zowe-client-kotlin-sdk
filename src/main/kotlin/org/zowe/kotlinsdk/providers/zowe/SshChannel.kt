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

import net.schmizz.sshj.connection.channel.direct.Session
import java.io.ByteArrayOutputStream
import java.io.Closeable

/**
 * SSH channel to read data in buffered format
 * @property session the SSH session to start the command in
 * @property sshCommand the SSH command to execute
 * @property channelSize the channel size (4096 by default)
 * @property status the associated SSH status object to adjust after on the channel closing
 * @property state the actual channel state
 */
class SshChannel(
  private val session: Session,
  private val sshCommand: String,
  private val channelSize: Int = 4096
) : Closeable {
  private val buffer = ByteArray(channelSize)

  private var cmd: Session.Command? = null
  var state: SshChannelState = SshChannelState.READY
    private set
  var status: SshStatus = SshStatus.INCOMPLETE
    private set

  init {
    status.channel = this
  }

  /**
   * Read next data portion
   * @return byte array of read data
   */
  fun readNextPortion(): ByteArray {
    when (state) {
      SshChannelState.COMPLETE -> throw IllegalStateException("Channel is completed, no bytes available")
      SshChannelState.CLOSED -> throw IllegalStateException("Channel is closed")
      SshChannelState.READY -> {
        state = SshChannelState.READ_STARTED
        cmd = session.exec(sshCommand)
      }
      else -> {}
    }

    val bytesRead = cmd?.inputStream?.read(buffer) ?: throw IllegalStateException("SSH command is null")

    return when {
      bytesRead == -1 -> {
        state = SshChannelState.COMPLETE
        ByteArray(0)
      }
      bytesRead < channelSize -> buffer.copyOf(bytesRead)
      else -> buffer.copyOf()
    }
  }

  /**
   * Close the channel, ending the session and the session command execution.
   * Adjusts the associated SSH status
   */
  override fun close() {
    if (state != SshChannelState.CLOSED) {
      state = SshChannelState.CLOSED

      val stderr = ByteArrayOutputStream()
      cmd?.let {
        it.errorStream?.copyTo(stderr)
        it.join()
      }

      status.exitStatus = cmd?.exitStatus
      status.exitSignal = cmd?.exitSignal
      status.output = "CHANNELLED READ IS COMPLETED"
      status.stderr = stderr.toString()

      session.close()
    }
  }
}
