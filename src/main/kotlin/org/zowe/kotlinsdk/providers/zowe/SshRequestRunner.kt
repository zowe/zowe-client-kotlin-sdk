/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe

import net.schmizz.sshj.SSHClient
import org.zowe.kotlinsdk.core.Request
import org.zowe.kotlinsdk.core.RequestRunner
import org.zowe.kotlinsdk.core.Response
import org.zowe.kotlinsdk.core.SupportedProtocol

// TODO: doc
/**
 * SSH request runner. Generalizes the way to process SSH requests and responses
 * @property client the SSHj client to execute requests with
 */
class SshRequestRunner(private val client: SSHClient) : RequestRunner(SupportedProtocol.SSH) {
  /**
   * Run the SSH request. Provides the way to cancel the request by the requester
   * @param sshRequest the SSH request object to execute a specific request with
   * @return [SshResponse] object with the respectively processed data
   */
  override suspend fun runRequest(params: Request): Response {
    params as? SshRequest ?: throw Exception("Invalid params provided")
    val connection = params.connection
    connection.checkConnection()

    val response = params.execRequest(client)
    return response
  }
}