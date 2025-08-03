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
import io.ktor.http.HttpMethod
import kotlinx.coroutines.runBlocking
import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.WriteToDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.Connection
import org.zowe.kotlinsdk.providers.zowe.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.HttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMDataType
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMMigratedRecall
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMObtainENQ

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-write-data-zos-data-set-member">Write data to a z/OS data set or member</a> */
class ZosmfWriteToDatasetRequest(
  override val connection: HttpConnection,

  /** dataset-name path param */
  @AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,

  /** content to write to the dataset */
  @AvailableSince(ZVersion.ZOS_2_1) override val content: ByteArray,

  /** volser path param */
  @AvailableSince(ZVersion.ZOS_2_1) val volser: String? = null,

  /** member-name path param */
  @AvailableSince(ZVersion.ZOS_2_1) val memberName: String? = null,

  /** content type */
  @AvailableSince(ZVersion.ZOS_2_1) override val contentType: String? = null,

  /** If-Match standard header*/
  @AvailableSince(ZVersion.ZOS_2_1) override val ifMatch: String? = null,

  /** X-IBM-Data-Type custom header */
  @AvailableSince(ZVersion.ZOS_2_1) override val xIBMDataType: XIBMDataType? = null,

  /** X-IBM-Migrated-Recall custom header*/
  @AvailableSince(ZVersion.ZOS_2_1) override val xIBMMigratedRecall: XIBMMigratedRecall? = null,

  /** X-IBM-Obtain-ENQ custom header */
  @AvailableSince(ZVersion.ZOS_2_1) override val xIBMObtainENQ: XIBMObtainENQ? = null,

  /** X-IBM-Session-Ref custom header */
  @AvailableSince(ZVersion.ZOS_2_1) override val xIBMSessionRef: String? = null,

  /** X-IBM-Release-ENQ custom header */
  @AvailableSince(ZVersion.ZOS_2_1) override val xIBMReleaseENQ: Boolean? = null,

  /** X-IBM-Dsname-Encoding custom header */
  @AvailableSince(ZVersion.ZOS_2_5) override val xIBMDsNameEncoding: String? = null,

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
) : HttpRequest, WriteToDatasetRequest, ZosmfWriteToDatasetRequestHeaders {

  override val method = HttpMethod.Put

  private val dsAndMemberPath = if (memberName?.isNotEmpty() == true) "$dsName($memberName)" else dsName
  private val fullDsPath = if (volser?.isNotEmpty() == true) "-$volser/$dsAndMemberPath" else dsAndMemberPath

  override val path = "/zosmf/restfiles/ds/$fullDsPath"

  override val headers = getHeadersMap()

  override val parameters = emptyMap<String, String>()

  override val body = content

  override fun produceHttpResponse(clientResponse: HttpResponse): org.zowe.kotlinsdk.providers.zowe.HttpResponse? {
    return runBlocking {
      val response = ZosmfWriteToDatasetResponse(clientResponse.status)
      // TODO: log warning?
//      if (clientResponse.status != HttpStatusCode.NoContent || clientResponse.status != HttpStatusCode.Created) {
//      }
      response
    }
  }

}
