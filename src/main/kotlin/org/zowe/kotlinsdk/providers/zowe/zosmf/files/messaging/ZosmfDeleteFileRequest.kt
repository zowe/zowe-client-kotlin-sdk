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
import org.zowe.kotlinsdk.core.files.api.messaging.DeleteFileRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.produceUssPathPart
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-delete-unix-file-directory">Delete a UNIX file or directory</a>
 * @property filePath the file-pathname path param
 */
class ZosmfDeleteFileRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val filePath: String,
  override val headers: ZosmfDeleteFileRequestHeaders = ZosmfDeleteFileRequestHeaders()
) : ZosmfHttpRequest, DeleteFileRequest {
  override val responseClass = ZosmfDeleteFileResponse::class.java

  override val method = HttpMethod.Delete
  override val path = "/zosmf/restfiles/fs/${produceUssPathPart(filePath)}"
  override val parameters = emptyMap<String, String>()
  override val body = null

  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfHttpResponse {
    return ZosmfDeleteFileResponse(ZosmfDeleteFileStatus(clientResponse.status, errorReport))
  }
}
