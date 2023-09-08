//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.restfiles

// TODO: doc
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