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
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.files.api.messaging.GetFileACLRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfStatus
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfUtilitiesRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.produceStdoutLines

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
 * @property body the request body to execute getfacl function on a UNIX System Services file or directory
 */
class ZosmfGetFileACLRequest(
  connection: HttpConnection,
  override val filePath: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val body: ZosmfGetFileACLRequestBody,
  headers: ZosmfUtilitiesRequestHeaders = ZosmfUtilitiesRequestHeaders()
) : ZosmfUssUtilitiesRequest(connection, filePath, headers), GetFileACLRequest {
  override val responseClass = ZosmfGetFileACLResponse::class.java

  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfHttpResponse {
    return ZosmfGetFileACLResponse(
      ZosmfStatus(clientResponse.status, errorReport),
      produceStdoutLines(clientResponse)
    )
  }
}
