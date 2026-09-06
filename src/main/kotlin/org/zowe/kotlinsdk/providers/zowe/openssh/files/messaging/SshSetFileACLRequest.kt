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
import org.zowe.kotlinsdk.core.files.api.messaging.SetFileACLRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest

/**
 * Set, modify or remove the access control list of a USS file or directory SSH request,
 * performed by the "setfacl" command.
 * Exactly one operation is performed by the request: either [set], or [deleteType],
 * or the [modify] and [delete] pair, which is the only combination the command accepts.
 * No existence pre-check is performed, as the "setfacl" command reports the absent file by itself
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-setfacl-set-remove-change-acl-entries">setfacl — Set, remove, and change ACL entries</a>
 * @property connection the SSH connection object to perform the operation with
 * @property filePath the USS file or directory path to operate the access control list of
 * @property abort stop the processing as soon as an error or a warning occurs
 * @property links the way the command treats the symbolic links. As the access control lists are not
 *                 associated with them, suppressing makes the command do nothing for such a target
 * @property deleteType the type of the extended access control list entries to delete all of
 * @property set the access control list entries to replace all the existing ones with
 * @property modify the access control list entries to add or to replace the existing ones by
 * @property delete the extended access control list entries to delete
 */
class SshSetFileACLRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val filePath: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val abort: Boolean = false,
  @property:AvailableSince(ZVersion.ZOS_2_2) val links: Links = Links.FOLLOW,
  @property:AvailableSince(ZVersion.ZOS_2_2) val deleteType: DeleteType? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val set: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val modify: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val delete: String? = null,
) : SshRequest, SetFileACLRequest {

  /**
   * The way the command treats the symbolic links
   * @property setfaclOption the "setfacl" command option to treat the symbolic links the way
   */
  enum class Links(val setfaclOption: String?) {
    /** Operate the access control list of the file or directory the symbolic link refers to */
    FOLLOW(null),

    /** Operate the symbolic link itself, which makes the command do nothing for it */
    SUPPRESS("-h")
  }

  /**
   * The type of the extended access control list entries to delete all of
   * @property setfaclOption the "setfacl" command "-D" option operand to delete the entries of the type
   */
  enum class DeleteType(val setfaclOption: String) {
    /** The access control list entries, controlling the access to the file or directory itself */
    ACCESS("a"),

    /** The default access control list entries, inherited by the subdirectories of the directory */
    DIR("d"),

    /** The default access control list entries, inherited by the files, created in the directory */
    FILE("f"),

    /** All the extended access control list entries of all the types */
    EVERY("e")
  }

  private val operationOptions = validateOperation()

  private val setfaclOptions =
    listOfNotNull(if (abort) "-a" else null, links.setfaclOption) + operationOptions

  override var sshCommand = (listOf("setfacl") + setfaclOptions + "'$filePath'").joinToString(" ")

  override suspend fun produceResponseObject(clientResponse: Any): SshSetFileACLResponse {
    return SshSetFileACLResponse(clientResponse as SshCmdResponse)
  }

  /**
   * Check that the operation to perform is requested by the combination of the properties
   * the "setfacl" command accepts, and build the command options for it. The command performs
   * either the replacement, or the deletion by the type, or the modification together with
   * the deletion of the particular entries, but never a mix of these operations
   * @return the command options to perform the requested operation by
   * @throws IllegalArgumentException when no operation or more than one of them is requested
   */
  private fun validateOperation(): List<String> {
    val entriesOperations = listOfNotNull(
      set?.let { "-s '${validateEntries(it, "set")}'" },
      modify?.let { "-m '${validateEntries(it, "modify")}'" },
      delete?.let { "-x '${validateEntries(it, "delete")}'" }
    )
    if (deleteType != null) {
      if (entriesOperations.isNotEmpty()) {
        throw IllegalArgumentException(
          "The access control list entries type to delete is not expected to be provided " +
            "together with the entries to set, to modify or to delete"
        )
      }
      return listOf("-D ${deleteType.setfaclOption}")
    }
    if (set != null && entriesOperations.size > 1) {
      throw IllegalArgumentException(
        "The access control list entries to set are not expected to be provided " +
          "together with the ones to modify or to delete"
      )
    }
    if (entriesOperations.isEmpty()) {
      throw IllegalArgumentException(
        "An operation to perform is expected to be requested by either the entries type to delete, " +
          "or the entries to set, to modify or to delete"
      )
    }
    return entriesOperations
  }

  /**
   * Check that the access control list entries are provided as a meaningful value
   * @param entries the entries string to check
   * @param operationName the name of the request property the entries are provided by, to report it in an error
   * @return the checked entries string
   * @throws IllegalArgumentException when the entries are provided as a blank value
   */
  private fun validateEntries(entries: String, operationName: String): String {
    if (entries.isBlank()) {
      throw IllegalArgumentException("The access control list entries to $operationName are provided as blank")
    }
    return entries
  }
}
