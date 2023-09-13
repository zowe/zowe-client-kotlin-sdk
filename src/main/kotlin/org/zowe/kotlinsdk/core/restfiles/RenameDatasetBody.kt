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

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

// TODO: doc
data class RenameDatasetBody(
  @SerializedName("request")
  @Expose
  var request: String = "rename",

  @SerializedName("from-dataset")
  @Expose
  var fromDataset: FromDataset,

  @SerializedName("enq")
  @Expose
  var enq: Enq? = null
) {
  enum class Enq(private var type: String) {

    @SerializedName("EXCLU")
    EXCLU("EXCLU"),
    @SerializedName("SHRW")
    SHRW("SHRW");

    override fun toString(): String {
      return type
    }
  }

  data class FromDataset(
    @SerializedName("dsn")
    @Expose
    var oldDatasetName: String,

    @SerializedName("member")
    @Expose
    var oldMemberName: String? = null,
  )
}