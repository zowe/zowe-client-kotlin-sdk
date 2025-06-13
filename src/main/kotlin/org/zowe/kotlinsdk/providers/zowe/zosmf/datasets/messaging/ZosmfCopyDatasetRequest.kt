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
import org.zowe.kotlinsdk.core.datasets.api.messaging.CopyDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.Connection
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMBPXKAutoCvt
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMMigratedRecall

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities">z/OS data set and member utilities: 'copy' request</a> */
class ZosmfCopyDatasetRequest(
  override val connection: Connection,

  /** to-dataset-name path param */
  @AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,

  /** to-volser path param */
  @AvailableSince(ZVersion.ZOS_2_1) val volser: String?,

  /** member-name path param */
  @AvailableSince(ZVersion.ZOS_2_1) val memberName: String? = null,

  /** The request body to copy dataset */
  @AvailableSince(ZVersion.ZOS_2_1) val copyDatasetBody: ZosmfCopyDatasetRequestBody,

  /** charset-name param for Content-Type header  */
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
) : ZosmfDatasetUtilitiesRequest(), CopyDatasetRequest, ZosmfDatasetUtilitiesRequestHeaders {

  private val dsAndMemberPath = if (memberName?.isNotEmpty() == true) "$dsName($memberName)" else dsName

  override val fullDsPath = if (volser?.isNotEmpty() == true) "-$volser/$dsAndMemberPath" else dsAndMemberPath

  override val body = copyDatasetBody

  override fun produceHttpResponse(clientResponse: HttpResponse): org.zowe.kotlinsdk.providers.zowe.HttpResponse? {
    return runBlocking {
      ZosmfCopyDatasetResponse(clientResponse.status)
    }
  }

}
