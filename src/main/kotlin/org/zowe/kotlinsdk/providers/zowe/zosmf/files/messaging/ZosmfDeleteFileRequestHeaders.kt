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

import io.ktor.http.ContentType
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfDsAndFilesCommonRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfTargetSystemRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.definitions.XIBMOption

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-unix-file-directory#DeleteUnixFile__title__3">Delete a UNIX file or directory: Standard and Custom headers</a> */
interface ZosmfDeleteFileRequestHeaders : ZosmfDsAndFilesCommonRequestHeaders, ZosmfTargetSystemRequestHeaders {
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMOption: XIBMOption?

  override fun getHeadersMap(): Map<String, String?> {
    return mapOf(
      "Content-Type" to ContentType.Application.Json.toString(),
      "X-IBM-Option" to xIBMOption?.toString(),
    ) +
      super<ZosmfDsAndFilesCommonRequestHeaders>.getHeadersMap() +
      super<ZosmfTargetSystemRequestHeaders>.getHeadersMap()
  }
}
