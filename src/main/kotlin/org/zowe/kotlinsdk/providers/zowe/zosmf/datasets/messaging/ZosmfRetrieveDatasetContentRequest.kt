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

import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import kotlinx.coroutines.runBlocking
import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.RetrieveDatasetContentRequest
import org.zowe.kotlinsdk.providers.zowe.Connection
import org.zowe.kotlinsdk.providers.zowe.HttpRequest
import org.zowe.kotlinsdk.providers.zowe.HttpResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMDataType
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMRecordRange
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMMigratedRecall
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.XIBMObtainENQ

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-data-set-member">Retrieve the contents of a z/OS data set or member</a> */
class ZosmfRetrieveDatasetContentRequest(
  override val connection: Connection,

  /** dataset-name path param */
  @AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,

  /** member-name path param */
  @AvailableSince(ZVersion.ZOS_2_1) val memberName: String? = null,

  /** volser path param */
  @AvailableSince(ZVersion.ZOS_2_1) val volser: String? = null,

  /** search query param */
  @AvailableSince(ZVersion.ZOS_2_1) val search: String? = null,

  /** research query param */
  @AvailableSince(ZVersion.ZOS_2_1) val research: String? = null,

  /** insensitive query param */
  @AvailableSince(ZVersion.ZOS_2_1) val insensitive: Boolean? = null,

  /** maxreturnsize query param */
  @AvailableSince(ZVersion.ZOS_2_1) val maxReturnSize: Int? = null,

  /** If-None-Match standard header */
  @AvailableSince(ZVersion.ZOS_2_1) override val ifNoneMatch: String? = null,

  /** X-IBM-Data-Type custom header */
  @AvailableSince(ZVersion.ZOS_2_1) override val xIBMDataType: XIBMDataType? = null,

  /** X-IBM-Return-Etag custom header */
  @AvailableSince(ZVersion.ZOS_2_1) override val xIBMReturnEtag: Boolean? = null,

  /** X-IBM-Migrated-Recall custom header */
  @AvailableSince(ZVersion.ZOS_2_1) override val xIBMMigratedRecall: XIBMMigratedRecall? = null,

  /** X-IBM-Record-Range custom header */
  @AvailableSince(ZVersion.ZOS_2_1) override val xIBMRecordRange: XIBMRecordRange? = null,

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
) : HttpRequest, RetrieveDatasetContentRequest, ZosmfRetrieveDatasetContentRequestHeaders {

  override val method = HttpMethod.Get

  private val dsAndMemberPath = if (memberName?.isNotEmpty() == true) "$dsName($memberName)" else dsName
  private val fullDsPath = if (volser?.isNotEmpty() == true) "-$volser/$dsAndMemberPath" else dsAndMemberPath

  override val path = "/zosmf/restfiles/ds/$fullDsPath"

  override val headers = getHeadersMap()

  override val parameters = mutableMapOf(
    "search" to search,
    "research" to research,
    "insensitive" to insensitive?.toString(),
    "maxreturnsize" to maxReturnSize?.toString()
  )

  override val body = null

  override fun produceHttpResponse(clientResponse: io.ktor.client.statement.HttpResponse): HttpResponse? {
    if (clientResponse.status.isSuccess()) {
      if (xIBMDataType?.type == XIBMDataType.Type.BINARY) {
        val lengthLong = clientResponse.contentLength()
        if (lengthLong != null) {
          val length = lengthLong.toInt()
          if (length.toLong() == lengthLong) {
            val byteArray = ByteArray(length)
            var offset = 0

            do {
              val currentRead = runBlocking {
                clientResponse.bodyAsChannel().readAvailable(byteArray, offset, byteArray.size)
              }
              offset += currentRead
            } while (currentRead > 0 && offset != length)

            return ZosmfRetrieveDatasetContentResponse(clientResponse.status, null, byteArray)
          } else {
            val responseStatus = HttpStatusCode(500, "The dataset is too large to read")
            return ZosmfRetrieveDatasetContentResponse(responseStatus, null, null)
          }
        } else {
          return ZosmfRetrieveDatasetContentResponse(clientResponse.status, null, null)
        }
      } else {
        val clientResponseBody = runBlocking { clientResponse.bodyAsText() }
        return ZosmfRetrieveDatasetContentResponse(clientResponse.status, clientResponseBody, null)
      }
    } else {
      return ZosmfRetrieveDatasetContentResponse(clientResponse.status, null, null)
    }
  }

}
