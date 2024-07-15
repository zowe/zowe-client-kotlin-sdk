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
import io.ktor.utils.io.charsets.*
import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.CopyMemberRequest
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.XIBMBPXKAutoCvt
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.XIBMMigratedRecall

/**
 * Use this operation to copy member
 * @see <a href="https://www.ibm.com/docs/en/zos/2.1.0?topic=interface-zos-data-set-member-utilities">z/OS Dataset and member utilities</a>
 * */
class ZosmfCopyMemberRequest(
  /** to-dataset-name path param */
  @AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,

  /** The request body to copy member */
  @AvailableSince(ZVersion.ZOS_2_1) val copyMemberBody: ZosmfCopyMemberBody,

  /** to-volser path param */
  @AvailableSince(ZVersion.ZOS_2_1) val volser: String?,

  /** member-name path param */
  @AvailableSince(ZVersion.ZOS_2_1) val memberName: String?,

  /** charset-name param for Content-Type header  */
  @AvailableSince(ZVersion.ZOS_2_1) val charsetName: String? = "UTF-8",

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

  /** X-IBM-BPXK-AUTOCVT custom headers */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMBPXKAutoCvt: XIBMBPXKAutoCvt?,

  /** X-IBM-Migrated-Recall custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMMigratedRecall: XIBMMigratedRecall? = null,

  /** X-IBM-Target-System-User custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemUser: String? = null,

  /** X-IBM-Target-System-Password custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemPassword: String? = null
) : CopyMemberRequest(dsName) {
  private val contentType = ContentType.Application.Json.withCharset(Charset.forName(charsetName))

  override val method = HttpMethod.Put

  private val dsAndMemberPath = if (memberName?.isNotEmpty() == true) "$dsName($memberName)" else dsName
  private val fullDsPath = if (volser?.isNotEmpty() == true) "-$volser/$dsAndMemberPath" else dsAndMemberPath

  override val path = "/zosmf/restfiles/ds/$fullDsPath"

  override val headers = mutableMapOf(
    "X-IBM-Async-Threshold" to asyncThreshold?.toString(),
    "X-IBM-Response-Timeout" to responseTimeout?.toString(),
    "X-IBM-Session-Limit-Wait" to sessionLimitWait?.toString(),
    "X-IBM-Target-System" to targetSystem,
    "X-IBM-Request-Acctnum" to requestAcctnum,
    "X-IBM-Request-Proc" to requestProc,
    "X-IBM-Request-Region" to requestRegion,
    "Content-Type" to contentType.toString(),
    "X-IBM-BPXK-AUTOCVT" to xIBMBPXKAutoCvt?.toString(),
    "X-IBM-Migrated-Recall" to xIBMMigratedRecall?.toString(),
    "X-IBM-Target-System-User" to targetSystemUser,
    "X-IBM-Target-System-Password" to targetSystemPassword
  )

  override val parameters = emptyMap<String, String>()

  override val body = copyMemberBody
}
