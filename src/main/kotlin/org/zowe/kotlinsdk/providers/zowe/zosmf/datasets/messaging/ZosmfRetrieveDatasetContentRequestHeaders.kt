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
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMRecordRange
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfDsAndFilesCommonRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfTargetSystemRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMMigratedRecall
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMObtainENQ

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-data-set-member#GetReadDataSet__title__3">Retrieve the contents of a z/OS data set or member: Standard and Custom headers</a> */
interface ZosmfRetrieveDatasetContentRequestHeaders : ZosmfDsAndFilesCommonRequestHeaders,
  ZosmfTargetSystemRequestHeaders {
  /** If-None-Match standard header */
  @AvailableSince(ZVersion.ZOS_2_1) val ifNoneMatch: String?

  /** X-IBM-Data-Type custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMDataType: XIBMDataType?

  /** X-IBM-Return-Etag custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMReturnEtag: Boolean?

  /** X-IBM-Migrated-Recall custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMMigratedRecall: XIBMMigratedRecall?

  /** X-IBM-Record-Range custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMRecordRange: XIBMRecordRange?

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
      "If-None-Match" to ifNoneMatch,
      "X-IBM-Data-Type" to xIBMDataType?.toString(),
      "X-IBM-Return-Etag" to xIBMReturnEtag?.toString(),
      "X-IBM-Migrated-Recall" to xIBMMigratedRecall?.toString(),
      "X-IBM-Record-Range" to xIBMRecordRange?.toString(),
      "X-IBM-Obtain-ENQ" to xIBMObtainENQ?.toString(),
      "X-IBM-Session-Ref" to xIBMSessionRef,
      "X-IBM-Release-ENQ" to xIBMReleaseENQ?.toString(),
      "X-IBM-Dsname-Encoding" to xIBMDsNameEncoding,
    ) +
      super<ZosmfDsAndFilesCommonRequestHeaders>.getHeadersMap() +
      super<ZosmfTargetSystemRequestHeaders>.getHeadersMap()
  }
}