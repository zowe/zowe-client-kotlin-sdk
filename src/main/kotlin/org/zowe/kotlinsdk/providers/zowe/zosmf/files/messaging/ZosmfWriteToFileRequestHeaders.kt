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

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMDataType
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfDsAndFilesCommonRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfTargetSystemRequestHeaders

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-write-data-zos-unix-file#PutWriteUnixFile__title__3">Write data to a z/OS UNIX file: Standard and Custom headers</a> */
interface ZosmfWriteToFileRequestHeaders : ZosmfDsAndFilesCommonRequestHeaders, ZosmfTargetSystemRequestHeaders {
  /** If-Match standard header*/
  @AvailableSince(ZVersion.ZOS_2_1) val ifMatch: String?

  /** X-IBM-Data-Type custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMDataType: XIBMDataType?

  override fun getHeadersMap(): Map<String, String?> {
    return mapOf(
      "If-Match" to ifMatch,
      "X-IBM-Data-Type" to xIBMDataType?.toString(),
    ) +
      super<ZosmfDsAndFilesCommonRequestHeaders>.getHeadersMap() +
      super<ZosmfTargetSystemRequestHeaders>.getHeadersMap()
  }
}
