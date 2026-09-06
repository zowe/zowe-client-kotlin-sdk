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
import org.zowe.kotlinsdk.core.files.api.messaging.ChangeFileOwnerResponse
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus

/**
 * Response of the [SshChangeFileOwnerRequest]
 * @param sshCmdResponse the SSH command result to produce the response from
 */
class SshChangeFileOwnerResponse(sshCmdResponse: SshCmdResponse) : SshResponse, ChangeFileOwnerResponse {
  override var status: Status = SshStatus(sshCmdResponse)
}
