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
class ZOSCopyBody {
  data class FromFile(
    @SerializedName("request")
    @Expose
    var request: String = "copy",

    @SerializedName("from-file")
    @Expose
    var file: File,

    @SerializedName("enq")
    @Expose
    var enq: Enq? = null,

    @SerializedName("replace")
    var replace: Boolean? = null
  ) {

    enum class Enq(private var type: String) {
      EXCLU("EXCLU"),
      SHRW("SHRW"),
      SHR("SHR");

      override fun toString(): String {
        return type
      }
    }

    data class File(
      @SerializedName("filename")
      @Expose
      var fileName: String,

      @SerializedName("type")
      @Expose
      var type: CopyType? = null,
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

  data class FromDataset(
    @SerializedName("request")
    @Expose
    private val request: String = "copy",

    @SerializedName("from-dataset")
    @Expose
    var dataset: Dataset,

    @SerializedName("enq")
    @Expose
    var enq: Enq? = null,

    @SerializedName("replace")
    var replace: Boolean? = null
  ) {
    enum class Enq(private var type: String) {
      @SerializedName("EXCLU")
      EXCLU("EXCLU"),

      @SerializedName("SHRW")
      SHRW("SHRW"),

      @SerializedName("SHR")
      SHR("SHR");

      override fun toString(): String {
        return type
      }
    }

    data class Dataset(
      @SerializedName("dsn")
      @Expose
      var datasetName: String,

      @SerializedName("member")
      @Expose
      var memberName: String? = null,

      @SerializedName("volser")
      @Expose
      var volumeSerial: String? = null,

      @SerializedName("alias")
      @Expose
      var alias: Boolean? = null,
    )
  }
}