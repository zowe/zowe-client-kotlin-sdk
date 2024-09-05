/*
 * Copyright (c) 2020-2024 IBA Group.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   IBA Group
 *   Zowe Community
 */

package org.zowe.kotlinsdk.zowe.client.sdk.zostso

import org.zowe.kotlinsdk.TsoResponse
import org.zowe.kotlinsdk.UnsafeOkHttpClient
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import org.zowe.kotlinsdk.zowe.client.sdk.zostso.input.StartTsoParams
import okhttp3.OkHttpClient
import retrofit2.Response

/**
 * Class to handle issue command to TSO
 */
class IssueTso(
  var connection: ZOSConnection,
  var httpClient: OkHttpClient = UnsafeOkHttpClient.unsafeOkHttpClient
) {

  init {
    connection.checkConnection()
  }

  var response: Response<*>? = null

  /**
   * API method to start a TSO address space with provided parameters, issue a command,
   * collect responses until prompt is reached, and terminate the address space.
   *
   * @param accountNumber accounting info for Jobs
   * @param command command text to issue to the TSO address space.
   * @param startParams start tso parameters, see startParams object
   * @return issue tso response, see IssueResponse object
   * @throws Exception error executing command
   */
  fun issueTsoCommand(accountNumber: String, command: String, startParams: StartTsoParams, failOnPrompt: Boolean = false): IssueResponse {
    if (accountNumber.isEmpty()) {
      throw Exception("accountNumber not specified")
    }
    if (command.isEmpty()) {
      throw Exception("command not specified")
    }

    // first stage open tso servlet session to use for our tso command processing
    val startTso = StartTso(connection, httpClient)
    val startResponse = startTso.start(accountNumber, startParams, failOnPrompt)

    if (!startResponse.success) {
      throw Exception("TSO address space failed to start. Error: ${startResponse.failureResponse ?: "Unknown error"}")
    }

    val zosmfTsoResponses = mutableListOf<TsoResponse>()
    zosmfTsoResponses.add(startResponse.tsoResponse ?: throw Exception("no zosmf start tso response"))
    val servletKey = startResponse.servletKey

    // second stage send command to tso servlet session created in first stage and collect all tso responses
    val sendTso = SendTso(connection, httpClient)
    val sendResponse = sendTso.sendDataToTSOCollect(servletKey, command, failOnPrompt)

    zosmfTsoResponses.addAll(sendResponse.tsoResponses)

    // lastly save the command response to our issueResponse reference
    val stopTso = StopTso(connection, httpClient)
    val stopResponse = stopTso.stop(servletKey)

    return IssueResponse(
      success = sendResponse.success,
      startResponse = startResponse,
      zosmfResponses = zosmfTsoResponses,
      commandResponses = sendResponse.commandResponse ?: throw Exception("error getting command response"),
      stopResponse = stopResponse
    )
  }

  /**
   * API method to start a TSO address space, issue a command, collect responses until prompt is reached, and
   * terminate the address space.
   *
   * @param accountNumber accounting info for Jobs
   * @param command command text to issue to the TSO address space.
   * @return issue tso response, see IssueResponse object
   * @throws Exception error executing command
   */
  fun issueTsoCommand(accountNumber: String, command: String, failOnPrompt: Boolean = false): IssueResponse {
    return issueTsoCommand(accountNumber, command, StartTsoParams(), failOnPrompt)
  }

}
