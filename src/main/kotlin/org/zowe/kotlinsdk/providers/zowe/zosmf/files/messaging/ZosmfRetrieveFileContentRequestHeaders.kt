/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging

import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.providers.zowe.zosmf.HttpByteRange
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMDataType
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMRecordRange
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfDsAndFilesCommonRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfTargetSystemRequestHeaders

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-retrieve-contents-zos-data-set-member#ReadUnixFile__title__3">Retrieve the contents of a z/OS UNIX file: Standard and Custom headers</a>
 * @property ifNoneMatch If-None-Match standard header
 * @property range Range standard header. Applicable to the binary data reads only
 * @property xIBMRecordRange X-IBM-Record-Range standard header
 * @property xIBMDataType X-IBM-Data-Type custom header
 */
class ZosmfRetrieveFileContentRequestHeaders(
  @property:AvailableSince(ZVersion.ZOS_2_1) val ifNoneMatch: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val range: HttpByteRange? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val xIBMRecordRange: XIBMRecordRange? = null,
  // TODO: RECORD type is not supported here, rework
  @property:AvailableSince(ZVersion.ZOS_2_1) val xIBMDataType: XIBMDataType = XIBMDataType(XIBMDataType.Type.TEXT),
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
  @property:AvailableSince(ZVersion.ZOS_2_4) override val targetSystemPassword: String? = null
) : ZosmfDsAndFilesCommonRequestHeaders, ZosmfTargetSystemRequestHeaders {
  override fun getHeadersMap(): Map<String, String?> {
    return mapOf(
      "If-None-Match" to ifNoneMatch,
      "Range" to range?.toString(),
      "X-IBM-Record-Range" to xIBMRecordRange?.toString(),
      "X-IBM-Data-Type" to xIBMDataType.toString(),
    ) +
      super<ZosmfDsAndFilesCommonRequestHeaders>.getHeadersMap() +
      super<ZosmfTargetSystemRequestHeaders>.getHeadersMap()
  }
}
