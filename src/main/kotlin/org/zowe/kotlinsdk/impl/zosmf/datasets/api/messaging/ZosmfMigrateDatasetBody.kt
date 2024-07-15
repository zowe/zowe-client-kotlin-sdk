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
 * Request body for migrate dataset request
 * @see <a href="https://www.ibm.com/docs/en/zos/2.1.0?topic=interface-zos-data-set-member-utilities">z/OS Dataset and member utilities</a>
 * */
data class ZosmfMigrateDatasetBody(
  /** If true then the function waits for completion of the request. If false the request is queued */
  @SerializedName("wait")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) var wait: Boolean?
) {
  /** Indicates the function hmigrate */
  @SerializedName("request")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1)
  val request = "hmigrate"
}
