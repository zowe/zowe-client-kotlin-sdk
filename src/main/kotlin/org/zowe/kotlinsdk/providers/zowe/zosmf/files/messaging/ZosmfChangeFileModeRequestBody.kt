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
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.definitions.ZosmfFileMode

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__3">z/OS UNIX file utilities: Request body</a> */
@Serializable
data class ZosmfChangeFileModeRequestBody(
  /** The mode value, which is specified as the POSIX symbolic form or octal value (as a JSON string) */
  @SerialName("mode")
  @AvailableSince(ZVersion.ZOS_2_1) val mode: ZosmfFileMode,

  /** This applies a mode change to the file or directory pointed to by any encountered links */
  @SerialName("links")
  @AvailableSince(ZVersion.ZOS_2_1) val links: Links? = null,

  /** When 'true', the file mode bits of the directory and all files in the file hierarchy below it are changed (chmod -R) */
  @SerialName("recursive")
  @AvailableSince(ZVersion.ZOS_2_1) val recursive: Boolean? = null
) {
  /** Indicates the function chmod */
  @SerialName("request")
  @AvailableSince(ZVersion.ZOS_2_1) val request = "chmod"

  @Serializable
  enum class Links {
    @SerialName("follow") FOLLOW,
    @SerialName("suppress") SUPPRESS
  }
}
