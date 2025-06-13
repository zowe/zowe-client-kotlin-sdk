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
data class ZosmfMigrateDatasetRequestBody(
  /** Indicates the function hmigrate */
  @SerialName("request")
  @AvailableSince(ZVersion.ZOS_2_1) val request: String = "hmigrate",

  /** If true then the function waits for completion of the request. If false the request is queued */
  @SerialName("wait")
  @AvailableSince(ZVersion.ZOS_2_1) var wait: Boolean?
)
