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
import org.zowe.kotlinsdk.core.datasets.api.messaging.WriteToDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMDataType
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfDsAndFilesCommonRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfTargetSystemRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMMigratedRecall
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMObtainENQ

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-write-data-zos-data-set-member#PutWriteDataSet__title__4">Write data to a z/OS data set or member: Custom headers</a>
 * @property contentType content type (if TEXT, then "text/plain; charset=UTF-8" header is used)
 * @property ifMatch If-Match standard header
 * @property xIBMDataType X-IBM-Data-Type custom header
 * @property xIBMMigratedRecall X-IBM-Migrated-Recall custom header
 * @property xIBMObtainENQ X-IBM-Obtain-ENQ custom header
 * @property xIBMSessionRef X-IBM-Session-Ref custom header
 * @property xIBMReleaseENQ X-IBM-Release-ENQ custom header
 * @property xIBMDsNameEncoding X-IBM-Dsname-Encoding custom header
 */
class ZosmfWriteToDatasetRequestHeaders(
  // TODO: process Content-Type header more universally (together with X-IBM-Data-Type)
  @property:AvailableSince(ZVersion.ZOS_2_1) val contentType: WriteToDatasetRequest.ContentType = WriteToDatasetRequest.ContentType.TEXT,
  @property:AvailableSince(ZVersion.ZOS_2_1) val contentEncoding: String = "UTF-8",
  @property:AvailableSince(ZVersion.ZOS_2_1) val ifMatch: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val xIBMDataType: XIBMDataType? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val xIBMMigratedRecall: XIBMMigratedRecall? = null,
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
    val optionalHeaders = listOfNotNull(
      if (contentType == WriteToDatasetRequest.ContentType.TEXT)
        "Content-Type" to "text/plain; charset=$contentEncoding"
      else null
    ).toMap()
    return mapOf(
      "If-Match" to ifMatch,
      "X-IBM-Data-Type" to xIBMDataType?.toString(),
      "X-IBM-Migrated-Recall" to xIBMMigratedRecall?.toString(),
      "X-IBM-Obtain-ENQ" to xIBMObtainENQ?.toString(),
      "X-IBM-Session-Ref" to xIBMSessionRef,
      "X-IBM-Release-ENQ" to xIBMReleaseENQ?.toString(),
      "X-IBM-Dsname-Encoding" to xIBMDsNameEncoding,
    ) +
      optionalHeaders +
      super<ZosmfDsAndFilesCommonRequestHeaders>.getHeadersMap() +
      super<ZosmfTargetSystemRequestHeaders>.getHeadersMap()
  }
}
