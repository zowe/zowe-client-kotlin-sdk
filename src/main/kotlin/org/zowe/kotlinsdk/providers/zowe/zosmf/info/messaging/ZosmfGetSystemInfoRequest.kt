/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.info.messaging

import io.ktor.http.HttpMethod
import org.zowe.kotlinsdk.core.info.api.messaging.GetSystemInfoRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.HttpRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest

/** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=service-retrieve-zosmf-information">Retrieve z/OSMF information</a> */
class ZosmfGetSystemInfoRequest(override val connection: HttpConnection) : ZosmfHttpRequest, GetSystemInfoRequest {
  override val responseClass = ZosmfGetSystemInfoResponse::class.java

  override val method = HttpMethod.Get

  override val path = "/zosmf/info"

  override val headers: HttpRequestHeaders = object : HttpRequestHeaders {
    override fun getHeadersMap() = mapOf("Content-Type" to "application/json")
  }

  override val parameters = mapOf<String, String>()

  override val body = null

}
