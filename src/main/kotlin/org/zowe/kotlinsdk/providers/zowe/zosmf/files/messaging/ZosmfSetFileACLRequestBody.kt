/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__3">z/OS UNIX file utilities: Request body</a> */
@Serializable
data class ZosmfSetFileACLRequestBody(
  /** When true, aborts processing if an error or warning occurs */
  @SerialName("abort")
  @property:AvailableSince(ZVersion.ZOS_2_1) val abort: Boolean? = null,

  /**
   * The default is 'follow'. 'suppress' does not follow symbolic links.
   * Because ACLs are not associated with symbolic links,
   * nothing happens if a symbolic link is encountered (setfacl -h)
   *
   * Note: At least one of the following four keywords must be specified.
   * 'modify' and 'delete' may both be specified, but not with 'delete-type' and 'set'.
   */
  @SerialName("links")
  @property:AvailableSince(ZVersion.ZOS_2_1) val links: Links? = null,

  /**
   * Delete all extended ACL entries by type (setfacl -D type)
   *
   * Note: The 'delete-type' keyword cannot be specified with 'set', 'modify' or 'delete'.
   */
  @SerialName("delete-type")
  @property:AvailableSince(ZVersion.ZOS_2_1) val deleteType: DeleteType? = null,

  /**
   * sets (replaces) all ACLs with 'entries'. 'entries' represents a string of ACL entries.
   * Refer to the setfacl command reference for the string format (setfacl -s entries)
   *
   * Note: The 'set' keyword cannot be specified with 'delete-type', 'modify' or 'delete'.
   */
  @SerialName("set")
  @property:AvailableSince(ZVersion.ZOS_2_1) val set: String? = null,

  /**
   * Modifies the ACL entries. 'entries' represents a string of ACL entries.
   * Refer to the setfacl command reference for the string format.
   * If an ACL entry does not exist for a user or group that is specified in 'entries', it is created.
   * If an ACL entry exists for a user or group that was specified in 'entries', it is replaced.
   *
   * Note: The 'modify' keyword cannot be specified with 'delete-type' or 'set'.
   */
  @SerialName("modify")
  @property:AvailableSince(ZVersion.ZOS_2_1) val modify: String? = null,

  /**
   * Deletes the extended ACL entries that are specified by 'entries'.
   * 'entries' is a string of ACL entries. Refer to the setfacl command reference for the string format.
   * If an ACL entry does not exist for the user or group specified, no error is issued.
   *
   * Note: The 'delete' keyword cannot be specified with 'delete-type' or 'set'.
   */
  @SerialName("delete")
  @property:AvailableSince(ZVersion.ZOS_2_1) val delete: String? = null
) {
  /** Indicates the function setfacl */
  @SerialName("request")
  @AvailableSince(ZVersion.ZOS_2_1) val request = "setfacl"

  @Serializable
  enum class Links {
    @SerialName("follow") FOLLOW,
    @SerialName("suppress") SUPPRESS
  }

  @Serializable
  enum class DeleteType {
    @SerialName("access") ACCESS,
    @SerialName("dir") DIR,
    @SerialName("file") FILE,
    @SerialName("every") EVERY
  }
}
