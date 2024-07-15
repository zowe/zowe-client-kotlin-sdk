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
import org.zowe.kotlinsdk.impl.zosmf.files.data.FileMode

/**
 * The request body for change file mode request
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
 * */
data class ZosmfChangeFileModeBody(
  /** The mode value, which is specified as the POSIX symbolic form or octal value (as a JSON string) */
  @AvailableSince(ZVersion.ZOS_2_1) var mode: FileMode,

  /** This applies a mode change to the file or directory pointed to by any encountered links */
  @AvailableSince(ZVersion.ZOS_2_1) var links: Links,

  /** When 'true', the file mode bits of the directory and all files in the file hierarchy below it are changed (chmod -R) */
  @AvailableSince(ZVersion.ZOS_2_1) var recursive: Boolean
) {
  /** Indicates the function chmod */
  @AvailableSince(ZVersion.ZOS_2_1)
  val request = "chmod"

  enum class Links(private val type: String) {
    FOLLOW("follow"),
    SUPPRESS("suppress");

    override fun toString(): String {
      return this.type
    }
  }
}
