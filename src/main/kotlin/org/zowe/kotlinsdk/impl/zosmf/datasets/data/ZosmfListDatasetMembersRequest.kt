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

import io.ktor.http.*
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
  /** X-IBM-Async-Threshold default header */
  @AvailableSince(ZVersion.ZOS_2_1) val asyncThreshold: Int? = null,

  /** X-IBM-Response-Timeout default header */
  @AvailableSince(ZVersion.ZOS_2_1) val responseTimeout: Int? = null,

  /** X-IBM-Session-Limit-Wait default header */
  @AvailableOnly(ZVersion.ZOS_2_4) val sessionLimitWait: Int? = null,

  /** X-IBM-Target-System default header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystem: String? = null,

  /** X-IBM-Request-Acctnum default header */
  @AvailableSince(ZVersion.ZOS_2_5) val requestAcctnum: String? = null,

  /** X-IBM-Request-Proc default header */
  @AvailableSince(ZVersion.ZOS_2_5) val requestProc: String? = null,

  /** X-IBM-Request-Region default header */
  @AvailableSince(ZVersion.ZOS_2_5) val requestRegion: String? = null,

  /** X-IBM-Max-Items custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val maxItems: Int? = null,

  /** X-IBM-Attributes custom header */
  // TODO: in impl module - BASE should not be a default, but the preferred somewhere
  @AvailableSince(ZVersion.ZOS_2_1) val attributes: XIBMAttributes? = null,

  /** X-IBM-Migrated-Recall custom header */
  @AvailableSince(ZVersion.ZOS_2_2) val migratedRecall: XIBMMigratedRecall? = null,

  /** X-IBM-Target-System-User custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemUser: String? = null,

  /** X-IBM-Target-System-Password custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemPassword: String? = null,

  /** dataset-name path param */
  @AvailableSince(ZVersion.ZOS_2_1) override val datasetName: String,

  /** start query param */
  @AvailableSince(ZVersion.ZOS_2_1) val start: String? = null,

  /** pattern query param */
  @AvailableSince(ZVersion.ZOS_2_1) val pattern: String? = null,
) : ListDatasetMembersRequest(datasetName), HttpRequest {
  override val method = HttpMethod.Get

  override val path = "/zosmf/restfiles/ds/$datasetName/member"

  override val headers = mutableMapOf(
    "X-IBM-Async-Threshold" to asyncThreshold?.toString(),
    "X-IBM-Response-Timeout" to responseTimeout?.toString(),
    "X-IBM-Session-Limit-Wait" to sessionLimitWait?.toString(),
    "X-IBM-Target-System" to targetSystem,
    "X-IBM-Request-Acctnum" to requestAcctnum,
    "X-IBM-Request-Proc" to requestProc,
    "X-IBM-Request-Region" to requestRegion,
    "X-IBM-Max-Items" to maxItems?.toString(),
    "X-IBM-Attributes" to attributes?.toString(),
    "X-IBM-Migrated-Recall" to migratedRecall?.toString(),
    "X-IBM-Target-System-User" to targetSystemUser,
    "X-IBM-Target-System-Password" to targetSystemPassword
  )

  override val parameters = mutableMapOf(
    "start" to start,
    "pattern" to pattern
  )
}