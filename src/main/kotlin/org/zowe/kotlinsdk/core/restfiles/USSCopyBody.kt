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
class USSCopyBody {
  data class FromFileOrDir(
    @SerializedName("request")
    private val request: String = "copy",

    @SerializedName("from")
    var from: String,

    @SerializedName("overwrite")
    var overwrite: Boolean? = null,

    @SerializedName("recursive")
    var recursive: Boolean? = null,

    @SerializedName("links")
    var links: Links? = null,

    @SerializedName("preserve")
    var preserve: Preserve? = null
  )

  data class FromDataset(
    @SerializedName("request")
    private val request: String = "copy",

    @SerializedName("from-dataset")
    var from: Dataset,

    @SerializedName("overwrite")
    var overwrite: Boolean? = null,

    @SerializedName("recursive")
    var recursive: Boolean? = null,

    @SerializedName("links")
    var links: Links? = null,

    @SerializedName("preserve")
    var preserve: Preserve? = null
  ) {
    data class Dataset(
      @SerializedName("dsn")
      @Expose
      private val datasetName: String,

      @SerializedName("member")
      @Expose
      private val memberName: String? = null,

      @SerializedName("type")
      @Expose
      private val type: XIBMDataType? = null,
    ) {
      enum class CopyType(private val type: String) {
        @SerializedName("binary")
        BINARY("binary"),

        @SerializedName("executable")
        EXECUTABLE("executable"),

        @SerializedName("text")
        TEXT("text")
      }
    }

  }

  enum class Preserve {
    @SerializedName("none")
    NONE,

    @SerializedName("modtime")
    MODTIME,

    @SerializedName("all")
    ALL
  }

  enum class Links {
    @SerializedName("NONE")
    NONE,

    @SerializedName("SRC")
    SRC,

    @SerializedName("ALL")
    ALL
  }
}