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
import org.zowe.kotlinsdk.core.files.api.messaging.ChangeFileModeRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.files.definitions.SshFileMode

/**
 * Change the mode of a USS file or directory SSH request, performed by the "chmod" command.
 * The command accepts the mode in the octal form, so the provided [fileMode] is converted to it.
 * No existence pre-check is performed, as the "chmod" command reports the absent item by itself
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-chmod-change-mode-file-directory">chmod — Change the mode of a file or directory</a>
 * @property connection the SSH connection object to perform the operation with
 * @property filePath the USS file or directory path to change the mode of
 * @property fileMode the permissions to set for the USS item
 * @property recursive change the mode of the directory and of all the items in the hierarchy below it ("chmod -R")
 * @property links the way the symbolic links are treated by the mode change
 */
class SshChangeFileModeRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val filePath: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val fileMode: SshFileMode,
  @property:AvailableSince(ZVersion.ZOS_2_2) val recursive: Boolean = false,
  @property:AvailableSince(ZVersion.ZOS_2_2) val links: Links = Links.FOLLOW,
) : SshRequest, ChangeFileModeRequest {
  /**
   * The way the symbolic links are treated by the "chmod" command
   * @property chmodOption the "chmod" command option to treat the symbolic links with, if any
   */
  enum class Links(val chmodOption: String?) {
    /** The mode of the file or directory, the symbolic link points to, is changed. The "chmod" command default */
    FOLLOW(null),

    /** The mode of the symbolic link itself is changed, the link is not followed ("chmod -h") */
    SUPPRESS("-h")
  }

  private val chmodOptions = listOfNotNull(
    if (recursive) "-R" else null,
    links.chmodOption
  )

  override var sshCommand =
    (listOf("chmod") + chmodOptions + fileMode.toOctalString() + "'$filePath'").joinToString(" ")

  override suspend fun produceResponseObject(clientResponse: Any): SshChangeFileModeResponse {
    return SshChangeFileModeResponse(clientResponse as SshCmdResponse)
  }
}
