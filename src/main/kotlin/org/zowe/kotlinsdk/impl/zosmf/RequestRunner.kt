//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * KTor HTTP cancellable request runner
 * @param client [HttpClient] instance to run requests with
 * @param connection the [Connection] instance to run requests with the info from it
 * @param requestCanceller the [RequestCanceller] instance to cancel requests from the outside

 */
internal class RequestRunner(
  private val client: HttpClient,
  private val connection: Connection,
  private val requestCanceller: RequestCanceller? = null
) : Cancellable {
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

  /**
   * Run an HTTP request as a suspendable coroutine with the provided [HttpRequest] parameters
   * @param request the [HttpRequest] instance with all the necessary parameters to run the request
   * @return [HttpResponse] instance in case the request is run successfully, throws exception otherwise
   */
  suspend fun runRequest(request: HttpRequest): HttpResponse {
    allowRequestCancellation(coroutineContext)
    connection.checkConnection()
    val fullUrl = "${connection.protocol}://${connection.host}:${connection.zosmfPort}${request.path}"
    val response = client.request(fullUrl) {
      method = request.method
      headers {
        append("Authorization", connection.getAuthParam())
        request.headers.forEach { (name, value) -> if (value != null) append(name, value) }
      }
      request.parameters.forEach { (name, value) -> if (value != null) parameter(name, value) }
    }
    disallowRequestCancellation()
    if (!response.status.isSuccess()) {
      throw Exception("Error during HTTP request execution. ${response.status.value}: ${response.status.description}")
    }
    return response
  }

  /** Cancel the running request as a coroutine (does not cancel the actual request, but the result is ignored) */
  override fun cancel() {
    disallowRequestCancellation()
    requestCoroutineContext?.cancel(CancellationException("Request cancelled"))
  }
}
