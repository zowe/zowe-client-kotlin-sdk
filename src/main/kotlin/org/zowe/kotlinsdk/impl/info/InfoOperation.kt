//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.info

import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.Operation
import org.zowe.kotlinsdk.core.info.InfoAPI
import retrofit2.Call

// TODO: doc
class InfoOperation(
  connection: Connection, httpClient: OkHttpClient
) : Operation<InfoAPI, InfoResponse>(connection, httpClient, InfoAPI::class.java) {

  // TODO: doc
  override fun buildCall(runnerAPI: InfoAPI): Call<InfoResponse> {
    return runnerAPI.getSystemInfo()
  }
}