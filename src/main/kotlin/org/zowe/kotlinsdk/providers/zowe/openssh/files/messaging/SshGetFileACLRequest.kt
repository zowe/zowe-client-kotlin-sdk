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
import org.zowe.kotlinsdk.core.files.api.messaging.GetFileACLRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest

/**
 * Retrieve the access control list of a USS file or directory SSH request,
 * performed by the "getfacl" command.
 * No existence pre-check is performed, as the "getfacl" command reports the absent file by itself
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-getfacl-display-owner-group-class-acl-entries">getfacl — Display owner, group, class, and ACL entries</a>
 * @property connection the SSH connection object to perform the operation with
 * @property filePath the USS file or directory path to retrieve the access control list of
 * @property type the type of the access control list to display, the command displays the access one by default
 * @property user the user ID or UID to display only the access control list entries affecting the access of
 * @property useCommas display the entries separated by commas rather than by the new lines
 * @property suppressHeader do not display the comment header, which is the first three lines of the output
 * @property suppressBaseACL display only the extended access control list entries, leaving out the base ones
 */
class SshGetFileACLRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val filePath: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val type: Type? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val user: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val useCommas: Boolean = false,
  @property:AvailableSince(ZVersion.ZOS_2_2) val suppressHeader: Boolean = false,
  @property:AvailableSince(ZVersion.ZOS_2_2) val suppressBaseACL: Boolean = false,
) : SshRequest, GetFileACLRequest {

  /**
   * The type of the access control list to display
   * @property getfaclOption the "getfacl" command option to display the entries of the type
   */
  enum class Type(val getfaclOption: String) {
    /** The access control list entries, controlling the access to the file or directory itself */
    ACCESS("-a"),

    /** The default access control list entries, inherited by the subdirectories of the directory */
    DIR("-d"),

    /** The default access control list entries, inherited by the files, created in the directory */
    FILE("-f")
  }

  private val getfaclOptions = listOfNotNull(
    type?.getfaclOption,
    if (useCommas) "-c" else null,
    if (suppressHeader) "-m" else null,
    if (suppressBaseACL) "-o" else null,
    user?.let { "-e '${validateUser(it)}'" }
  )

  override var sshCommand = (listOf("getfacl") + getfaclOptions + "'$filePath'").joinToString(" ")

  override suspend fun produceResponseObject(clientResponse: Any): SshGetFileACLResponse {
    return SshGetFileACLResponse(clientResponse as SshCmdResponse)
  }

  /**
   * Check that the user to filter the access control list entries by is provided as a meaningful value
   * @param user the user ID or UID to check
   * @return the checked user ID or UID
   * @throws IllegalArgumentException when the user is provided as a blank value
   */
  private fun validateUser(user: String): String {
    if (user.isBlank()) {
      throw IllegalArgumentException("The user to display the access control list entries for is provided as blank")
    }
    return user
  }
}
