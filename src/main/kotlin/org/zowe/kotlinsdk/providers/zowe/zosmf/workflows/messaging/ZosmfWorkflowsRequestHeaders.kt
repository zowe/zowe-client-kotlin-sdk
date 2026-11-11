/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging

import io.ktor.http.ContentType
import org.zowe.kotlinsdk.providers.zowe.HttpRequestHeaders

/**
 * z/OSMF workflow services request headers
 * @property isJsonBody whether the request has a JSON body, so the "Content-Type: application/json" header is added
 */
class ZosmfWorkflowsRequestHeaders(
  val isJsonBody: Boolean = false
) : HttpRequestHeaders {
  override fun getHeadersMap(): Map<String, String?> {
    return if (isJsonBody) mapOf("Content-Type" to ContentType.Application.Json.toString()) else emptyMap()
  }
}
