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

package org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfDsAndFilesCommonRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfTargetSystemRequestHeaders

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-files-directories-unix-file-path#ListUNIXfiles__title__3">List the files and directories of a UNIX file path: Custom headers</a> */
interface ZosmfListFilesRequestHeaders : ZosmfDsAndFilesCommonRequestHeaders, ZosmfTargetSystemRequestHeaders {
  /** X-IBM-Max-Items custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val maxItems: Int?

    /** X-IBM-Lstat custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val lstat: Boolean?

  override fun getHeadersMap(): Map<String, String?> {
    return mapOf(
      "X-IBM-Max-Items" to maxItems?.toString(),
      "X-IBM-Lstat" to lstat?.toString()
    ) +
      super<ZosmfDsAndFilesCommonRequestHeaders>.getHeadersMap() +
      super<ZosmfTargetSystemRequestHeaders>.getHeadersMap()
  }
}
