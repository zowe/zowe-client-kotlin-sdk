/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf

import org.zowe.kotlinsdk.CodePage
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-retrieve-contents-zos-data-set-member#GetReadDataSet__title__4">Retrieve the contents of a z/OS data set or member: Custom headers</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-write-data-zos-data-set-member#PutWriteDataSet__title__4">Write data to a z/OS data set or member: Custom headers</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-retrieve-contents-zos-unix-file#ReadUnixFile__title__4">Retrieve the contents of a z/OS UNIX file: Custom headers</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-write-data-zos-unix-file#PutWriteUnixFile__title__4">Write data to a z/OS UNIX file: Custom headers</a>
 */
data class XIBMDataType(
  val type: Type,
  @AvailableSince(ZVersion.ZOS_2_4) val encoding: CodePage? = null
) {
  enum class Type(val value: String) {
    TEXT("text"),
    BINARY("binary"),
    RECORD("record")
  }

  override fun toString(): String {
    return if (encoding != null) "${type.value};fileEncoding=${encoding.codePage}" else type.value
  }
}
