/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.zowe.kotlinsdk.providers.zowe.HttpRequest
import kotlin.reflect.full.starProjectedType

/** HTTP request to z/OSMF REST API interface. Provides the way of working with the z/OSMF REST API specific objects */
interface ZosmfHttpRequest : HttpRequest {
  /** A response class expected to be associated with the request object */
  val responseClass: Class<out ZosmfHttpResponse>

  /**
   * Produce a [ZosmfHttpResponse] object basing on the provided [clientResponse] from an HTTP request
   * and an [errorReport] object, if it is returned by the HTTP client
   */
  suspend fun produceZosmfResponseObject(
    clientResponse: HttpResponse,
    errorReport: ZosmfErrorReport?
  ): ZosmfHttpResponse {
    val responseSerializer = serializer(responseClass.kotlin.starProjectedType)
    val isRequestSucceeded = clientResponse.status.isSuccess()
    val response = Json.decodeFromString(
      responseSerializer,
      if (isRequestSucceeded) clientResponse.bodyAsText() else "{}"
    ) as ZosmfHttpResponse
    return response.applyStatus(ZosmfStatus(clientResponse.status, errorReport))
  }

  /**
   * Produce a [ZosmfErrorReport] from the [clientResponse] if the request did not succeed.
   * @return [ZosmfErrorReport] or null if the request is succeeded or the response body is empty
   */
  suspend fun produceErrorReport(clientResponse: HttpResponse): ZosmfErrorReport? {
    return if (!clientResponse.status.isSuccess()) {
      val clientResponseBody = clientResponse.bodyAsText()
      if (!clientResponseBody.isEmpty()) {
        val errorReportSerializer = serializer<ZosmfErrorReport>()
        try {
          Json.decodeFromString(errorReportSerializer, clientResponseBody)
        } catch(_: Exception) {
          ZosmfErrorReport(0, 0, 0, clientResponseBody)
        }
      } else null
    } else null
  }

  /**
   * Produce a [ZosmfHttpResponse] object basing on the [clientResponse] if a request is succeeded.
   * Generates an error report if the request is returned a non-success response
   */
  override suspend fun produceResponseObject(clientResponse: Any): ZosmfHttpResponse {
    clientResponse as HttpResponse
    val errorReport = produceErrorReport(clientResponse)
    return produceZosmfResponseObject(clientResponse, errorReport)
  }
}
