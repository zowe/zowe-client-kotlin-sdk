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

// TODO: doc
open class ReqType {
  companion object {
    const val BASE = "base"
    const val VOL = "vol"
  }
}

// TODO: doc
class DsReqType : ReqType() {
  companion object {
    const val DSNAME = "dsname"
  }
}

// TODO: doc
class MemReqType : ReqType() {
  companion object {
    const val MEMBER = "member"
  }
}

// TODO: doc
data class XIBMAttributes(private var type: String = ReqType.BASE, private val isTotal: Boolean = false) {
  init {
    if (!(type == ReqType.BASE || type == ReqType.VOL || type == DsReqType.DSNAME || type == MemReqType.MEMBER)) {
      type = ReqType.BASE
    }
  }

  override fun toString(): String {
    return if(isTotal) "${type},total" else type
  }
}
