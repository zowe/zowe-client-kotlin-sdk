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

import io.ktor.http.*
import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.DeleteDatasetRequest

/**
 * You can use this operation to delete sequential and partitioned datasets on a z/OS system, to delete a member of a PDS or PDSE.
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-sequential-partitioned-data-set">Delete a sequential and partitioned dataset</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-partitioned-data-set-member">Delete a partitioned dataset member</a>
 * */
class ZosmfDeleteDatasetRequest(
  /** dataset-name path param */
  @AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,

  /** volume path param */
  @AvailableSince(ZVersion.ZOS_2_1) val volume: String?,

  /** member-name path param */
  @AvailableSince(ZVersion.ZOS_2_1) val memberName: String?,

  /** X-IBM-Async-Threshold default header */
  @AvailableSince(ZVersion.ZOS_2_1) val asyncThreshold: Int? = null,

  /** X-IBM-Response-Timeout default header */
  @AvailableSince(ZVersion.ZOS_2_1) val responseTimeout: Int? = null,

  /** X-IBM-Session-Limit-Wait default header */
  @AvailableOnly(ZVersion.ZOS_2_4) val sessionLimitWait: Int? = null,

  /** X-IBM-Target-System default header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystem: String? = null,

  /** X-IBM-Request-Acctnum default header */
  @AvailableSince(ZVersion.ZOS_2_5) val requestAcctnum: String? = null,

  /** X-IBM-Request-Proc default header */
  @AvailableSince(ZVersion.ZOS_2_5) val requestProc: String? = null,

  /** X-IBM-Request-Region default header */
  @AvailableSince(ZVersion.ZOS_2_5) val requestRegion: String? = null,

  /** X-IBM-Target-System-User custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemUser: String? = null,

  /** X-IBM-Target-System-Password custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemPassword: String? = null,

  /** X-IBM-Dsname-Encoding custom header */
  @AvailableSince(ZVersion.ZOS_2_5) val xIBMDsNameEncoding: String? = null,
) : DeleteDatasetRequest(dsName) {
  override val method = HttpMethod.Delete

  private val dsAndMemberPath = if (memberName?.isNotEmpty() == true) "$dsName($memberName)" else dsName
  private val fullDsPath = if (volume?.isNotEmpty() == true) "-$volume/$dsAndMemberPath" else dsAndMemberPath

  override val path = "/zosmf/restfiles/ds/$fullDsPath"

  override val headers = mutableMapOf(
    "X-IBM-Async-Threshold" to asyncThreshold?.toString(),
    "X-IBM-Response-Timeout" to responseTimeout?.toString(),
    "X-IBM-Session-Limit-Wait" to sessionLimitWait?.toString(),
    "X-IBM-Target-System" to targetSystem,
    "X-IBM-Request-Acctnum" to requestAcctnum,
    "X-IBM-Request-Proc" to requestProc,
    "X-IBM-Request-Region" to requestRegion,
    "X-IBM-Target-System-User" to targetSystemUser,
    "X-IBM-Target-System-Password" to targetSystemPassword,
    "X-IBM-Dsname-Encoding" to xIBMDsNameEncoding
  )

  override val parameters = emptyMap<String, String>()

  override val body = null
}
