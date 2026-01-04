/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.core.Request

/**
 * A basic representation of an HTTP request object
 * @property connection the [HttpConnection] instance that holds all the necessary auth info to perform the request
 * @property method the [HttpMethod] to execute the request as
 * @property path the URL base path of the request
 * @property headers the [HttpRequestHeaders] object to provide with the request
 * @property parameters the URL parameters to provide in the request line
 * @property body the request body to put a payload in
 */
interface HttpRequest : Request {
  val connection: HttpConnection
  val method: HttpMethod
  val path: String
  val headers: HttpRequestHeaders
  val parameters: Map<String, String?>
  val body: Any?

  /**
   * Append HTTP query parameters to request
   * @param request the [HttpRequest] instance with all the necessary query parameters
   * @return [Unit]
   * */
  private fun HttpRequestBuilder.appendParams(request: HttpRequest) {
    request.parameters.forEach { (name, value) -> if (value != null) parameter(name, value) }
  }

  /**
   * Append HTTP headers to request
   * @param request the [HttpRequest] instance with all the necessary headers
   * @return [Unit]
   * */
  private fun HttpRequestBuilder.appendHeaders(request: HttpRequest) {
    headers {
      append("Authorization", request.connection.getAuthParam())
      request.headers.getHeadersMap().forEach { (name, value) -> if (value != null) append(name, value) }
    }
  }

  /**
   * Perform an HTTP request and return the [io.ktor.client.statement.HttpResponse] object.
   * It is considered as a plain request to execute, because it does not include working with streams
   * @param client the HTTP client to perform the request with
   * @return an [io.ktor.client.statement.HttpResponse] object with the request result
   */
  suspend fun performHttpPlainRequest(client: HttpClient): io.ktor.client.statement.HttpResponse {
    val fullUrl = "${connection.scheme}://${connection.host}:${connection.port}${path}"
    return client.request(fullUrl) {
      method = this@HttpRequest.method
      appendHeaders(this@HttpRequest)
      appendParams(this@HttpRequest)
      this@HttpRequest.body?.let { nonNullBody ->
        setBody(nonNullBody)
      }
    }
  }

  /**
   * Produce an [HttpResponse] object from the client's [io.ktor.client.statement.HttpResponse] object
   * @param clientResponse the object to produce the response object from
   */
  override suspend fun produceResponseObject(clientResponse: Any): HttpResponse

  /** Execute the HTTP request with the provided [HttpClient] and return the [HttpResponse] */
  suspend fun execHttpRequest(client: HttpClient): HttpResponse {
    val clientResponse = performHttpPlainRequest(client)
    return produceResponseObject(clientResponse)
  }

  override suspend fun execRequest(payload: Any): HttpResponse {
    return execHttpRequest(payload as HttpClient)
  }
}
