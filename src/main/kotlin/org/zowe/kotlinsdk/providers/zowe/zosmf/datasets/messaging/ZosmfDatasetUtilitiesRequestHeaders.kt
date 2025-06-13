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
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfUtilitiesRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMMigratedRecall
import kotlin.collections.plus

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__5">z/OS data set and member utilities: Custom headers</a> */
interface ZosmfDatasetUtilitiesRequestHeaders : ZosmfUtilitiesRequestHeaders {
  /** X-IBM-Migrated-Recall custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMMigratedRecall: XIBMMigratedRecall?

  override fun getHeadersMap(): Map<String, String?> {
    return mapOf(
      "X-IBM-Migrated-Recall" to xIBMMigratedRecall?.toString(),
    ) +
      super<ZosmfUtilitiesRequestHeaders>.getHeadersMap()
  }
}
