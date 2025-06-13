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

package org.zowe.kotlinsdk.providers.zowe.zosmf.info.messaging

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import kotlinx.coroutines.runBlocking
import org.zowe.kotlinsdk.core.info.api.messaging.GetSystemInfoRequest
import org.zowe.kotlinsdk.providers.zowe.Connection
import org.zowe.kotlinsdk.providers.zowe.HttpRequest

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=service-retrieve-zosmf-information">Retrieve z/OSMF information</a> */
class ZosmfGetSystemInfoRequest(override val connection: Connection) : HttpRequest, GetSystemInfoRequest {

  override val method = HttpMethod.Get

  override val path = "/zosmf/info"

  override val headers = mapOf("Content-Type" to "application/json")

  override val parameters = mapOf<String, String>()

  override val body = null

  override fun produceHttpResponse(clientResponse: HttpResponse): org.zowe.kotlinsdk.providers.zowe.HttpResponse? {
    return runBlocking {
      val response = clientResponse.body<ZosmfGetSystemInfoResponse>()
      response.status = clientResponse.status
      response
    }
  }

}
