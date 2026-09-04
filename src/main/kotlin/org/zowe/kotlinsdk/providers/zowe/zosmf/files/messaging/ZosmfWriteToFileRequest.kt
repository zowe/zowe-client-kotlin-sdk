/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.files.api.messaging.WriteToFileRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.produceUssPathPart
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-write-data-zos-unix-file">Write data to a z/OS UNIX file</a>
 * @property filePath filepath-name path param
 * @property content the content to write to the file
 */
class ZosmfWriteToFileRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val filePath: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val content: ByteArray,
  override val headers: ZosmfWriteToFileRequestHeaders = ZosmfWriteToFileRequestHeaders()
): ZosmfHttpRequest, WriteToFileRequest {
  override val responseClass = ZosmfWriteToFileResponse::class.java
  override val method = HttpMethod.Put
  override val path = "/zosmf/restfiles/fs/${produceUssPathPart(filePath)}"
  override val parameters = emptyMap<String, String>()
  override val body = content

  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfHttpResponse {
    return ZosmfWriteToFileResponse(ZosmfWriteToFileStatus(clientResponse.status, errorReport))
  }
}
