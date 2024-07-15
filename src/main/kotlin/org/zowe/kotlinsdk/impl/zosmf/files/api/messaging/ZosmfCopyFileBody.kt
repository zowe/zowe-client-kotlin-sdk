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
 * The request body for copy file request
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
 * */
data class ZosmfCopyFileBody(
  /** The file or directory to copy */
  @AvailableSince(ZVersion.ZOS_2_1) var from: String,

  @AvailableSince(ZVersion.ZOS_2_1) var overwrite: Boolean? = null,

  /**
   * When 'true', copies all the files and subdirectories that are specified by source into a directory (cp -R)
   * */
  @AvailableSince(ZVersion.ZOS_2_1) var recursive: Boolean? = null,

  /**
   * When 'src', follows symbolic links that are specified as source file or directory (cp -H).
   * When 'all', follows symbolic links specified as source file/directory and those encountered in the tree traverse (cp -L)
   * */
  @AvailableSince(ZVersion.ZOS_2_1) var links: Links? = null,

  /**
   * When 'modtime', sets the modification and access time of each destination file to that of the corresponding source file. (cp -m).
   * When 'all', preserves the modification and access times as well as the file mode, file format, owner, and group owner (cp -p)
   * */
  @AvailableSince(ZVersion.ZOS_2_1) var preserve: Preserve? = null
) {
  /** Indicates the function copy */
  @AvailableSince(ZVersion.ZOS_2_1)
  val request = "copy"

  enum class Links(private val type: String) {
    NONE("none"),
    SRC("src"),
    ALL("all");

    override fun toString(): String {
      return this.type
    }
  }

  enum class Preserve(private val type: String) {
    NONE("none"),
    MODTIME("modtime"),
    ALL("all");

    override fun toString(): String {
      return this.type
    }
  }
}
