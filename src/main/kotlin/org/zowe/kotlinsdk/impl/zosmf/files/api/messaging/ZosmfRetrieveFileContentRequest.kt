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

package org.zowe.kotlinsdk.impl.zosmf.files.api.messaging

import io.ktor.http.*
import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.files.api.messaging.RetrieveFileContentRequest
import org.zowe.kotlinsdk.impl.zosmf.common.XIBMDataType
import org.zowe.kotlinsdk.impl.zosmf.common.XIBMRecordRange

/**
 * Use this operation to retrieve the contents of a z/OS UNIX System Services file
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-unix-file">Retrieve the contents of a z/OS UNIX file</a>
 * */
class ZosmfRetrieveFileContentRequest(
  /** filepath-name path param */
  @AvailableSince(ZVersion.ZOS_2_1) override val fpName: String,

  /** search optional query param */
  @AvailableSince(ZVersion.ZOS_2_1) val search: String? = null,

  /** research optional query param */
  @AvailableSince(ZVersion.ZOS_2_1) val research: String? = null,

  /** insensitive optional query param */
  @AvailableSince(ZVersion.ZOS_2_1) val insensitive: Boolean? = null,

  /** maxreturnsize optional query param */
  @AvailableSince(ZVersion.ZOS_2_1) val maxReturnSize: Int? = null,

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

  /** If-None-Match standard header*/
  @AvailableSince(ZVersion.ZOS_2_1) val ifNoneMatch: String? = null,

  /** Range standard header*/
  @AvailableSince(ZVersion.ZOS_2_1) val range: Int? = null,

  /** X-IBM-Record-Range standard header*/
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMRecordRange: XIBMRecordRange? = null,

  /** X-IBM-Data-Type custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMDataType: XIBMDataType? = null,

  /** X-IBM-Target-System-User custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemUser: String? = null,

  /** X-IBM-Target-System-Password custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemPassword: String? = null
) : RetrieveFileContentRequest(fpName) {
  override val method = HttpMethod.Get

  override val path = "/zosmf/restfiles/fs/$fpName"

  override val headers = mutableMapOf(
    "X-IBM-Async-Threshold" to asyncThreshold?.toString(),
    "X-IBM-Response-Timeout" to responseTimeout?.toString(),
    "X-IBM-Session-Limit-Wait" to sessionLimitWait?.toString(),
    "X-IBM-Target-System" to targetSystem,
    "X-IBM-Request-Acctnum" to requestAcctnum,
    "X-IBM-Request-Proc" to requestProc,
    "X-IBM-Request-Region" to requestRegion,
    "If-None-Match" to ifNoneMatch,
    "Range" to range?.toString(),
    "X-IBM-Record-Range" to xIBMRecordRange?.toString(),
    "X-IBM-Data-Type" to xIBMDataType?.toString(),
    "X-IBM-Target-System-User" to targetSystemUser,
    "X-IBM-Target-System-Password" to targetSystemPassword
  )

  override val parameters = mutableMapOf(
    "search" to search,
    "research" to research,
    "insensitive" to insensitive?.toString(),
    "maxreturnsize" to maxReturnSize?.toString()
  )

  override val body = null
}
