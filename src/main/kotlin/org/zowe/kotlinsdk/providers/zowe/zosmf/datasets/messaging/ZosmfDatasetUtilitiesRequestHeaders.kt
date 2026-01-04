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
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMBPXKAutoCvt
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfUtilitiesRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMMigratedRecall
import kotlin.collections.plus

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__5">z/OS data set and member utilities: Custom headers</a>
 * @property xIBMMigratedRecall X-IBM-Migrated-Recall custom header
 */
class ZosmfDatasetUtilitiesRequestHeaders(
  @property:AvailableSince(ZVersion.ZOS_2_1) val xIBMMigratedRecall: XIBMMigratedRecall? = null,
  // ZosmfUtilitiesRequestHeaders
  @property:AvailableSince(ZVersion.ZOS_2_1) override val charsetName: String? = "UTF-8",
  @property:AvailableSince(ZVersion.ZOS_2_1) override val xIBMBPXKAutoCvt: XIBMBPXKAutoCvt? = null,
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
  @property:AvailableSince(ZVersion.ZOS_2_4) override val targetSystemPassword: String? = null,
) : ZosmfUtilitiesRequestHeaders {
  override fun getHeadersMap(): Map<String, String?> {
    return mapOf(
      "X-IBM-Migrated-Recall" to xIBMMigratedRecall?.toString(),
    ) +
      super.getHeadersMap()
  }
}
