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
import org.zowe.kotlinsdk.core.datasets.data.GetDatasetInfoRequest

/**
 * TODO: doc
 */
class ZosmfGetDatasetInfoRequest(
  /** dataset name */
  @AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,

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

  /** X-IBM-Target-System-User custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemUser: String? = null,

  /** X-IBM-Target-System-Password custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemPassword: String? = null,

  /** volser query param */
  @AvailableSince(ZVersion.ZOS_2_1) val volumeSerial: String? = null
) : GetDatasetInfoRequest(dsName) {
  // TODO: doc
  fun toListDatasetsRequest(): ZosmfListDatasetsRequest {
    return ZosmfListDatasetsRequest(
      mask = this.dsName,
      asyncThreshold = this.asyncThreshold,
      responseTimeout = this.responseTimeout,
      sessionLimitWait = this.sessionLimitWait,
      targetSystem = this.targetSystem,
      requestAcctnum = this.requestAcctnum,
      requestProc = this.requestProc,
      requestRegion = this.requestRegion,
      maxItems = 1,
      attributes = XIBMAttributes(ReqType.BASE),
      targetSystemUser = this.targetSystemUser,
      targetSystemPassword = this.targetSystemPassword,
      volumeSerial = this.volumeSerial,
      start = this.dsName
    )
  }
}