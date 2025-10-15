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

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.core.Request

/**
 * A basic representation of an HTTP request object
 * @property connection the [HttpConnection] instance that holds all the necessary auth info to perform the request
 * @property method the [HttpMethod] to execute the request as
 * @property path the URL base path of the request
 * @property headers the headers map to provide with the request
 * @property parameters the URL parameters to provide in the request line
 * @property body the request body to put a payload in
 */
interface HttpRequest : Request {
  val connection: HttpConnection
  val method: HttpMethod
  val path: String
  val headers: Map<String, String?>
  val parameters: Map<String, String?>
  val body: Any?

  /**
   * HTTP response handler to provide a custom implementation of the request result processing
   * @param clientResponse the actual client response to process in the custom handler
   * @return an [org.zowe.kotlinsdk.providers.zowe.HttpResponse] compatible object with the appropriate response handling result
   */
  suspend fun produceHttpResponse(clientResponse: HttpResponse): org.zowe.kotlinsdk.providers.zowe.HttpResponse? = null
}
