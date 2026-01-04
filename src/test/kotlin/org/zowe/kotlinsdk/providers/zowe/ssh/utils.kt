/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.ssh

import java.io.InputStream

/**
 * SSH mock command response
 * @property output the output of the command (error payload if exit code is not 0, empty otherwise)
 * @property error additional error (might be not related to the actual error, more like a session-related error)
 * @property exitCode the final exit code of the SSH command execution
 */
data class SshMockCommandResponse(
  val output: String,
  val error: String = "",
  val exitCode: Int = 0
)

/**
 * SSH mock response dispatcher implementation.
 * Provides the way to dispatch SSH requests and to provide the correct request handlers
 */
class SshMockResponseDispatcher {
  private data class SshMockResponseResolver(
    val endpointName: String,
    val resolver: (cmd: String) -> Boolean,
    val handler: (cmd: String, inputStream: InputStream) -> SshMockCommandResponse
  )

  private var responseResolvers = mutableListOf<SshMockResponseResolver>()

  /**
   * Inject request resolver
   * @param name the name of the resolver
   * @param resolver the resolver function.
   * Receives the command as a string, returns true or false (true - the handler of the resolver is returned)
   * @param handler the handler of the request resolver to return if the resolver returns true
   */
  fun injectResolver(name: String, resolver: (String) -> Boolean, handler: (String, InputStream) -> SshMockCommandResponse) {
    responseResolvers.add(SshMockResponseResolver(name, resolver, handler))
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
   * Dispatch the SSH command. Will return the handled result for the command, triggering the respective resolver
   * @param cmd the SSH command to resolve and handle
   * @param inputStream the SSH channel input stream to handle its data (when needed)
   * @return the SSH command mocked response with prefilled parameters
   */
  fun dispatch(cmd: String, inputStream: InputStream): SshMockCommandResponse {
    val foundResolver = responseResolvers.find {
      it.resolver(cmd)
    }

    return foundResolver
      ?.handler
      ?.let { it(cmd, inputStream) }
      ?: throw Exception("Resolver is not found for command: $cmd")
  }
}
