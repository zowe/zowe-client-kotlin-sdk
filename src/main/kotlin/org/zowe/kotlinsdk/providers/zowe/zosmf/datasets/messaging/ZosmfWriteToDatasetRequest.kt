/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.WriteToDatasetRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.2.0?topic=interface-write-data-zos-data-set-member">Write data to a z/OS data set or member</a>
 * @property dsName dataset-name path param
 * @property content content to write to the dataset
 * @property contentType the content type to write (TEXT by default)
 * @property volser volser path param
 * @property memberName member-name path param
 */
class ZosmfWriteToDatasetRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val content: ByteArray,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val contentType: WriteToDatasetRequest.ContentType =
    WriteToDatasetRequest.ContentType.TEXT,
  @property:AvailableSince(ZVersion.ZOS_2_1) val volser: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val memberName: String? = null,
  override val headers: ZosmfWriteToDatasetRequestHeaders = ZosmfWriteToDatasetRequestHeaders(contentType),
) : ZosmfHttpRequest, WriteToDatasetRequest {
  override val responseClass = ZosmfWriteToDatasetResponse::class.java

  private val dsAndMemberPath = if (memberName?.isNotEmpty() == true) "$dsName($memberName)" else dsName
  private val fullDsPath = if (volser?.isNotEmpty() == true) "-$volser/$dsAndMemberPath" else dsAndMemberPath

  override val method = HttpMethod.Put
  override val path = "/zosmf/restfiles/ds/$fullDsPath"
  override val parameters = emptyMap<String, String>()
  override val body = content

  override suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfWriteToDatasetResponse {
    return ZosmfWriteToDatasetResponse(ZosmfWriteToDatasetStatus(clientResponse.status, errorReport))
  }
}
