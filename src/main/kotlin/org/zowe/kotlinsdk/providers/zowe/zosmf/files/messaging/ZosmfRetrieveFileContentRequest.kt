///*
// * This program and the accompanying materials are made available under the terms of the
// * Eclipse Public License v2.0 which accompanies this distribution, and is available at
// * https://www.eclipse.org/legal/epl-v20.html
// *
// * SPDX-License-Identifier: EPL-2.0
// *
// * Copyright Contributors to the Zowe Project.
// */
//
//package org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging
//
//import io.ktor.client.HttpClient
//import io.ktor.client.statement.bodyAsChannel
//import io.ktor.client.statement.bodyAsText
//import io.ktor.http.HttpMethod
//import io.ktor.http.HttpStatusCode
//import io.ktor.http.contentLength
//import io.ktor.http.isSuccess
//import kotlinx.serialization.json.Json
//import kotlinx.serialization.serializer
//import org.zowe.kotlinsdk.annotations.AvailableOnly
//import org.zowe.kotlinsdk.annotations.AvailableSince
//import org.zowe.kotlinsdk.annotations.ZVersion
//import org.zowe.kotlinsdk.core.files.api.messaging.RetrieveFileContentRequest
//import org.zowe.kotlinsdk.core.connectivity.HttpConnection
//import org.zowe.kotlinsdk.providers.zowe.HttpRequest
//import org.zowe.kotlinsdk.providers.zowe.HttpResponse
//import org.zowe.kotlinsdk.providers.zowe.HttpStatus
//import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMDataType
//import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMRecordRange
//import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
//
///** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-unix-file">Retrieve the contents of a z/OS UNIX file</a> */
//class ZosmfRetrieveFileContentRequest(
//  override val connection: HttpConnection,
//
//  /** filepath-name path param */
//  @property:AvailableSince(ZVersion.ZOS_2_1) override val filePath: String,
//
//  /** search optional query param */
//  @property:AvailableSince(ZVersion.ZOS_2_1) val search: String? = null,
//
//  /** research optional query param */
//  @property:AvailableSince(ZVersion.ZOS_2_1) val research: String? = null,
//
//  /** insensitive optional query param */
//  @property:AvailableSince(ZVersion.ZOS_2_1) val insensitive: Boolean? = null,
//
//  /** maxreturnsize optional query param */
//  @property:AvailableSince(ZVersion.ZOS_2_1) val maxReturnSize: Int? = null,
//
//  /** If-None-Match standard header*/
//  @property:AvailableSince(ZVersion.ZOS_2_1) override val ifNoneMatch: String? = null,
//
//  /** Range standard header*/
//  @property:AvailableSince(ZVersion.ZOS_2_1) override val range: Int? = null,
//
//  /** X-IBM-Record-Range standard header*/
//  @property:AvailableSince(ZVersion.ZOS_2_1) override val xIBMRecordRange: XIBMRecordRange? = null,
//
//  /** X-IBM-Data-Type custom header */
//  @property:AvailableSince(ZVersion.ZOS_2_1) override val xIBMDataType: XIBMDataType? = null,
//
//  /** X-IBM-Target-System default header */
//  @property:AvailableSince(ZVersion.ZOS_2_4) override val targetSystem: String? = null,
//
//  /** X-IBM-Target-System-User custom header */
//  @property:AvailableSince(ZVersion.ZOS_2_4) override val targetSystemUser: String? = null,
//
//  /** X-IBM-Target-System-Password custom header */
//  @property:AvailableSince(ZVersion.ZOS_2_4) override val targetSystemPassword: String? = null,
//
//  /** X-IBM-Async-Threshold default header */
//  @property:AvailableSince(ZVersion.ZOS_2_1) override val asyncThreshold: Int? = null,
//
//  /** X-IBM-Response-Timeout default header */
//  @property:AvailableSince(ZVersion.ZOS_2_1) override val responseTimeout: Int? = null,
//
//  /** X-IBM-Request-Acctnum default header */
//  @property:AvailableSince(ZVersion.ZOS_2_5) override val requestAcctnum: String? = null,
//
//  /** X-IBM-Request-Proc default header */
//  @property:AvailableSince(ZVersion.ZOS_2_5) override val requestProc: String? = null,
//
//  /** X-IBM-Request-Region default header */
//  @property:AvailableSince(ZVersion.ZOS_2_5) override val requestRegion: String? = null,
//): HttpRequest, RetrieveFileContentRequest, ZosmfRetrieveFileContentRequestHeaders {
//
//  override val method = HttpMethod.Get
//
//  override val path = "/zosmf/restfiles/fs/$filePath"
//
//  override val headers = getHeadersMap()
//
//  override val parameters = mutableMapOf(
//    "search" to search,
//    "research" to research,
//    "insensitive" to insensitive?.toString(),
//    "maxreturnsize" to maxReturnSize?.toString()
//  )
//
//  override val body = null
//
//  override val responseClass = ZosmfRetrieveFileContentResponse::class.java
//
//  override suspend fun execRequestAndProduceResponse(payload: HttpClient): HttpResponse {
//    val clientResponse = performHttpPlainRequest(payload)
//    if (clientResponse.status.isSuccess()) {
//      if (xIBMDataType?.type == XIBMDataType.Type.BINARY) {
//        val lengthLong = clientResponse.contentLength()
//        if (lengthLong != null) {
//          val length = lengthLong.toInt()
//          if (length.toLong() == lengthLong) {
//            val byteArray = ByteArray(length)
//            var offset = 0
//
//            do {
//              val currentRead = clientResponse.bodyAsChannel()
//                .readAvailable(byteArray, offset, byteArray.size)
//              offset += currentRead
//            } while (currentRead > 0 && offset != length)
//
//            val status = HttpStatus(clientResponse.status)
//            return ZosmfRetrieveFileContentResponse(status, null, byteArray)
//          } else {
//            val responseStatus = HttpStatusCode(500, "The file is too large to read")
//            val status = HttpStatus(responseStatus)
//            return ZosmfRetrieveFileContentResponse(status, null, null)
//          }
//        } else {
//            val status = HttpStatus(clientResponse.status)
//          return ZosmfRetrieveFileContentResponse(status, null, null)
//        }
//      } else {
//        val clientResponseBody = clientResponse.bodyAsText()
//        val status = HttpStatus(clientResponse.status)
//        return ZosmfRetrieveFileContentResponse(status, clientResponseBody, null)
//      }
//    } else {
//      val errorReportSerializer = serializer<ZosmfErrorReport>()
//      val errorReport = Json.decodeFromString(
//        errorReportSerializer,
//        clientResponse.bodyAsText()
//      )
//      val status = HttpStatus(clientResponse.status, errorReport)
//      return ZosmfRetrieveFileContentResponse(status, null, null)
//    }
//  }
//
//}
