/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.workflows

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.encodeURLPathPart
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

/** The base path of the z/OSMF workflow services */
const val ZOSMF_WORKFLOWS_BASE_PATH = "/zosmf/workflow/rest/1.0"

/**
 * JSON decoder for the z/OSMF workflow services responses.
 * z/OSMF adds new properties to the responses over the releases, so unknown properties are skipped,
 * and unknown enum values are coerced to the property defaults
 */
internal val workflowsJson = Json {
  ignoreUnknownKeys = true
  isLenient = true
  coerceInputValues = true
}

/**
 * Produce the workflow key part of the URL path. The key is encoded to be safely used as a path segment
 * @param workflowKey the workflow key to produce the path part for
 * @return the encoded workflow key
 */
fun produceWorkflowKeyPathPart(workflowKey: String): String {
  return workflowKey.encodeURLPathPart()
}

/**
 * Produce the "returnData" query param value
 * @param isFetchSteps whether the steps should be returned
 * @param isFetchVariables whether the variables should be returned
 * @return "steps", "variables", "steps,variables" or null if nothing additional is requested
 */
fun produceReturnDataParam(isFetchSteps: Boolean, isFetchVariables: Boolean): String? {
  return listOfNotNull(
    if (isFetchSteps) "steps" else null,
    if (isFetchVariables) "variables" else null
  )
    .joinToString(",")
    .ifEmpty { null }
}

/**
 * Produce the response body text to decode. Returns an empty JSON object for a failed request or an empty body,
 * so the response object is always produced with its defaults
 * @param clientResponse the HTTP client response to get the body from
 * @return the JSON text to decode
 */
internal suspend fun produceWorkflowsResponseBody(clientResponse: HttpResponse): String {
  return if (clientResponse.status.isSuccess()) clientResponse.bodyAsText().ifBlank { "{}" } else "{}"
}
