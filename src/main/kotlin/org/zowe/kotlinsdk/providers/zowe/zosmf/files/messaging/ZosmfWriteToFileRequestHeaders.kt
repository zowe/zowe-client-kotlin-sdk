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
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMDataType
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfDsAndFilesCommonRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfTargetSystemRequestHeaders

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-write-data-zos-unix-file#PutWriteUnixFile__title__3">Write data to a z/OS UNIX file: Standard and Custom headers</a>
 * @property ifMatch If-Match standard header
 * @property xIBMDataType X-IBM-Data-Type custom header
 */
class ZosmfWriteToFileRequestHeaders(
  @property:AvailableSince(ZVersion.ZOS_2_1) val ifMatch: String? = null,
  // TODO: create USS data type class with additional params that could be specified together with this header
  @property:AvailableSince(ZVersion.ZOS_2_1) val xIBMDataType: XIBMDataType? = XIBMDataType(XIBMDataType.Type.TEXT),
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
//      TODO: define how to specify the header
//      @Header("Content-Type") contentType: String? = "text/plain", //"application/octet-stream",
//      @property:AvailableSince(ZVersion.ZOS_2_1) val contentType: String? = null,
      "Content-Type" to "text/plain",
      "If-Match" to ifMatch,
      "X-IBM-Data-Type" to xIBMDataType?.toString(),
    ) +
      super<ZosmfDsAndFilesCommonRequestHeaders>.getHeadersMap() +
      super<ZosmfTargetSystemRequestHeaders>.getHeadersMap()
  }
}
