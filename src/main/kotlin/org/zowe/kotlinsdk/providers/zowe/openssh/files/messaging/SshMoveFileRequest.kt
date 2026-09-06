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

import net.schmizz.sshj.SSHClient
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import org.zowe.kotlinsdk.core.files.api.messaging.MoveFileRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest

/**
 * Move a USS file or directory SSH request, performed by the "mv" command.
 * A directory is moved with all of its content, so no recursion option is needed.
 * The "mv" command overwrites the existing target silently, so the existence pre-check with the "ls" command
 * is performed, unless [overwrite] is set to true. The check is done for the target path itself, thus the move
 * of an item into an already existing directory also requires [overwrite] to be set to true
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-mv-rename-move-file-directory">mv — Rename or move a file or directory</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-ls-list-file-directory-names-attributes">ls — List file and directory names and attributes</a>
 * @property connection the SSH connection object to perform the operation with
 * @property filePath the USS file or directory path to move to
 * @property from the USS file or directory path to move
 * @property overwrite move the item even if the target already exists.
 *                     Without the option an already existing target is not overwritten
 */
class SshMoveFileRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val filePath: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val from: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val overwrite: Boolean = false,
) : SshRequest, MoveFileRequest {
  override var sshCommand = "mv '$from' '$filePath'"

  override suspend fun produceResponseObject(clientResponse: Any): SshMoveFileResponse {
    return SshMoveFileResponse(clientResponse as SshCmdResponse)
  }

  /**
   * Checks that the target of the move does not exist yet with the "ls" command, unless [overwrite] is requested,
   * then performs the move itself
   * @param client the [SSHClient] to execute the commands with
   * @return the [SshMoveFileResponse] object with respective status
   */
  override suspend fun execSshRequest(client: SSHClient): SshMoveFileResponse {
    if (!overwrite) {
      val listFilesRequest = SshListFilesRequest(connection, filePath)
      val listFilesResponse = listFilesRequest.execSshRequest(client) as SshListFilesResponse
      if (listFilesResponse.status.type == StatusType.SUCCESS) {
        return produceResponseObject(
          SshCmdResponse(1, output = "THE FILE \"$filePath\" ALREADY EXISTS")
        )
      }
    }

    val clientResponse = performSshPlainRequest(client)
    return produceResponseObject(clientResponse)
  }
}
