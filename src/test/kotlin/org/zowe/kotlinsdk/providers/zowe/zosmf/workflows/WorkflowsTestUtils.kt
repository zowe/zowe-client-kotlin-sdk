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

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.StatusType

// Intentionally hardcoded instead of reusing the production constant, so a typo there fails the tests
const val TEST_WORKFLOWS_PATH = "/zosmf/workflow/rest/1.0"

/**
 * Strict request resolver for the workflow services mocks.
 * The method, the exact encoded path and the exact set of query params must match.
 * Any extra or missing query param makes the resolver skip the request
 * @param method the expected HTTP method
 * @param encodedPath the expected encoded URL path
 * @param query the expected query params (decoded values)
 */
fun RecordedRequest.isWorkflowsRequest(
  method: String,
  encodedPath: String,
  query: Map<String, String> = emptyMap()
): Boolean {
  val url = requestUrl ?: return false
  return this.method == method &&
    url.encodedPath == encodedPath &&
    url.queryParameterNames == query.keys &&
    query.all { (name, value) -> url.queryParameter(name) == value }
}

/**
 * Read the workflows mock JSON from "src/test/resources/mock/workflows"
 * @param name the mock file name without the extension
 */
fun readWorkflowsMock(name: String): String {
  return object {}.javaClass.classLoader.getResource("mock/workflows/$name.json")?.readText()
    ?: throw IllegalArgumentException("Mock file mock/workflows/$name.json is not found")
}

/** Produce a JSON mock response with the provided [code] and [body] */
fun workflowsJsonResponse(code: Int, body: String): MockResponse {
  return MockResponse()
    .setResponseCode(code)
    .setBody(body)
    .addHeader("Content-Type", "application/json")
}

/**
 * Produce a z/OSMF workflow services error response.
 * The workflow services return "messageID" and "messageText" properties for the 4nn and 5nn codes
 */
fun workflowsErrorResponse(code: Int, messageId: String, messageText: String): MockResponse {
  return workflowsJsonResponse(code, "{\"messageID\":\"$messageId\",\"messageText\":\"$messageText\"}")
}

/**
 * Check that the status is an error one, produced by the expected mock.
 * The [messageId] check makes sure the request reached the expected resolver
 * and did not fall back to the dispatcher's default 404
 */
fun Status.shouldBeWorkflowsError(code: Int, messageId: String) {
  type shouldBe StatusType.ERROR
  text shouldContain code.toString()
  text shouldContain messageId
}

/** Parse the recorded request body as a JSON object */
fun String?.toJsonObject(): JsonObject {
  return Json.parseToJsonElement(this ?: "").jsonObject
}
