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

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-retrieve-contents-zos-data-set-member#GetReadDataSet__title__4">Retrieve the contents of a z/OS data set or member: Custom headers</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-retrieve-contents-zos-unix-file#ReadUnixFile__title__3">Retrieve the contents of a z/OS UNIX file: Standard headers</a>
 */
data class XIBMRecordRange(private val format: Format, private val sss: Int, private val nnn: Int) {
  enum class Format {
    DASHED,
    COMA_SEPARATED
  }

  override fun toString(): String {
    return when (format) {
      Format.DASHED -> "$sss-$nnn"
      Format.COMA_SEPARATED -> "$sss,$nnn"
    }
  }
}
