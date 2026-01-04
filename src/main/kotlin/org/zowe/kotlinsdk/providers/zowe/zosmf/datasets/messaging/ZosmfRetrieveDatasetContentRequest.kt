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

import io.ktor.client.HttpClient
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.DataType
import org.zowe.kotlinsdk.core.datasets.api.messaging.RetrieveDatasetContentRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMDataType
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfStatus

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-data-set-member">Retrieve the contents of a z/OS data set or member</a>
 * @property dsName the dataset-name path param
 * @property memberName the member-name path param
 * @property volser the volser path param
 * @property search the search query param
 * @property research the research query param
 * @property insensitive the insensitive query param
 * @property maxReturnSize the maxreturnsize query param
 */
class ZosmfRetrieveDatasetContentRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) val memberName: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val volser: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val search: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val research: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val insensitive: Boolean? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val maxReturnSize: Int? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val channelSize: Int = RetrieveDatasetContentRequest.DEFAULT_CHANNEL_SIZE,
  override val headers: ZosmfRetrieveDatasetContentRequestHeaders = ZosmfRetrieveDatasetContentRequestHeaders()
) : ZosmfHttpRequest, RetrieveDatasetContentRequest {

  override val responseClass = ZosmfRetrieveDatasetContentResponse::class.java

  override val method = HttpMethod.Get

  private val dsAndMemberPath = if (memberName?.isNotEmpty() == true) "$dsName($memberName)" else dsName
  private val fullDsPath = if (volser?.isNotEmpty() == true) "-$volser/$dsAndMemberPath" else dsAndMemberPath

  override val path = "/zosmf/restfiles/ds/$fullDsPath"

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
   * Execute the request with the provided [client] and produce a [ZosmfRetrieveDatasetContentResponse],
   * basing on the [dataType] requested and received.
   * For BINARY data type, forms a response object with the open channel to fetch data from.
   * For TEXT data type, returns the fetched text as is.
   * Handles any error situations that could occur during the response object creation
   */
  override suspend fun execHttpRequest(client: HttpClient): ZosmfRetrieveDatasetContentResponse {
    return when (dataType) {
      DataType.BINARY -> {
        val clientResponse = performHttpPlainRequest(client)
        if (clientResponse.status.isSuccess()) {
          val contentLength = clientResponse.contentLength()
          if (contentLength != null) {
            ZosmfRetrieveDatasetContentResponse(
              ZosmfStatus(clientResponse.status),
              dataType,
              fetchChannel = clientResponse.bodyAsChannel(),
              contentLength = contentLength,
              channelSize = channelSize
            )
          } else {
            val httpStatus = HttpStatusCode(
              500,
              "'Content-Length' header is not returned for binary read request"
            )
            ZosmfRetrieveDatasetContentResponse(ZosmfStatus(httpStatus), dataType)
          }
        } else {
          val errorReport = produceErrorReport(clientResponse)
          ZosmfRetrieveDatasetContentResponse(
            ZosmfStatus(clientResponse.status, errorReport),
            dataType
          )
        }
      }
      DataType.TEXT -> {
        val clientResponse = performHttpPlainRequest(client)
        if (clientResponse.status.isSuccess()) {
          ZosmfRetrieveDatasetContentResponse(
            ZosmfStatus(clientResponse.status),
            dataType,
            clientResponse.bodyAsText()
          )
        } else {
          val errorReport = produceErrorReport(clientResponse)
          ZosmfRetrieveDatasetContentResponse(
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
        ZosmfRetrieveDatasetContentResponse(ZosmfStatus(httpStatus), dataType)
      }
    }
  }

}
