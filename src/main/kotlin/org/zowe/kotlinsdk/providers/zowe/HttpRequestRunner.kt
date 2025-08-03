/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.providers.zowe

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import kotlinx.coroutines.runBlocking
import org.zowe.kotlinsdk.core.Request
import org.zowe.kotlinsdk.core.RequestCanceller
import org.zowe.kotlinsdk.core.RequestRunner
import org.zowe.kotlinsdk.core.Response
import org.zowe.kotlinsdk.core.SupportedProtocol
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.coroutines.coroutineContext

/** The HTTP request generic processing mechanism. Works through [HttpClient] */
class HttpRequestRunner(
  private val client: HttpClient,
  requestCanceller: RequestCanceller? = null
) : RequestRunner(SupportedProtocol.HTTP, requestCanceller) {
  /**
   * Run HTTP request with provided HTTP request instance.
   * Includes mechanism of disallowing the request if it is cancelled by user.
   * Checks if the connection is open before the request
   * @param httpRequest the HTTP request delegate instance, will run the request and produce the respective response
   * @return the respective [Response] after the request is run and the HTTP client response is handled
   */
   suspend fun runHttpRequest(httpRequest: HttpRequest): Response {
    allowRequestCancellation(coroutineContext)
    val connection = httpRequest.connection
    connection.checkConnection()
    val fullUrl = "${connection.scheme}://${connection.host}:${connection.port}${httpRequest.path}"
    val clientResponse = client.request(fullUrl) {
      method = httpRequest.method
      appendHeaders(httpRequest)
      appendParams(httpRequest)
      if (httpRequest.body != null) {
        setBody(httpRequest.body)
      }
    }
    disallowRequestCancellation()
    return HttpResponseProducer(httpRequest,  clientResponse).produceResponse()
  }

  /**
   * Run an HTTP request as a suspendable coroutine with the provided [HttpRequest] parameters
   * @param params the [Request] generic instance with all the necessary parameters to run the request
   * @return [Response] instance after the request is handled
   */
  override fun runRequest(params: Request): Response {
    return runBlocking {
      runHttpRequest(params as? HttpRequest ?: throw Exception("Invalid params provided"))
    }
  }

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
      request.headers.forEach { (name, value) -> if (value != null) append(name, value) }
    }
  }
}