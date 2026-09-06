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

import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.files.api.messaging.GetFileACLResponse
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus

/**
 * Response of the [SshGetFileACLRequest]
 * @param sshCmdResponse the SSH command result to produce the response from
 * @property fileACL the access control list lines, produced by the command. It is null when the command
 *                   reports nothing, which is the case of a failure and of a file with no entry to display
 */
class SshGetFileACLResponse(
  sshCmdResponse: SshCmdResponse
) : SshResponse, GetFileACLResponse {
  override var status: Status = SshStatus(sshCmdResponse)

  val fileACL: List<String>? = sshCmdResponse.output
    .lines()
    .filter { it.isNotBlank() }
    .map { it.trim() }
    .ifEmpty { null }
}
