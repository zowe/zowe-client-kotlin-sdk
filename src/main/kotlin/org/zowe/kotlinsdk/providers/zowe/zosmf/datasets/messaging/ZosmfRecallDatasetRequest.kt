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

import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.runBlocking
import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.RecallDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.Connection
import org.zowe.kotlinsdk.providers.zowe.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMBPXKAutoCvt
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMMigratedRecall

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities">z/OS data set and member utilities: 'hrecall' request</a> */
class ZosmfRecallDatasetRequest(
  override val connection: HttpConnection,

  /** to-dataset-name path param */
  @AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,

  /** member-name path param */
  @AvailableSince(ZVersion.ZOS_2_1) val memberName: String?,

  /** The request body to migrate dataset */
  @AvailableSince(ZVersion.ZOS_2_1) val recallDatasetBody: ZosmfRecallDatasetRequestBody,

  /** charset-name param for Content-Type header */
  @AvailableSince(ZVersion.ZOS_2_1) override val charsetName: String? = "UTF-8",

  /** X-IBM-BPXK-AUTOCVT custom headers */
  @AvailableSince(ZVersion.ZOS_2_1) override val xIBMBPXKAutoCvt: XIBMBPXKAutoCvt?,

  /** X-IBM-Migrated-Recall custom header */
  @AvailableSince(ZVersion.ZOS_2_1) override val xIBMMigratedRecall: XIBMMigratedRecall? = null,

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
  @AvailableSince(ZVersion.ZOS_2_5) override val requestRegion: String? = null,
) : ZosmfDatasetUtilitiesRequest(), RecallDatasetRequest, ZosmfDatasetUtilitiesRequestHeaders {

  override val fullDsPath = dsName

  override val body = recallDatasetBody

  override fun produceHttpResponse(clientResponse: HttpResponse): org.zowe.kotlinsdk.providers.zowe.HttpResponse? {
    return runBlocking {
      ZosmfRecallDatasetResponse(clientResponse.status)
    }
  }

}
