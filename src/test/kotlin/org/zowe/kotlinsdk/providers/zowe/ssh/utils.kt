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

package org.zowe.kotlinsdk.providers.zowe.ssh

/**
 * SSH mock response dispatcher implementation.
 * Provides the way to dispatch SSH requests and to provide the correct request handlers
 */
class SshMockResponseDispatcher {
  private data class SshMockResponseResolver(
    val endpointName: String,
    val resolver: (cmd: String) -> Boolean,
    val handler: (cmd: String) -> String
  )

  private var responseResolvers = mutableListOf<SshMockResponseResolver>()

  /**
   * Inject request resolver
   * @param name the name of the resolver
   * @param resolver the resolver function.
   *                 Receives the command as a string,
   *                 returns true or false (true - the handler of the resolver is returned)
   * @param handler the handler of the request resolver to return if the resolver returns true
   */
  fun injectResolver(name: String, resolver: (String) -> Boolean, handler: (String) -> String) {
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
   * @return the string response for the command
   */
  fun dispatch(cmd: String): String {
    println("SSH command received: $cmd")

    val foundResolver = responseResolvers.find {
      it.resolver(cmd)
    }

    return foundResolver?.handler?.let { it(cmd) } ?: "OK\n"
  }
}
