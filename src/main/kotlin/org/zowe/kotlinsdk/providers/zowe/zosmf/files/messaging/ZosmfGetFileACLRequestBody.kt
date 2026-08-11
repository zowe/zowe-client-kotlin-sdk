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
data class ZosmfGetFileACLRequestBody(
  /**
   * The default is 'access', displays the access ACL entries for a file or directory (getfacl -a).
   * 'dir' displays the directory default ACL entries (getfacl -d).
   * If the target is not a directory, a warning is issued.
   */
  @SerialName("type")
  @property:AvailableSince(ZVersion.ZOS_2_1) val type: Type? = null,

  /**
   * The user ID or UID (as a JSON string), displays only the ACL entries for the specified types of
   * access control lists (getfacl -a, -d, -f) which affects the specified user's access (getfacl -e user)
   */
  @SerialName("user")
  @property:AvailableSince(ZVersion.ZOS_2_1) val user: String? = null,

  /**
   * The default is 'false'. When true, displays each ACL entry, using commas to separate the ACL entries instead of newlines
   */
  @SerialName("use-commas")
  @property:AvailableSince(ZVersion.ZOS_2_1) val useCommas: Boolean? = null,

  /**
   * The default is 'false'. When true, the comment header (the first three lines of each file's output) is not to be displayed (getfacl -m)
   */
  @SerialName("suppress-header")
  @property:AvailableSince(ZVersion.ZOS_2_1) val suppressHeader: Boolean? = null,

  /**
   * The default is 'false'. When true, displays only the extended ACL entries. Does not display the base ACL entries (getfacl -o)
   */
  @SerialName("suppress-baseacl")
  @property:AvailableSince(ZVersion.ZOS_2_1) val suppressBaseACL: Boolean? = null
) {
  /** Indicates the function getfacl */
  @SerialName("request")
  @AvailableSince(ZVersion.ZOS_2_1) val request = "getfacl"

  @Serializable
  enum class Type {
    @SerialName("access") ACCESS,
    @SerialName("dir") DIR,
    @SerialName("file") FILE
  }
}
