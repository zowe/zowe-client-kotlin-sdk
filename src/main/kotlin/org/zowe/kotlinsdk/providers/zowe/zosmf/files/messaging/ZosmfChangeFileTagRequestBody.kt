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

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__3">z/OS UNIX file utilities: Request body</a> */
@Serializable
data class ZosmfChangeFileTagRequestBody(
  /** The file tag action */
  @SerialName("action")
  @property:AvailableSince(ZVersion.ZOS_2_1) var action: Action,

  /** This option can be specified only when the action is [Action.SET] */
  @SerialName("type")
  @property:AvailableSince(ZVersion.ZOS_2_1) var type: Type? = null,

  /**
   * Specifies the coded character set in which text data is encoded, such as ASCII or EBCDIC.
   * For example, the code set for ASCII is ISO8859-1; the code set for EBCDIC is IBM-1047.
   */
  @SerialName("codeSet")
  @property:AvailableSince(ZVersion.ZOS_2_1) var codeSet: String? = null,

  /** This applies a tag change to the file or directory pointed to by any encountered links */
  @SerialName("links")
  @property:AvailableSince(ZVersion.ZOS_2_1) var links: Links ? = null,

  /** When 'true', tags all the files and subdirectories in that directory (chtag -R) */
  @SerialName("recursive")
  @property:AvailableSince(ZVersion.ZOS_2_1) val recursive: Boolean? = null
) {
  /** Indicates the function chtag */
  @SerialName("request")
  @AvailableSince(ZVersion.ZOS_2_1) val request = "chtag"

  @Serializable
  enum class Action {
    @SerialName("set") SET,
    @SerialName("remove") REMOVE,
    @SerialName("list") LIST
  }

  @Serializable
  enum class Type {
    @SerialName("binary") BINARY,
    @SerialName("mixed") MIXED,
    @SerialName("text") TEXT
  }

  @Serializable
  enum class Links {
    @SerialName("change") CHANGE,
    @SerialName("suppress") SUPPRESS
  }
}
