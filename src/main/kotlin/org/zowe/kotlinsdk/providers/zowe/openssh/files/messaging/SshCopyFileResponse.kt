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
import org.zowe.kotlinsdk.core.files.api.messaging.CopyFileResponse
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.SshCpStatus

/**
 * Response of the [SshCopyFileRequest]
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-cp-copy-file#cp__title__18">cp - Copy a file. Exit values</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-cp-copy-file#cp__cpmessagesection__title__1">cp - Copy a file. Messages</a>
 * @param sshCmdResponse the SSH command result to produce the response from
 */
class SshCopyFileResponse(sshCmdResponse: SshCmdResponse) : SshResponse, CopyFileResponse {
  override var status: Status = SshCpStatus(sshCmdResponse)
}
