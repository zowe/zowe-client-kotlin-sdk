// Copyright © 2020 IBA Group, a.s. All rights reserved. Use of this source code is governed by Eclipse Public License – v 2.0 that can be found at: https://www.eclipse.org/legal/epl-2.0/

package org.zowe.kotlinsdk.zowe.client.sdk.zostso

import org.zowe.kotlinsdk.TsoApi
import org.zowe.kotlinsdk.TsoResponse
import org.zowe.kotlinsdk.UnsafeOkHttpClient
import org.zowe.kotlinsdk.buildApi
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import org.zowe.kotlinsdk.zowe.client.sdk.zostso.input.StopTsoParams
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Response

/**
 * Stop active TSO address space using servlet key
 */
class StopTso(
  var connection: ZOSConnection,
  var httpClient: OkHttpClient = UnsafeOkHttpClient.unsafeOkHttpClient
) {

  init {
    connection.checkConnection()
  }

  var response: Response<*>? = null

  /**
   * Stop TSO address space and populates response with [StartStopResponse]
   *
   * @param servletKey unique servlet entry identifier
   * @return [StartStopResponse] object
   * @throws Exception error on TSO sto command
   */
  fun stop(servletKey: String): StartStopResponse {
    if (servletKey.isEmpty()) {
      throw Exception("servletKey not specified")
    }

    val tsoResponse = stopCommon(StopTsoParams(servletKey))
    val startStopResponse = StartStopResponse(
      tsoResponse = tsoResponse,
      servletKey = tsoResponse.servletKey ?: "",
      success = tsoResponse.servletKey != null
    )
    if (tsoResponse.msgData.isNotEmpty()) {
      val zosmfMsg = tsoResponse.msgData[0]
      val msgText = zosmfMsg.messageText ?: TsoConstants.ZOSMF_UNKNOWN_ERROR
      startStopResponse.failureResponse = msgText
    }

    return startStopResponse
  }

  /**
   * Sends REST call to z/OSMF for stopping active TSO address space
   *
   * @param commandParams [StopTsoParams] command parameters
   * @return [TsoResponse] z/OSMF response object
   * @throws Exception error on TSO sto command
   */
  fun stopCommon(commandParams: StopTsoParams): TsoResponse {
    if (commandParams.servletKey.isNullOrEmpty()) {
      throw Exception("commandParams servletKey not specified")
    }

    val url = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
    val tsoApi = buildApi<TsoApi>(url, httpClient)
    val call = tsoApi.endTso(
      authorizationToken = Credentials.basic(connection.user, connection.password),
      servletKey = commandParams.servletKey
    )
    response = call.execute()
    if (response?.isSuccessful != true) {
      throw Exception("Failed to stop active TSO address space. ${response?.errorBody()?.string()}")
    }

    return response?.body() as TsoResponse? ?: throw Exception("No body returned")
  }

}
