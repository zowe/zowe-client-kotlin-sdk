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
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.HttpRequest

// TODO: doc
internal class RequestRunner(
  private val client: HttpClient,
  private val connection: Connection
) {
  // TODO: doc
  suspend fun runRequest(request: HttpRequest): HttpResponse {
    connection.checkConnection()
    val fullUrl = "${connection.protocol}://${connection.host}:${connection.zosmfPort}${request.path}"
    val response = client
      .request(fullUrl) {
        method = request.method
        headers {
          append("Authorization", connection.getAuthParam())
          request.headers.forEach { (name, value) -> if (value != null) append(name, value) }
        }
        request.parameters.forEach { (name, value) -> if (value != null) parameter(name, value) }
      }
    if (!response.status.isSuccess()) {
      throw Exception("Error during HTTP request execution. ${response.status.value}: ${response.status.description}")
    }
    return response
  }
}
