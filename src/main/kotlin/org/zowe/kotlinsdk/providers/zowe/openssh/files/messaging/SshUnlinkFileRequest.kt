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

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import org.zowe.kotlinsdk.core.files.api.messaging.UnlinkFileRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest

/**
 * Remove a link to a USS file or directory SSH request, performed by the "unlink" command.
 * The command takes no option, as the z/OSMF unlink function accepts no parameter either.
 * Only the link itself is removed by the request: the file it refers to stays as it is,
 * until the last link to it is removed. No existence pre-check is performed,
 * as the "unlink" command reports the absent path by itself
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-unlink-remove-directory-entry">unlink — Remove a directory entry</a>
 * @property connection the SSH connection object to perform the operation with
 * @property filePath the USS path of the link to remove
 */
class SshUnlinkFileRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val filePath: String,
) : SshRequest, UnlinkFileRequest {

  override var sshCommand = "unlink '$filePath'"

  override suspend fun produceResponseObject(clientResponse: Any): SshUnlinkFileResponse {
    return SshUnlinkFileResponse(clientResponse as SshCmdResponse)
  }
}
