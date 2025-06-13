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

import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.GetDatasetInfoRequest
import org.zowe.kotlinsdk.providers.zowe.Connection
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.ReqType
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMAttributes

/**
 * Get dataset info request parameters holder.
 * Stores all necessary info to get a dataset information by the specified parameters
 */
class ZosmfGetDatasetInfoRequest(
  val connection: Connection,

  /** dataset name */
  @AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,

  /** volser query param */
  @AvailableSince(ZVersion.ZOS_2_1) val volumeSerial: String? = null,

  /** X-IBM-Target-System default header */
  @AvailableSince(ZVersion.ZOS_2_4) override val targetSystem: String? = null,

  /** X-IBM-Target-System-User custom header */
  @AvailableSince(ZVersion.ZOS_2_4) override val targetSystemUser: String? = null,

  /** X-IBM-Target-System-Password custom header */
  @AvailableSince(ZVersion.ZOS_2_4) override val targetSystemPassword: String? = null,

  /** X-IBM-Async-Threshold default header */
  @AvailableSince(ZVersion.ZOS_2_1) override val asyncThreshold: Int? = null,

  /** X-IBM-Response-Timeout default header */
  @AvailableSince(ZVersion.ZOS_2_1) override val responseTimeout: Int? = null,

  /** X-IBM-Session-Limit-Wait default header */
  @AvailableOnly(ZVersion.ZOS_2_4) override val sessionLimitWait: Int? = null,

  /** X-IBM-Request-Acctnum default header */
  @AvailableSince(ZVersion.ZOS_2_5) override val requestAcctnum: String? = null,

  /** X-IBM-Request-Proc default header */
  @AvailableSince(ZVersion.ZOS_2_5) override val requestProc: String? = null,

  /** X-IBM-Request-Region default header */
  @AvailableSince(ZVersion.ZOS_2_5) override val requestRegion: String? = null
) : GetDatasetInfoRequest, ZosmfGetDatasetInfoRequestHeaders {
  /** Produce a [ZosmfListDatasetsRequest] parameters for a single dataset */
  fun toListDatasetsRequest(): ZosmfListDatasetsRequest {
    return ZosmfListDatasetsRequest(
      connection = connection,
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