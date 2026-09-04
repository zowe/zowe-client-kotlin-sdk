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
import org.zowe.kotlinsdk.core.files.api.messaging.CreateFileRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.produceUssPathPart
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-create-unix-file-directory">Create a UNIX file or directory</a>
 * @property filePath file-path path param
 * @property body the request body to create a UNIX file or directory
 */
class ZosmfCreateFileRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val filePath: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val body: ZosmfCreateFileRequestBody,
  override val headers: ZosmfCreateFileRequestHeaders = ZosmfCreateFileRequestHeaders()
) : ZosmfHttpRequest, CreateFileRequest {
  override val responseClass = ZosmfCreateFileResponse::class.java
  override val method = HttpMethod.Post
  override val path = "/zosmf/restfiles/fs/${produceUssPathPart(filePath)}"
  override val parameters = emptyMap<String, String>()

  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfHttpResponse {
    return ZosmfCreateFileResponse(ZosmfCreateFileStatus(clientResponse.status, errorReport))
  }
}
