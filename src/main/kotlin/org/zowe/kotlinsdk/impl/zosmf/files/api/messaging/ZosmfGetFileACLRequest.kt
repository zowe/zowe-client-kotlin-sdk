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
import org.zowe.kotlinsdk.core.files.api.messaging.GetFileACLRequest
import org.zowe.kotlinsdk.impl.zosmf.files.data.XIBMBPXKAutoCvt
import java.nio.charset.Charset

/**
 * Use this operation to execute getfacl function on a UNIX System Services file or directory
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
 * */
class ZosmfGetFileACLRequest(
  /** file-path-name path param */
  @AvailableSince(ZVersion.ZOS_2_1)
  override val fpName: String,

  /** The request body to execute cp from dataset function on a UNIX System Services file or directory */
  @AvailableSince(ZVersion.ZOS_2_1)
  val getFileACLBody: ZosmfGetFileACLBody,

  /** X-IBM-Async-Threshold default header */
  @AvailableSince(ZVersion.ZOS_2_1)
  val asyncThreshold: Int? = null,

  /** X-IBM-Response-Timeout default header */
  @AvailableSince(ZVersion.ZOS_2_1)
  val responseTimeout: Int? = null,

  /** X-IBM-Session-Limit-Wait default header */
  @AvailableOnly(ZVersion.ZOS_2_4)
  val sessionLimitWait: Int? = null,

  /** X-IBM-Target-System default header */
  @AvailableSince(ZVersion.ZOS_2_4)
  val targetSystem: String? = null,

  /** X-IBM-Request-Acctnum default header */
  @AvailableSince(ZVersion.ZOS_2_5)
  val requestAcctnum: String? = null,

  /** X-IBM-Request-Proc default header */
  @AvailableSince(ZVersion.ZOS_2_5)
  val requestProc: String? = null,

  /** X-IBM-Request-Region default header */
  @AvailableSince(ZVersion.ZOS_2_5)
  val requestRegion: String? = null,

  /** X-IBM-BPXK-AUTOCVT custom header */
  @AvailableSince(ZVersion.ZOS_2_1)
  val xIBMBPXKAutoCvt: XIBMBPXKAutoCvt? = null,

  /** X-IBM-Target-System-User custom header */
  @AvailableSince(ZVersion.ZOS_2_4)
  val targetSystemUser: String? = null,

  /** X-IBM-Target-System-Password custom header */
  @AvailableSince(ZVersion.ZOS_2_4)
  val targetSystemPassword: String? = null,

  /** charset-name param for Content-Type header  */
  @AvailableSince(ZVersion.ZOS_2_1)
  val charsetName: String? = "UTF-8"
) : GetFileACLRequest(fpName) {
  private val contentType = ContentType.Application.Json.withCharset(Charset.forName(charsetName))

  override val method = HttpMethod.Put

  override val path = "/zosmf/restfiles/fs/$fpName"

  override val headers = mutableMapOf(
    "X-IBM-Async-Threshold" to asyncThreshold?.toString(),
    "X-IBM-Response-Timeout" to responseTimeout?.toString(),
    "X-IBM-Session-Limit-Wait" to sessionLimitWait?.toString(),
    "X-IBM-Target-System" to targetSystem,
    "X-IBM-Request-Acctnum" to requestAcctnum,
    "X-IBM-Request-Proc" to requestProc,
    "X-IBM-Request-Region" to requestRegion,
    "X-IBM-BPXK-AUTOCVT" to xIBMBPXKAutoCvt.toString(),
    "X-IBM-Target-System-User" to targetSystemUser,
    "X-IBM-Target-System-Password" to targetSystemPassword,
    "Content-Type" to contentType.toString()
  )
  override val parameters = emptyMap<String, String>()

  override val body = getFileACLBody
}
