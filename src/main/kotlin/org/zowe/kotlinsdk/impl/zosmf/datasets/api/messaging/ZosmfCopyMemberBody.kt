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

package org.zowe.kotlinsdk.impl.zosmf.datasets.api.messaging

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * Request body for copy member request
 * @see <a href="https://www.ibm.com/docs/en/zos/2.1.0?topic=interface-zos-data-set-member-utilities">z/OS Dataset and member utilities</a>
 * */
data class ZosmfCopyMemberBody(
  /** from-file request body param */
  @SerializedName("from-file")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) var src: FromFile,

  /** enq request body param */
  @SerializedName("enq")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) var enq: Enq?,

  /** replace request body param */
  @SerializedName("replace")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) var replace: Boolean?
) {
  /** Indicates the function copy */
  @SerializedName("request")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1)
  val request = "copy"

  enum class Enq(private val type: String) {
    SHR("SHR"),
    SHRW("SHRW"),
    EXCLU("EXCLU");

    override fun toString(): String {
      return this.type
    }
  }

  /**
   * from-file request body param for copy member request
   * */
  data class FromFile(
    /** The absolute source filename */
    @SerializedName("filename")
    @Expose
    val fileName: String,

    /** The file type */
    @SerializedName("type")
    @Expose
    val type: FileType?
  ) {
    enum class FileType(private val type: String) {
      BINARY("binary"),
      EXECUTABLE("executable"),
      TEXT("text");

      override fun toString(): String {
        return this.type
      }
    }
  }
}
