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
//import io.ktor.client.statement.HttpResponse
//import io.ktor.http.HttpMethod
//import org.zowe.kotlinsdk.annotations.AvailableOnly
//import org.zowe.kotlinsdk.annotations.AvailableSince
//import org.zowe.kotlinsdk.annotations.ZVersion
//import org.zowe.kotlinsdk.core.files.api.messaging.WriteToFileRequest
//import org.zowe.kotlinsdk.core.connectivity.HttpConnection
//import org.zowe.kotlinsdk.providers.zowe.HttpRequest
//import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMDataType
//
///** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-write-data-zos-unix-file">Write data to a z/OS UNIX file</a> */
//class ZosmfWriteToFileRequest(
//  override val connection: HttpConnection,
//
//  /** filepath-name path param */
//  @property:AvailableSince(ZVersion.ZOS_2_1) override val filePath: String,
//
//  /** content to write to the file */
//  @property:AvailableSince(ZVersion.ZOS_2_1) override val content: ByteArray,
//
//  /** content type */
//  @property:AvailableSince(ZVersion.ZOS_2_1) val contentType: String? = null,
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
//
//  /** If-Match standard header*/
//  @property:AvailableSince(ZVersion.ZOS_2_1) override val ifMatch: String? = null,
//
//  /** X-IBM-Data-Type custom header */
//  @property:AvailableSince(ZVersion.ZOS_2_1) override val xIBMDataType: XIBMDataType? = null,
//): HttpRequest, WriteToFileRequest, ZosmfWriteToFileRequestHeaders {
//
//  override val method = HttpMethod.Put
//
//  override val path = "/zosmf/restfiles/fs/$filePath"
//
//  override val headers = getHeadersMap()
//
//  override val parameters = emptyMap<String, String>()
//
//  override val body = content
//
//  override suspend fun produceHttpResponse(clientResponse: HttpResponse): org.zowe.kotlinsdk.providers.zowe.HttpResponse {
//    val response = ZosmfWriteToFileResponse(clientResponse.status)
//      // TODO: log warning?
////      if (clientResponse.status != HttpStatusCode.NoContent || clientResponse.status != HttpStatusCode.Created) {
////      }
//    return response
//  }
//
//}