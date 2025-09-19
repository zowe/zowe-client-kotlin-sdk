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

package org.zowe.kotlinsdk.providers.zowe

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.runBlocking
import net.schmizz.sshj.SSHClient
import org.zowe.kotlinsdk.core.Request
import org.zowe.kotlinsdk.core.RequestCanceller
import org.zowe.kotlinsdk.core.RequestRunner
import org.zowe.kotlinsdk.core.Response
import org.zowe.kotlinsdk.core.SupportedProtocol

/**
 * SSH request runner. Generalizes the way to process SSH requests and responses
 * @property client the SSHj client to execute requests with
 */
class SshRequestRunner(
  private val client: SSHClient,
  requestCanceller: RequestCanceller? = null
) : RequestRunner(SupportedProtocol.SSH, requestCanceller) {
  /**
   * Run the SSH request. Provides the way to cancel the request by the requester
   * @param sshRequest the SSH request object to execute a specific request with
   * @return [SshResponse] object with the respectively processed data
   */
  suspend fun runSshRequest(sshRequest: SshRequest): SshResponse {
    val connection = sshRequest.connection
    connection.checkConnection()

    allowRequestCancellation(currentCoroutineContext())
    val response = sshRequest.execRequest(client)
    disallowRequestCancellation()
    return response
  }

  override fun runRequest(params: Request): Response {
    return runBlocking {
      runSshRequest(params as? SshRequest ?: throw Exception("Invalid params provided"))
    }
  }
}