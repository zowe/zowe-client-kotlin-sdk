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
import org.zowe.kotlinsdk.core.Response

/**
 * HTTP custom response handling result producer. Is needed to customize the actual HTTP response from KTor client
 * @param delegate the [HttpRequest] compatible delegate that is going to produce the response
 * @param clientResponse the actual KTor client response to modify
 */
class HttpResponseProducer(
  private val delegate: HttpRequest,
  private val clientResponse: HttpResponse
) : HttpRequest by delegate {
  override suspend fun produceResponse(): Response {
    return produceHttpResponse(clientResponse) ?: super.produceResponse()
  }
}