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

package org.zowe.kotlinsdk.providers.zowe.zosmf

import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.providers.zowe.HttpRequestHeaders

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=services-zos-data-set-file-rest-interface#izuhpinfo_api_restfiles__title__5">z/OS data set and file REST interface: Common HTTP Request Headers</a> */
interface ZosmfDsAndFilesCommonRequestHeaders : HttpRequestHeaders {
  /** X-IBM-Session-Limit-Wait default header */
  @AvailableOnly(ZVersion.ZOS_2_4) val sessionLimitWait: Int?

  /** X-IBM-Async-Threshold default header */
  @AvailableSince(ZVersion.ZOS_2_1) val asyncThreshold: Int?

  /** X-IBM-Response-Timeout default header */
  @AvailableSince(ZVersion.ZOS_2_1) val responseTimeout: Int?

  /** X-IBM-Request-Acctnum default header */
  @AvailableSince(ZVersion.ZOS_2_5) val requestAcctnum: String?

  /** X-IBM-Request-Proc default header */
  @AvailableSince(ZVersion.ZOS_2_5) val requestProc: String?

  /** X-IBM-Request-Region default header */
  @AvailableSince(ZVersion.ZOS_2_5) val requestRegion: String?

  override fun getHeadersMap(): Map<String, String?> {
    return mapOf(
      "X-IBM-Session-Limit-Wait" to sessionLimitWait?.toString(),
      "X-IBM-Async-Threshold" to asyncThreshold?.toString(),
      "X-IBM-Response-Timeout" to responseTimeout?.toString(),
      "X-IBM-Request-Acctnum" to requestAcctnum,
      "X-IBM-Request-Proc" to requestProc,
      "X-IBM-Request-Region" to requestRegion,
    )
  }
}