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
import org.zowe.kotlinsdk.core.files.api.messaging.ChangeFileTagResponse
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus

/**
 * Response of the [SshChangeFileTagRequest]
 * @param sshCmdResponse the SSH command result to produce the response from
 * @property currentTagInfo the tag information line, produced by the command.
 *                          Only the [SshChangeFileTagRequest.Action.LIST] action makes the command report it,
 *                          the other actions leave it null
 */
class SshChangeFileTagResponse(sshCmdResponse: SshCmdResponse) : SshResponse, ChangeFileTagResponse {
  override var status: Status = SshStatus(sshCmdResponse)

  val currentTagInfo: String? = sshCmdResponse.output.lines().firstOrNull { it.isNotBlank() }?.trim()
}
