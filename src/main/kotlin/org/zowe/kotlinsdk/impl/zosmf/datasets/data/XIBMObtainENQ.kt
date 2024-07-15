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

package org.zowe.kotlinsdk.impl.zosmf.datasets.data

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.AvailableUntil
import org.zowe.kotlinsdk.annotations.ZVersion

// TODO: doc
enum class XIBMObtainENQ(private val type: String) {

  @AvailableSince(ZVersion.ZOS_2_1)
  @AvailableUntil(ZVersion.ZOS_2_3)
  EXCL("EXCL"),

  @AvailableSince(ZVersion.ZOS_2_3)
  EXCLU("EXCLU"),

  @AvailableSince(ZVersion.ZOS_2_1)
  SHRW("SHRW");

  override fun toString(): String {
    return this.type
  }
}
