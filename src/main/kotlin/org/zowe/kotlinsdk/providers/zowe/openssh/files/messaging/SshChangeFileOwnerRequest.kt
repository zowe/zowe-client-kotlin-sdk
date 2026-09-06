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
import org.zowe.kotlinsdk.core.files.api.messaging.ChangeFileOwnerRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest

/**
 * Change the owner of a USS file or directory SSH request, performed by the "chown" command.
 * The owner and the group are passed to the command as the single "owner:group" operand,
 * the group part is omitted when no [group] is provided, leaving the group owner unchanged.
 * Both of them are accepted either as a name or as a numeric ID.
 * No existence pre-check is performed, as the "chown" command reports the absent item by itself
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-chown-change-owner-group-file-directory">chown — Change the owner or group of a file or directory</a>
 * @property connection the SSH connection object to perform the operation with
 * @property filePath the USS file or directory path to change the owner of
 * @property owner the user name or the UID to set as the owner of the USS item
 * @property group the group name or the GID to set as the group owner of the USS item.
 *                 If not provided, the group owner is not changed
 * @property recursive change the owner of the directory and of all the items in the hierarchy below it ("chown -R")
 * @property links the way the symbolic links are treated by the owner change
 */
class SshChangeFileOwnerRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val filePath: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val owner: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val group: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val recursive: Boolean = false,
  @property:AvailableSince(ZVersion.ZOS_2_2) val links: Links = Links.FOLLOW,
) : SshRequest, ChangeFileOwnerRequest {
  /**
   * The way the symbolic links are treated by the "chown" command
   * @property chownOption the "chown" command option to treat the symbolic links with, if any
   */
  enum class Links(val chownOption: String?) {
    /** The owner of the file or directory, the symbolic link points to, is changed. The "chown" command default */
    FOLLOW(null),

    /** The owner of the symbolic link itself is changed, the link is not followed ("chown -h") */
    CHANGE("-h")
  }

  private val chownOptions = listOfNotNull(
    if (recursive) "-R" else null,
    links.chownOption
  )

  private val ownerOperand = if (group.isNullOrEmpty()) owner else "$owner:$group"

  override var sshCommand =
    (listOf("chown") + chownOptions + "'$ownerOperand'" + "'$filePath'").joinToString(" ")

  override suspend fun produceResponseObject(clientResponse: Any): SshChangeFileOwnerResponse {
    return SshChangeFileOwnerResponse(clientResponse as SshCmdResponse)
  }
}
