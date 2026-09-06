/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * The document the z/OS UNIX file utilities report their textual output by. The utilities, that only perform
 * a change, return an empty body instead, and an unsuccessful request returns an error report
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
 * @property stdout the output lines, produced by the utility
 */
@Serializable
internal data class ZosmfStdoutDocument(val stdout: List<String>? = null)

/** The parser of the [ZosmfStdoutDocument], tolerant to the keys it does not expect */
private val stdoutDocumentJson = Json { ignoreUnknownKeys = true }

/**
 * Extract the output lines, reported by a z/OS UNIX file utility, from the response body.
 * Nothing is extracted from an unsuccessful response: its body holds an error report,
 * which is processed by [ZosmfHttpRequest.produceErrorReport] instead.
 * A body of an unexpected form is not reported either, to not fail the whole request because of it
 * @param clientResponse the HTTP client response to extract the lines from
 * @return the output lines or null if there are none to report
 */
internal suspend fun produceStdoutLines(clientResponse: HttpResponse): List<String>? {
  if (!clientResponse.status.isSuccess()) return null
  val clientResponseBody = clientResponse.bodyAsText()
  if (clientResponseBody.isEmpty()) return null
  return try {
    stdoutDocumentJson.decodeFromString<ZosmfStdoutDocument>(clientResponseBody).stdout
  } catch (_: Exception) {
    null
  }
}
