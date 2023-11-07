//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.restfiles

// TODO: doc
data class XIBMAttr(private val type: Type = Type.BASE, private val isTotal: Boolean = false) {

  enum class Type(val queryVal: String) {
    BASE("base"),
    VOL("vol"),
    DSNAME("dsname"),
    MEMBER("member")
  }

  override fun toString(): String {
    val suffix = if (isTotal) ",total" else ""
    return type.queryVal + suffix
  }

}