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

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfDsAndFilesCommonRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfTargetSystemRequestHeaders

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-sequential-partitioned-data-set#DeleteDataSet__title__4">Delete a sequential and partitioned data set: Custom headers</a> */
interface ZosmfDeleteDatasetRequestHeaders : ZosmfDsAndFilesCommonRequestHeaders, ZosmfTargetSystemRequestHeaders {
  /** X-IBM-Dsname-Encoding custom header */
  @AvailableSince(ZVersion.ZOS_2_5) val xIBMDsNameEncoding: String?

  override fun getHeadersMap(): Map<String, String?> {
    return mapOf(
      "X-IBM-Dsname-Encoding" to xIBMDsNameEncoding
    ) +
      super<ZosmfDsAndFilesCommonRequestHeaders>.getHeadersMap() +
      super<ZosmfTargetSystemRequestHeaders>.getHeadersMap()
  }
}
