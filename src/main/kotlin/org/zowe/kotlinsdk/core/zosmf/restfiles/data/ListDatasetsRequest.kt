//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.zosmf.restfiles.data

import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/2.5.0?topic=interface-list-zos-data-sets-system
 */
class ListDatasetsRequest(
  /** X-IBM-Async-Threshold header */
  @AvailableSince(ZVersion.ZOS_2_1) asyncThreshold: Int? = null,

  /** X-IBM-Response-Timeout header */
  @AvailableSince(ZVersion.ZOS_2_1) responseTimeout: Int? = null,

  /** X-IBM-Session-Limit-Wait header */
  @AvailableOnly(ZVersion.ZOS_2_4) sessionLimitWait: Int? = null,

  /** X-IBM-Request-Acctnum header */
  @AvailableSince(ZVersion.ZOS_2_5) requestAcctnum: String? = null,

  /** X-IBM-Request-Proc header */
  @AvailableSince(ZVersion.ZOS_2_5) requestProc: String? = null,

  /** X-IBM-Request-Region header */
  @AvailableSince(ZVersion.ZOS_2_5) requestRegion: String? = null,

  /** X-IBM-Target-System header */
  @AvailableSince(ZVersion.ZOS_2_4) targetSystem: String? = null,

  /** X-IBM-Max-Items header */
  @AvailableSince(ZVersion.ZOS_2_1) val maxItems: Int? = null,

  /** X-IBM-Attributes header */
  // TODO: in impl module - BASE should not be a default, but the preferred somewhere
  @AvailableSince(ZVersion.ZOS_2_1) val attributes: XIBMAttributes? = null,

  /** X-IBM-Target-System-User header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemUser: String? = null,

  /** X-IBM-Target-System-Password header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemPassword: String? = null,

  /** dslevel query param */
  @AvailableSince(ZVersion.ZOS_2_1) val dslevel: String,

  /** volser query param */
  @AvailableSince(ZVersion.ZOS_2_1) val volumeSerial: String? = null,

  /** start query param */
  @AvailableSince(ZVersion.ZOS_2_1) val start: String? = null
) : CommonRequestHeaders(
  asyncThreshold,
  responseTimeout,
  sessionLimitWait,
  requestAcctnum,
  requestProc,
  requestRegion,
  targetSystem
)
