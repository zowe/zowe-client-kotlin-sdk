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
import org.zowe.kotlinsdk.core.Request
import org.zowe.kotlinsdk.core.RequestRunner
import org.zowe.kotlinsdk.core.SupportedProtocol

/** The HTTP request generic processing mechanism. Works through [HttpClient] */
class HttpRequestRunner(
  private val client: HttpClient
) : RequestRunner(SupportedProtocol.HTTP) {
  /**
   * Run an HTTP request with provided HTTP request instance.
   * Checks if the connection is open before the request
   * @param params the HTTP request parameters
   * @return the respective [HttpResponse] after the request is run and the HTTP client response is handled
   */
  override suspend fun runRequest(params: Request): HttpResponse {
    params as? HttpRequest ?: throw Exception("Invalid params provided")
    val connection = params.connection
    connection.checkConnection()
    return params.execRequest(client)
  }
}