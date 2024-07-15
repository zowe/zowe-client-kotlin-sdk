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

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * The request body for change file owner request
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
 * */
data class ZosmfChangeFileOwnerBody(
  /** The user ID or UID (as a JSON string) */
  @AvailableSince(ZVersion.ZOS_2_1) var owner: String,

  /** The group ID or GID (as a JSON string) */
  @AvailableSince(ZVersion.ZOS_2_1) var group: String,

  /** This applies an owner change to the file or directory pointed to by any encountered links */
  @AvailableSince(ZVersion.ZOS_2_1) var links: Links,

  /** When 'true', changes all the files and subdirectories in that directory to belong to the specified owner and group, if :group is specified (chown -R) */
  @AvailableSince(ZVersion.ZOS_2_1) var recursive: Boolean
) {
  /** Indicates the function chown */
  @AvailableSince(ZVersion.ZOS_2_1)
  val request = "chown"

  enum class Links(private val type: String) {
    FOLLOW("follow"),
    CHANGE("change");

    override fun toString(): String {
      return this.type
    }
  }
}
