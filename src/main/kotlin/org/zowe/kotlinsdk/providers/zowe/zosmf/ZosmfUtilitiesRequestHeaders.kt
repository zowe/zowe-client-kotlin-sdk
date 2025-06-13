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

import io.ktor.http.ContentType
import io.ktor.http.withCharset
import io.ktor.utils.io.charsets.Charset
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__5">z/OS data set and member utilities: Custom headers</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__2">z/OS UNIX file utilities: Custom headers</a>
 */
interface ZosmfUtilitiesRequestHeaders : ZosmfDsAndFilesCommonRequestHeaders, ZosmfTargetSystemRequestHeaders {
  /** charset-name param for Content-Type header */
  @AvailableSince(ZVersion.ZOS_2_1) val charsetName: String?

  /** X-IBM-BPXK-AUTOCVT custom headers */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMBPXKAutoCvt: XIBMBPXKAutoCvt?

  override fun getHeadersMap(): Map<String, String?> {
    return mapOf(
      "Content-Type" to ContentType.Application.Json.withCharset(Charset.forName(charsetName)).toString(),
      "X-IBM-BPXK-AUTOCVT" to xIBMBPXKAutoCvt?.toString(),
    ) +
      super<ZosmfDsAndFilesCommonRequestHeaders>.getHeadersMap() +
      super<ZosmfTargetSystemRequestHeaders>.getHeadersMap()
  }
}
