/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__3">z/OS UNIX file utilities: Request body</a> */
@Serializable
data class ZosmfCopyFileRequestBody(
  /** The file or directory to copy */
  @SerialName("from")
  @AvailableSince(ZVersion.ZOS_2_1) val from: String?,

  /** The file or directory to copy */
  @SerialName("from-dataset")
  @AvailableSince(ZVersion.ZOS_2_1) val fromDataset: FromDataset? = null,

  @SerialName("overwrite")
  @AvailableSince(ZVersion.ZOS_2_1) val overwrite: Boolean? = null,

  /** When 'true', copies all the files and subdirectories that are specified by source into a directory (cp -R) */
  @SerialName("recursive")
  @AvailableSince(ZVersion.ZOS_2_1) val recursive: Boolean? = null,

  /**
   * When 'src', follows symbolic links that are specified as source file or directory (cp -H).
   * When 'all', follows symbolic links specified as source file/directory and those encountered in the tree traverse (cp -L)
   */
  @SerialName("links")
  @AvailableSince(ZVersion.ZOS_2_1) val links: Links? = null,

  /**
   * When 'modtime', sets the modification and access time of each destination file to that of the corresponding source file. (cp -m).
   * When 'all', preserves the modification and access times as well as the file mode, file format, owner, and group owner (cp -p)
   */
  @SerialName("preserve")
  @AvailableSince(ZVersion.ZOS_2_1) val preserve: Preserve? = null
) {
  /** Indicates the function copy */
  @SerialName("request")
  @AvailableSince(ZVersion.ZOS_2_1) val request = "copy"

  @Serializable
  data class FromDataset(
    /** The fully qualified dataset name */
    @SerialName("dsn")
    @AvailableSince(ZVersion.ZOS_2_1) val datasetName: String,

    /** The dataset member to copy */
    @SerialName("member")
    @AvailableSince(ZVersion.ZOS_2_1) val memberName: String? = null,

    /** dataset type */
    @AvailableSince(ZVersion.ZOS_2_1) val type: Type? = null
  ) {
    @Serializable
    enum class Type {
      @SerialName("binary") BINARY,
      @SerialName("executable") EXECUTABLE,
      @SerialName("text") TEXT
    }
  }

  @Serializable
  enum class Links {
    @SerialName("none") NONE,
    @SerialName("src") SRC,
    @SerialName("all") ALL
  }

  @Serializable
  enum class Preserve {
    @SerialName("none") NONE,
    @SerialName("modtime") MODTIME,
    @SerialName("all") ALL
  }
}
