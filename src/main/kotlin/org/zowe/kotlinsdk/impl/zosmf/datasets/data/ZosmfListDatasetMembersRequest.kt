//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.datasets.data

import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.data.ListDatasetMembersRequest

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/2.5.0?topic=interface-list-members-zos-data-set
 */
class ZosmfListDatasetMembersRequest(
  /** dataset-name path param */
  @AvailableSince(ZVersion.ZOS_2_1) private val dslevel: String,

  /** X-IBM-Async-Threshold header */
  @AvailableSince(ZVersion.ZOS_2_1) val asyncThreshold: Int? = null,

  /** X-IBM-Response-Timeout header */
  @AvailableSince(ZVersion.ZOS_2_1) val responseTimeout: Int? = null,

  /** X-IBM-Session-Limit-Wait header */
  @AvailableOnly(ZVersion.ZOS_2_4) val sessionLimitWait: Int? = null,

  /** X-IBM-Target-System header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystem: String? = null,

  /** X-IBM-Request-Acctnum header */
  @AvailableSince(ZVersion.ZOS_2_5) val requestAcctnum: String? = null,

  /** X-IBM-Request-Proc header */
  @AvailableSince(ZVersion.ZOS_2_5) val requestProc: String? = null,

  /** X-IBM-Request-Region header */
  @AvailableSince(ZVersion.ZOS_2_5) val requestRegion: String? = null,

  /** X-IBM-Max-Items header */
  @AvailableSince(ZVersion.ZOS_2_1) val maxItems: Int? = null,

  /** X-IBM-Attributes header */
  // TODO: in impl module - BASE should not be a default, but the preferred somewhere
  @AvailableSince(ZVersion.ZOS_2_1) val attributes: XIBMAttributes? = null,

  /** X-IBM-Migrated-Recall header */
  @AvailableSince(ZVersion.ZOS_2_2) val migratedRecall: XIBMMigratedRecall? = null,

  /** X-IBM-Target-System-User header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemUser: String? = null,

  /** X-IBM-Target-System-Password header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemPassword: String? = null,

  /** start query param */
  @AvailableSince(ZVersion.ZOS_2_1) val start: String? = null,

  /** pattern query param */
  @AvailableSince(ZVersion.ZOS_2_1) val pattern: String? = null
) : ListDatasetMembersRequest(datasetName = dslevel)