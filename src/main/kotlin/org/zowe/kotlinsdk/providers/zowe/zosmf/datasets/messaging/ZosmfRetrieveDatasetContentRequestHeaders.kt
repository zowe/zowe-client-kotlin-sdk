/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging

import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMDataType
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMRecordRange
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfDsAndFilesCommonRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfTargetSystemRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMMigratedRecall
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMObtainENQ

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-retrieve-contents-zos-data-set-member#GetReadDataSet__title__3">Retrieve the contents of a z/OS data set or member: Standard and Custom headers</a>
 * @property ifNoneMatch If-None-Match standard header
 * @property xIBMDataType X-IBM-Data-Type custom header
 * @property xIBMReturnEtag X-IBM-Return-Etag custom header
 * @property xIBMMigratedRecall X-IBM-Migrated-Recall custom header
 * @property xIBMRecordRange X-IBM-Record-Range custom header
 * @property xIBMObtainENQ X-IBM-Obtain-ENQ custom header
 * @property xIBMSessionRef X-IBM-Session-Ref custom header
 * @property xIBMReleaseENQ X-IBM-Release-ENQ custom header
 * @property xIBMDsNameEncoding X-IBM-Dsname-Encoding custom header
 */
class ZosmfRetrieveDatasetContentRequestHeaders(
  @property:AvailableSince(ZVersion.ZOS_2_1) val ifNoneMatch: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val xIBMDataType: XIBMDataType = XIBMDataType(XIBMDataType.Type.TEXT),
  @property:AvailableSince(ZVersion.ZOS_2_1) val xIBMReturnEtag: Boolean? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val xIBMMigratedRecall: XIBMMigratedRecall? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val xIBMRecordRange: XIBMRecordRange? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val xIBMObtainENQ: XIBMObtainENQ? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val xIBMSessionRef: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val xIBMReleaseENQ: Boolean? = null,
  @property:AvailableSince(ZVersion.ZOS_2_5) val xIBMDsNameEncoding: String? = null,
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
      "X-IBM-Data-Type" to xIBMDataType.toString(),
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