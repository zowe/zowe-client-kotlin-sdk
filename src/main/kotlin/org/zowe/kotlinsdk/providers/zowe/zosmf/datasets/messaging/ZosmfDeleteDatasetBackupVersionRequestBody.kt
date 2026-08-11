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
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__6">z/OS data set and member utilities: Request body</a>
 * @property request indicates the function hdelete
 * @property wait if true then the function waits for completion of the request. If false the request is queued
 * @property purge if true then the function uses the PURGE=YES on ARCHDEL request. If false the function uses the PURGE=NO on ARCHDEL request
 */
@Serializable
data class ZosmfDeleteDatasetBackupVersionRequestBody(
  @SerialName("request")
  @property:AvailableSince(ZVersion.ZOS_2_1) val request: String = "hdelete",

  @SerialName("wait")
  @property:AvailableSince(ZVersion.ZOS_2_1) var wait: Boolean?,

  @SerialName("purge")
  @property:AvailableSince(ZVersion.ZOS_2_1) var purge: Boolean?
)
