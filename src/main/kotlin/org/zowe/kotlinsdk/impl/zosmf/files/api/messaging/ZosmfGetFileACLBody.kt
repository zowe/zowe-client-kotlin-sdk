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
 * The request body for get file or directory access control list request
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
 * */
data class ZosmfGetFileACLBody(
  /**
   * The default is 'access', displays the access ACL entries for a file or directory (getfacl -a).
   * 'dir' displays the directory default ACL entries (getfacl -d).
   * If the target is not a directory, a warning is issued.
   * */
  @AvailableSince(ZVersion.ZOS_2_1) var type: Type? = null,

  /**
   * The user ID or UID (as a JSON string), displays only the ACL entries for the specified types of
   * access control lists (getfacl -a, -d, -f) which affects the specified user's access (getfacl -e user)
   * */
  @AvailableSince(ZVersion.ZOS_2_1) var user: String? = null,

  /**
   * The default is 'false'. When true, displays each ACL entry, using commas to separate the ACL entries instead of newlines
   * */
  @SerializedName("use-commas")
  @AvailableSince(ZVersion.ZOS_2_1) var useCommas: Boolean? = null,

  /**
   * The default is 'false'. When true, the comment header (the first three lines of each file's output) is not to be displayed (getfacl -m)
   * */
  @SerializedName("suppress-header")
  @AvailableSince(ZVersion.ZOS_2_1) var suppressHeader: Boolean? = null,

  /**
   * The default is 'false'. When true, displays only the extended ACL entries. Does not display the base ACL entries (getfacl -o)
   * */
  @SerializedName("suppress-baseacl")
  @AvailableSince(ZVersion.ZOS_2_1) var suppressBaseACL: Boolean? = null
) {
  /** Indicates the function getfacl */
  @AvailableSince(ZVersion.ZOS_2_1)
  val request = "getfacl"

  enum class Type(private val type: String) {
    ACCESS("access"),
    DIR("dir"),
    FILE("file");

    override fun toString(): String {
      return this.type
    }
  }
}
