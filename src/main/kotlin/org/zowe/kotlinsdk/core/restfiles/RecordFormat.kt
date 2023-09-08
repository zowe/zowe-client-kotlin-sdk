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

import com.google.gson.annotations.SerializedName

// TODO: doc
enum class RecordFormat(private val type: String) {
  @SerializedName("F")
  F("F"),
  @SerializedName("FB")
  FB("FB"),
  @SerializedName("V")
  V("V"),
  @SerializedName("VB")
  VB("VB"),
  @SerializedName("U")
  U("U"),
  @SerializedName("?")
  VSAM("VSAM"),
  @SerializedName("VA")
  VA("VA");

  override fun toString(): String {
    return type
  }
}