// Copyright (c) 2024 IBA Group.
//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Contributors:
//   IBA Group
//   Zowe Community

package org.zowe.kotlinsdk.impl.zosmf.files.api.messaging

import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * The request body for set, modify or remove file access list request
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
 * */
data class ZosmfSetFileACLBody(
  /** When true, aborts processing if an error or warning occurs */
  @AvailableSince(ZVersion.ZOS_2_1)
  var abort: Boolean? = null,

  /**
   * The default is 'follow'. 'suppress' does not follow symbolic links.
   * Because ACLs are not associated with symbolic links,
   * nothing happens if a symbolic link is encountered (setfacl -h)
   *
   * Note: At least one of the following four keywords must be specified.
   * 'modify' and 'delete' may both be specified, but not with 'delete-type' and 'set'.
   * */
  @AvailableSince(ZVersion.ZOS_2_1)
  var links: Links? = null,

  /**
   * Delete all extended ACL entries by type (setfacl -D type)
   *
   * Note: The 'delete-type' keyword cannot be specified with 'set', 'modify' or 'delete'.
   * */
  @SerializedName("delete-type")
  @AvailableSince(ZVersion.ZOS_2_1)
  var deleteType: DeleteType? = null,

  /**
   * sets (replaces) all ACLs with 'entries'. 'entries' represents a string of ACL entries. Refer to the setfacl command reference for the string format (setfacl -s entries)
   *
   * Note: The 'set' keyword cannot be specified with 'delete-type', 'modify' or 'delete'.
   * */
  @AvailableSince(ZVersion.ZOS_2_1)
  var set: String? = null,

  /**
   * Modifies the ACL entries. 'entries' represents a string of ACL entries.
   * Refer to the setfacl command reference for the string format.
   * If an ACL entry does not exist for a user or group that is specified in 'entries', it is created.
   * If an ACL entry exists for a user or group that was specified in 'entries', it is replaced.
   *
   * Note: The 'modify' keyword cannot be specified with 'delete-type' or 'set'.
   * */
  @AvailableSince(ZVersion.ZOS_2_1)
  var modify: String? = null,

  /**
   * Deletes the extended ACL entries that are specified by 'entries'.
   * 'entries' is a string of ACL entries. Refer to the setfacl command reference for the string format.
   * If an ACL entry does not exist for the user or group specified, no error is issued.
   *
   * Note: The 'delete' keyword cannot be specified with 'delete-type' or 'set'.
   * */
  @AvailableSince(ZVersion.ZOS_2_1)
  var delete: String? = null
) {
  /** Indicates the function setfacl */
  @AvailableSince(ZVersion.ZOS_2_1)
  val request = "setfacl"

  enum class Links(private val type: String) {
    FOLLOW("follow"),
    SUPPRESS("suppress");

    override fun toString(): String {
      return this.type
    }
  }

  enum class DeleteType(private val type: String) {
    ACCESS("access"),
    DIR("dir"),
    FILE("file"),
    EVERY("every");

    override fun toString(): String {
      return this.type
    }
  }
}
