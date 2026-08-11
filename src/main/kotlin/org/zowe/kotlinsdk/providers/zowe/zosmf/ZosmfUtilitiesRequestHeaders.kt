/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf

import io.ktor.http.ContentType
import io.ktor.http.withCharset
import io.ktor.utils.io.charsets.Charset
import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__5">z/OS data set and member utilities: Custom headers</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__2">z/OS UNIX file utilities: Custom headers</a>
 * @property charsetName charset-name param for Content-Type header
 * @property xIBMBPXKAutoCvt X-IBM-BPXK-AUTOCVT custom headers
 */
open class ZosmfUtilitiesRequestHeaders(
  @property:AvailableSince(ZVersion.ZOS_2_1) val charsetName: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val xIBMBPXKAutoCvt: XIBMBPXKAutoCvt? = null,
  // ZosmfDsAndFilesCommonRequestHeaders
  @property:AvailableOnly(ZVersion.ZOS_2_4) override val sessionLimitWait: Int? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val asyncThreshold: Int? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val responseTimeout: Int? = null,
  @property:AvailableSince(ZVersion.ZOS_2_5) override val requestAcctnum: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_5) override val requestProc: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_5) override val requestRegion: String? = null,
  // ZosmfTargetSystemRequestHeaders
  @property:AvailableSince(ZVersion.ZOS_2_4) override val targetSystem: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_4) override val targetSystemUser: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_4) override val targetSystemPassword: String? = null,
) : ZosmfDsAndFilesCommonRequestHeaders, ZosmfTargetSystemRequestHeaders {
  override fun getHeadersMap(): Map<String, String?> {
    return mapOf(
      "Content-Type" to ContentType.Application.Json.withCharset(Charset.forName(charsetName)).toString(),
      "X-IBM-BPXK-AUTOCVT" to xIBMBPXKAutoCvt?.toString(),
    ) +
      super<ZosmfDsAndFilesCommonRequestHeaders>.getHeadersMap() +
      super<ZosmfTargetSystemRequestHeaders>.getHeadersMap()
  }
}
