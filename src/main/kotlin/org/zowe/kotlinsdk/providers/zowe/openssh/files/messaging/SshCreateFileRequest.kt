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
import org.zowe.kotlinsdk.core.files.api.messaging.CreateFileRequest
import org.zowe.kotlinsdk.core.files.data.FileItem
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.files.definitions.SshFileMode

/**
 * Create a USS file or directory SSH request.
 * A directory is created by the "mkdir" command, a file - by the "touch" command.
 * The "mkdir" command fails by itself if the directory to create already exists, while the "touch" command
 * only updates the timestamps of an existing file, so the existence pre-check with the "ls" command
 * is performed for a file to not silently accept the creation of the already existing one.
 * The mode is applied by the "-m" option of the "mkdir" command for a directory
 * and by the "chmod" command for a file, as the "touch" command has no mode option
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-mkdir-make-directory">mkdir — Make a directory</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-touch-change-file-access-modification-times">touch — Change the file access and modification times</a>
 * @property connection the SSH connection object to perform the operation with
 * @property filePath the USS file or directory path to create
 * @property fileType the type of the USS item to create
 * @property fileMode the permissions to create the USS item with.
 *                    If not provided, the permissions are defined by the user's umask
 */
class SshCreateFileRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val filePath: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val fileType: FileItem.FileType = FileItem.FileType.FILE,
  @property:AvailableSince(ZVersion.ZOS_2_2) val fileMode: SshFileMode? = null,
) : SshRequest, CreateFileRequest {
  private val octalMode = fileMode?.toOctalString()

  override var sshCommand = when (fileType) {
    FileItem.FileType.DIRECTORY ->
      if (octalMode == null) "mkdir '$filePath'" else "mkdir -m $octalMode '$filePath'"

    FileItem.FileType.FILE ->
      if (octalMode == null) "touch '$filePath'"
      else "touch '$filePath' && chmod $octalMode '$filePath'"
  }

  override suspend fun produceResponseObject(clientResponse: Any): SshCreateFileResponse {
    return SshCreateFileResponse(clientResponse as SshCmdResponse)
  }

  /**
   * Checks that the file to create does not exist yet with the "ls" command, if a file is to be created,
   * then creates the requested USS item. No check is performed for a directory,
   * as the "mkdir" command reports the already existing directory by itself
   * @param client the [SSHClient] to execute the commands with
   * @return the [SshCreateFileResponse] object with respective status
   */
  override suspend fun execSshRequest(client: SSHClient): SshCreateFileResponse {
    if (fileType == FileItem.FileType.FILE) {
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
