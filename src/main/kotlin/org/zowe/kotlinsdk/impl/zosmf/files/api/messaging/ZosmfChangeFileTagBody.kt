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
import org.zowe.kotlinsdk.impl.zosmf.files.api.messaging.ZosmfChangeFileTagBody.Action

/**
 * The request body for change file tag request
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
 * */
data class ZosmfChangeFileTagBody(
  /** The file tag action */
  @AvailableSince(ZVersion.ZOS_2_1) var action: Action,

  /** This option can be specified only when the action is [Action.SET] */
  @AvailableSince(ZVersion.ZOS_2_1) var type: Type,

  /**
   * Specifies the coded character set in which text data is encoded, such as ASCII or EBCDIC.
   * For example, the code set for ASCII is ISO8859-1; the code set for EBCDIC is IBM-1047.
   * */
  @AvailableSince(ZVersion.ZOS_2_1) var codeSet: String,

  /** This applies a tag change to the file or directory pointed to by any encountered links */
  @AvailableSince(ZVersion.ZOS_2_1) var links: Links,

  /** When 'true', tags all the files and subdirectories in that directory (chtag -R) */
  @AvailableSince(ZVersion.ZOS_2_1) var recursive: Boolean
) {
  /** Indicates the function chtag */
  @AvailableSince(ZVersion.ZOS_2_1)
  val request = "chtag"

  enum class Action(private val type: String) {
    SET("set"),
    REMOVE("remove"),
    LIST("list");

    override fun toString(): String {
      return this.type
    }
  }

  enum class Type(private val type: String) {
    BINARY("binary"),
    MIXED("mixed"),
    TEXT("text");

    override fun toString(): String {
      return this.type
    }
  }

  enum class Links(private val type: String) {
    CHANGE("change"),
    SUPPRESS("suppress");

    override fun toString(): String {
      return this.type
    }
  }
}
