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
import org.zowe.kotlinsdk.core.files.api.messaging.FileExtAttributesUtilityResponse
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus

/**
 * Response of the [SshFileExtAttributesUtilityRequest]
 * @param sshCmdResponse the SSH command result to produce the response from
 * @property extendedAttributes the extended attributes lines, produced by the command.
 *                              Only a request with neither the attributes to set nor the ones to reset
 *                              makes the command report them, the other ones leave it null
 */
class SshFileExtAttributesUtilityResponse(
  sshCmdResponse: SshCmdResponse
) : SshResponse, FileExtAttributesUtilityResponse {
  override var status: Status = SshStatus(sshCmdResponse)

  val extendedAttributes: List<String>? = sshCmdResponse.output
    .lines()
    .filter { it.isNotBlank() }
    .map { it.trim() }
    .ifEmpty { null }
}
