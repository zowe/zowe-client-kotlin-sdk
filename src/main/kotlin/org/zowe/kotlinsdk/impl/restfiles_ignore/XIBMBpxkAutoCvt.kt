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

package org.zowe.kotlinsdk.impl.restfiles_ignore

// TODO: doc
enum class XIBMBpxkAutoCvt(private val type: String) {
  ON("on"),
  ALL("all"),
  OFF("off");

  override fun toString(): String {
    return type
  }
}
