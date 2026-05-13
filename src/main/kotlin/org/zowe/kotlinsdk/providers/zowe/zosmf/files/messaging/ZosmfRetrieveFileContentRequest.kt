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

import io.ktor.client.HttpClient
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.ChanneledRequest
import org.zowe.kotlinsdk.core.DataType
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.core.files.api.messaging.RetrieveFileContentRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMDataType
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfStatus

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.2.0?topic=interface-retrieve-contents-zos-unix-file">Retrieve the contents of a z/OS UNIX file</a>
 * @property filePath filepath-name path param
 * @property search search optional query param
 * @property research research optional query param
 * @property insensitive insensitive optional query param
 * @property maxReturnSize maxreturnsize optional query param
 */
class ZosmfRetrieveFileContentRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val filePath: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) val search: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val research: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val insensitive: Boolean? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val maxReturnSize: Int? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val channelSize: Int = ChanneledRequest.DEFAULT_CHANNEL_SIZE,
  override val headers: ZosmfRetrieveFileContentRequestHeaders = ZosmfRetrieveFileContentRequestHeaders(),
) : ZosmfHttpRequest, RetrieveFileContentRequest {

  override val responseClass = ZosmfRetrieveFileContentResponse::class.java

  override val method = HttpMethod.Get

  override val path = "/zosmf/restfiles/fs/$filePath"

  override val parameters = mutableMapOf(
    "search" to search,
    "research" to research,
    "insensitive" to insensitive?.toString(),
    "maxreturnsize" to maxReturnSize?.toString()
  )

  override val body = null

  override val dataType = when (headers.xIBMDataType.type) {
    XIBMDataType.Type.TEXT -> DataType.TEXT
    XIBMDataType.Type.BINARY -> DataType.BINARY
    else -> DataType.ERROR
  }

  /**
   * Execute the request with the provided [client] and produce a [ZosmfRetrieveFileContentResponse],
   * basing on the [dataType] requested and received.
   * For BINARY data type, forms a response object with the open channel to fetch data from.
   * For TEXT data type, returns the fetched text as is.
   * Handles any error situations that could occur during the response object creation
   */
  override suspend fun execHttpRequest(client: HttpClient): ZosmfRetrieveFileContentResponse {
    return when (dataType) {
      DataType.BINARY -> {
        val clientResponse = performHttpPlainRequest(client)
        if (clientResponse.status.isSuccess()) {
          val contentLength = clientResponse.contentLength()
          if (contentLength != null) {
            ZosmfRetrieveFileContentResponse(
              ZosmfStatus(clientResponse.status),
              dataType,
              fetchChannel = clientResponse.bodyAsChannel(),
              contentLength = contentLength,
              channelSize = channelSize
            )
          } else {
            val httpStatus = HttpStatusCode(
              500,
              "'Content-Length' header is not returned for a binary read request"
            )
            ZosmfRetrieveFileContentResponse(ZosmfStatus(httpStatus), dataType)
          }
        } else {
          val errorReport = produceErrorReport(clientResponse)
          ZosmfRetrieveFileContentResponse(
            ZosmfStatus(clientResponse.status, errorReport),
            dataType
          )
        }
      }
      DataType.TEXT -> {
        val clientResponse = performHttpPlainRequest(client)
        if (clientResponse.status.isSuccess()) {
          ZosmfRetrieveFileContentResponse(
            ZosmfStatus(clientResponse.status),
            dataType,
            clientResponse.bodyAsText()
          )
        } else {
          val errorReport = produceErrorReport(clientResponse)
          ZosmfRetrieveFileContentResponse(
            ZosmfStatus(clientResponse.status, errorReport),
            dataType
          )
        }
      }
      else -> {
        val httpStatus = HttpStatusCode(
          400,
          "Incorrect data type: $dataType. Check if 'X-IBM-DataType' header is correct. Current value: ${headers.xIBMDataType.type}"
        )
        ZosmfRetrieveFileContentResponse(ZosmfStatus(httpStatus), dataType)
      }
    }
  }
}
