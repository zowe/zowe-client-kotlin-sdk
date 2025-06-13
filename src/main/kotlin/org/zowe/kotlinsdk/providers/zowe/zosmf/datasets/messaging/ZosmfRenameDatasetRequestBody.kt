/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__6">z/OS data set and member utilities: Request body</a> */
@Serializable
data class ZosmfRenameDatasetRequestBody(
  /** Indicates the function rename */
  @SerialName("request")
  @AvailableSince(ZVersion.ZOS_2_1) val request: String = "rename",

  /** The dataset to rename */
  @SerialName("from-dataset")
  @AvailableSince(ZVersion.ZOS_2_1) val src: FromDataset,

  /** enq for the "to" dataset is only allowed for renaming members */
  @SerialName("enq")
  @AvailableSince(ZVersion.ZOS_2_1) val enq: Enq,
) {
  @Serializable
  enum class Enq(private val type: String) {
    EXCLU("EXCLU"),
    SHRW("SHRW");

    override fun toString(): String {
      return this.type
    }
  }

  @Serializable
  data class FromDataset(
    /** The source data set name */
    @SerialName("dsn")
    @AvailableSince(ZVersion.ZOS_2_1) val oldDatasetName: String,

    /** If renaming a member this is the old member name */
    @SerialName("member")
    @AvailableSince(ZVersion.ZOS_2_1) val oldMemberName: String? = null
  )
}
