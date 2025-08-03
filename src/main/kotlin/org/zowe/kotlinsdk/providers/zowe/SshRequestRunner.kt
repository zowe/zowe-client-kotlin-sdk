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

import kotlinx.coroutines.runBlocking
import net.schmizz.sshj.SSHClient
import org.zowe.kotlinsdk.core.Request
import org.zowe.kotlinsdk.core.RequestCanceller
import org.zowe.kotlinsdk.core.RequestRunner
import org.zowe.kotlinsdk.core.Response
import org.zowe.kotlinsdk.core.SupportedProtocol
import kotlin.coroutines.coroutineContext

// TODO: doc
class SshRequestRunner(
  private val client: SSHClient,
  requestCanceller: RequestCanceller? = null
) : RequestRunner(SupportedProtocol.SSH, requestCanceller) {
  // TODO: doc
  suspend fun runSshRequest(sshRequest: SshRequest): SshResponse {
    val connection = sshRequest.connection
    connection.checkConnection()
    allowRequestCancellation(coroutineContext)
    val response = sshRequest.execRequest(client)
    disallowRequestCancellation()
    return response
  }

  // TODO: doc
  override fun runRequest(params: Request): Response {
    return runBlocking {
      runSshRequest(params as? SshRequest ?: throw Exception("Invalid params provided"))
    }
  }
}
