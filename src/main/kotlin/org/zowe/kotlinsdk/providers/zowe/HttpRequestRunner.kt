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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.zowe.kotlinsdk.core.Request
import org.zowe.kotlinsdk.core.RequestRunner
import org.zowe.kotlinsdk.core.Response
import org.zowe.kotlinsdk.core.SupportedProtocol
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.coroutineContext

// TODO: doc
class HttpRequestRunner(
  private val client: HttpClient,
  private val requestCanceller: RequestCanceller? = null
) : RequestRunner, Cancellable {
  override val protocol: SupportedProtocol = SupportedProtocol.HTTP

  /** The current [CoroutineContext] to cancel the request on a demand. Null if there is no running request */
  private var requestCoroutineContext: CoroutineContext? = null

  /**
   * Allow the current running request to be cancelled from the outside
   * @param coroutineContext the [CoroutineContext] to cancel the coroutine with the running request
   */
  private fun allowRequestCancellation(coroutineContext: CoroutineContext) {
    requestCoroutineContext = coroutineContext
    requestCanceller?.currentRequest = this
  }

  /** Disallow the current running request to be cancelled as the request is already finished */
  private fun disallowRequestCancellation() {
    requestCanceller?.currentRequest = null
  }

  // TODO: doc
  suspend fun runHttpRequest(httpRequest: HttpRequest): Response {
    allowRequestCancellation(coroutineContext)
    val connection = httpRequest.connection
    connection.checkConnection()
    val fullUrl = "${connection.protocol}://${connection.host}:${connection.zosmfPort}${httpRequest.path}"
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

  // TODO: doc
  /**
   * Run an HTTP request as a suspendable coroutine with the provided [HttpRequest] parameters
   * @param params the [HttpRequest] instance with all the necessary parameters to run the request
   * @return [io.ktor.client.statement.HttpResponse] instance in case the request is run successfully, throws exception otherwise
   */
  override fun runRequest(params: Request): Response {
    return runBlocking {
      runHttpRequest(params as? HttpRequest ?: throw Exception("Invalid params provided"))
    }
  }

  /** Cancel the running request as a coroutine (does not cancel the actual request, but the result is ignored) */
  override fun cancel() {
    disallowRequestCancellation()
    requestCoroutineContext?.cancel(CancellationException("Request cancelled"))
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