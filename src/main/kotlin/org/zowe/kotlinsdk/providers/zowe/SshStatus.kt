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

import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.StatusType
import java.time.Instant

/** The SSH command execution status.
 * Processes [sshCmdResponse] and produces all the necessary info
 * to identify the actual result of the command execution
 */
open class SshStatus(
  protected val sshCmdResponse: SshCmdResponse = SshCmdResponse()
) : Status {
  companion object {
    val OK = SshStatus()
  }

  /** Type of the SSH command result. By default, everything other than exitStatus == 0 is considered as an error */
  override val type: StatusType = if (sshCmdResponse.exitStatus == 0) StatusType.SUCCESS else StatusType.ERROR
  override val timestamp: Instant = Instant.now()
  override val metadata: Map<String, Any>
    get() {
      return listOfNotNull(
        "RC" to sshCmdResponse.exitStatus,
        sshCmdResponse.exitSignal?.let { "Exit signal" to "SIG_${sshCmdResponse.exitSignal}" },
        "Output" to sshCmdResponse.output,
        "STDERR" to sshCmdResponse.stderr
      ).toMap()
    }
  override val text: String
    get() {
      return metadata.entries.joinToString(".\n") { entry -> "${entry.key}: ${entry.value}" }
    }
}
