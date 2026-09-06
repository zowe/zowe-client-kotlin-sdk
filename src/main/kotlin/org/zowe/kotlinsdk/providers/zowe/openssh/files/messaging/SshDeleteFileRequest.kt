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
import org.zowe.kotlinsdk.core.files.api.messaging.DeleteFileRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest

/**
 * Delete a USS file or directory SSH request.
 * A file is deleted by the "rm" command, an empty directory - by the "rmdir" command,
 * so the item type is checked by the "test" command to run the appropriate one.
 * When the recursive deletion is requested, the "rm -r" command is run instead,
 * deleting the directory with all of its content
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-rm-remove-directory-entries">rm — Remove a directory entry</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-rmdir-remove-directory">rmdir — Remove a directory</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-test-evaluate-expression">test — Evaluate expression</a>
 * @property connection the SSH connection object to perform the operation with
 * @property filePath the USS file or directory path to delete
 * @property recursive delete the directory together with all of its content.
 *                     Without the option a non-empty directory is not deleted
 */
class SshDeleteFileRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val filePath: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val recursive: Boolean = false
) : SshRequest, DeleteFileRequest {
  override var sshCommand =
    if (recursive) {
      "rm -r '$filePath'"
    } else {
      "if test -d '$filePath'; then rmdir '$filePath'; else rm '$filePath'; fi"
    }

  override suspend fun produceResponseObject(clientResponse: Any): SshDeleteFileResponse {
    return SshDeleteFileResponse(clientResponse as SshCmdResponse)
  }
}
