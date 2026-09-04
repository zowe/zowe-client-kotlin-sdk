/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging

import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus

/**
 * Status for the listFiles SSH command.
 * The command is considered as failed with [StatusType.ERROR] if the exit status is not 0.
 * "ls" reports both a missing path (the FSUM6785 message) and any other failure
 * with a non-zero exit status, the exact reason is available through the STDERR of [text]
 * @param sshCmdResponse the SSH command result to recognize the status by
 */
class SshListFilesStatus(
  sshCmdResponse: SshCmdResponse,
) : SshStatus(sshCmdResponse) {
  override val type: StatusType
    get() {
      return if (
//        (sshCmdResponse.stderr.contains("ls: FSUM6785 File or directory")
//        && sshCmdResponse.stderr.contains("is not found"))
//        || sshCmdResponse.exitStatus != 0
        sshCmdResponse.exitStatus != 0
      ) {
        StatusType.ERROR
      } else super.type
    }
}
