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

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

// TODO: doc
data class HMigrateBody(
  @SerializedName("request")
  @Expose
  private val request: String = "hmigrate",

  @SerializedName("wait")
  @Expose
  var wait: Boolean? = null
)