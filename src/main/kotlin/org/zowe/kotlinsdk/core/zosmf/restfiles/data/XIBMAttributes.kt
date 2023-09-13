//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.zosmf.restfiles.data

// TODO: doc
data class XIBMAttributes(private val type: Type = Type.DSNAME, private val isTotal: Boolean = false) {
  enum class Type(val queryVal: String) {
    BASE("base"),
    VOL("vol"),
    DSNAME("dsname"),
    MEMBER("member")
  }

  override fun toString(): String {
    return if(isTotal) "${type.queryVal},total" else type.queryVal
  }

}