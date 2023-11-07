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
import org.zowe.kotlinsdk.core.datasets.data.RetrieveDatasetContentRequest

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-data-set-member
 */
class ZosmfRetrieveDatasetContentRequest(
  /** dataset-name path param */
  @AvailableSince(ZVersion.ZOS_2_1) private val datasetName: String,

  /** member-name path param */
  @AvailableSince(ZVersion.ZOS_2_1) private val memberName: String? = null,

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

  /** If-None-Match standard header */
  @AvailableSince(ZVersion.ZOS_2_1) val ifNoneMatch: String? = null,

  /** X-IBM-Data-Type custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMDataType: XIBMDataType? = null,

  /** X-IBM-Return-Etag custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMReturnEtag: Boolean? = null,

  /** X-IBM-Migrated-Recall custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMMigratedRecall: XIBMMigratedRecall? = null,

  /** X-IBM-Record-Range custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMRecordRange: XIBMRecordRange? = null,

  /** X-IBM-Obtain-ENQ custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMObtainENQ: XIBMObtainENQ? = null,

  /** X-IBM-Session-Ref custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMSessionRef: String? = null,

  /** X-IBM-Release-ENQ custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMReleaseENQ: Boolean? = null,

  /** X-IBM-Dsname-Encoding custom header */
  @AvailableSince(ZVersion.ZOS_2_5) val xIBMDsNameEncoding: String? = null,

  /** X-IBM-Target-System-User custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemUser: String? = null,

  /** X-IBM-Target-System-Password custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemPassword: String? = null,

  /** volser path param */
  @AvailableSince(ZVersion.ZOS_2_1) val volser: String? = null,

  /** search query param */
  @AvailableSince(ZVersion.ZOS_2_1) val search: String? = null,

  /** research query param */
  @AvailableSince(ZVersion.ZOS_2_1) val research: String? = null,

  /** insensitive query param */
  @AvailableSince(ZVersion.ZOS_2_1) val insensitive: Boolean? = null,

  /** maxreturnsize query param */
  @AvailableSince(ZVersion.ZOS_2_1) val maxReturnSize: Int? = null
) : RetrieveDatasetContentRequest(
  dsName = if (memberName?.isNotEmpty() == true) "$datasetName($memberName)" else datasetName
)
