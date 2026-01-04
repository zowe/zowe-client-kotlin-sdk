/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__6">z/OS data set and member utilities: Request body</a>
 * @property request indicates the function copy
 * @property src from-dataset request body param
 * @property enq enq request body param
 * @property replace replace request body param
 */
@Serializable
data class ZosmfCopyDatasetRequestBody(
  @SerialName("request")
  @property:AvailableSince(ZVersion.ZOS_2_1) val request: String = "copy",

  @SerialName("from-dataset")
  @property:AvailableSince(ZVersion.ZOS_2_1) var src: FromDataset,

  @SerialName("enq")
  @property:AvailableSince(ZVersion.ZOS_2_1) var enq: Enq?,

  @SerialName("replace")
  @property:AvailableSince(ZVersion.ZOS_2_1) var replace: Boolean?
) {
  @Serializable
  enum class Enq(private val type: String) {
    SHR("SHR"),
    SHRW("SHRW"),
    EXCLU("EXCLU");

    override fun toString(): String {
      return type
    }
  }

  @Serializable
  data class FromDataset(
    /** The source dataset */
    @SerialName("dsn")
    val datasetName: String,

    /** Used to specify a member; "*" means all members */
    @SerialName("member")
    val memberName: String?,

    /** May be specified if dsn is not cataloged */
    @SerialName("volser")
    val volser: String?,

    /** if true, aliases are copied along with main member;if false(default), alias relationships are not maintained */
    @SerialName("alias")
    val alias: Boolean?,
  )
}
