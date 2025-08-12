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

package org.zowe.kotlinsdk.providers.zowe.zosmf

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

/**
 * HTTP mock response dispatcher implementation.
 * Provides the way to dispatch HTTP requests and to provide the correct request handlers
 */
class HttpMockResponseDispatcher : Dispatcher() {
  private data class HttpMockResponseResolver(
    val endpointName: String,
    val resolver: (RecordedRequest) -> Boolean,
    val handler: (RecordedRequest) -> MockResponse
  )

  private var responseResolvers = mutableListOf<HttpMockResponseResolver>()

  /**
   * Inject request resolver
   * @param name the name of the resolver
   * @param resolver the resolver function.
   *                 Receives the recorded request object to find the resolver for,
   *                 returns true or false (true - the handler of the resolver is returned)
   * @param handler the handler of the request resolver to return if the resolver returns true
   */
  fun injectResolver(
    name: String,
    resolver: (RecordedRequest) -> Boolean,
    handler: (RecordedRequest) -> MockResponse
  ) {
    responseResolvers.add(HttpMockResponseResolver(name, resolver, handler))
  }

  /**
   * Remove request resolver by the name
   * @param name the name of the resolver to remove
   */
  fun removeResolver(name: String) {
    responseResolvers.removeAll { it.endpointName == name }
  }

  /** Remove all resolvers from the dispatcher */
  fun clearResolvers() {
    responseResolvers.clear()
  }

  /**
   * Dispatch the HTTP request. Will return the handled result for the command, triggering the respective resolver
   * @param request the HTTP recorded request to resolve and handle
   * @return the mock response for the request
   */
  override fun dispatch(request: RecordedRequest): MockResponse {
    println("HTTP request received: $request")

    val foundResolver = responseResolvers.find {
      it.resolver(request)
    }

    return foundResolver
      ?.handler
      ?.let { it(request) }
      ?: MockResponse()
        .setBody("Response is not implemented")
        .setResponseCode(404)
        .addHeader("Content-Type", "application/json")
  }
}
