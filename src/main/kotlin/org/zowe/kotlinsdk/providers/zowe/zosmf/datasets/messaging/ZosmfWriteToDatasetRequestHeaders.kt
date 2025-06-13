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
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMDataType
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfDsAndFilesCommonRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfTargetSystemRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMMigratedRecall
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMObtainENQ

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-write-data-zos-data-set-member#PutWriteDataSet__title__4">Write data to a z/OS data set or member: Custom headers</a> */
interface ZosmfWriteToDatasetRequestHeaders : ZosmfDsAndFilesCommonRequestHeaders,
  ZosmfTargetSystemRequestHeaders {
  /** content type */
  @AvailableSince(ZVersion.ZOS_2_1) val contentType: String?

  /** If-Match standard header*/
  @AvailableSince(ZVersion.ZOS_2_1) val ifMatch: String?

  /** X-IBM-Data-Type custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMDataType: XIBMDataType?

  /** X-IBM-Migrated-Recall custom header*/
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMMigratedRecall: XIBMMigratedRecall?

  /** X-IBM-Obtain-ENQ custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMObtainENQ: XIBMObtainENQ?

  /** X-IBM-Session-Ref custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMSessionRef: String?

  /** X-IBM-Release-ENQ custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMReleaseENQ: Boolean?

  /** X-IBM-Dsname-Encoding custom header */
  @AvailableSince(ZVersion.ZOS_2_5) val xIBMDsNameEncoding: String?

  override fun getHeadersMap(): Map<String, String?> {
    return mapOf(
      "Content-Type" to contentType,
      "If-Match" to ifMatch,
      "X-IBM-Data-Type" to xIBMDataType?.toString(),
      "X-IBM-Migrated-Recall" to xIBMMigratedRecall?.toString(),
      "X-IBM-Obtain-ENQ" to xIBMObtainENQ?.toString(),
      "X-IBM-Session-Ref" to xIBMSessionRef,
      "X-IBM-Release-ENQ" to xIBMReleaseENQ?.toString(),
      "X-IBM-Dsname-Encoding" to xIBMDsNameEncoding,
    ) +
      super<ZosmfDsAndFilesCommonRequestHeaders>.getHeadersMap() +
      super<ZosmfTargetSystemRequestHeaders>.getHeadersMap()
  }
}
