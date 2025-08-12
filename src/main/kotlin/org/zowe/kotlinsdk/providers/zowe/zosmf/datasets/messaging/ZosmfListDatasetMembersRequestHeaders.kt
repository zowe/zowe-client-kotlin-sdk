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
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMAttributes
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMMigratedRecall

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-members-zos-data-set#ListDataSetMembers__title__3">List the members of a z/OS data set: Custom headers</a> */
interface ZosmfListDatasetMembersRequestHeaders : ZosmfDsAndFilesCommonRequestHeaders,
  ZosmfTargetSystemRequestHeaders {
    /** X-IBM-Max-Items custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val maxItems: Int?

  /** X-IBM-Attributes custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val attributes: XIBMAttributes?

  /** X-IBM-Migrated-Recall custom header */
  @AvailableSince(ZVersion.ZOS_2_2) val migratedRecall: XIBMMigratedRecall?

  override fun getHeadersMap(): Map<String, String?> {
    return mapOf(
      "X-IBM-Max-Items" to maxItems?.toString(),
      "X-IBM-Attributes" to attributes?.toString(),
      "X-IBM-Migrated-Recall" to migratedRecall?.toString()
    ) +
      super<ZosmfDsAndFilesCommonRequestHeaders>.getHeadersMap() +
      super<ZosmfTargetSystemRequestHeaders>.getHeadersMap()
  }
}