//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.datasets.data

import org.zowe.kotlinsdk.CodePage
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

// TODO: doc
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