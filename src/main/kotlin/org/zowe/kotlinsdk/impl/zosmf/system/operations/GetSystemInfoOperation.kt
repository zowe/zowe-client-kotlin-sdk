//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.system.operations

import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.Operation
import org.zowe.kotlinsdk.impl.zosmf.system.ZosmfSystemAPICalls
import org.zowe.kotlinsdk.impl.zosmf.system.data.ZosmfGetSystemInfoRequest
import org.zowe.kotlinsdk.impl.zosmf.system.data.ZosmfGetSystemInfoResponse
import retrofit2.Call

// TODO: doc
internal class GetSystemInfoOperation (
  private val params: ZosmfGetSystemInfoRequest,
  private val connection: Connection,
  httpClient: OkHttpClient
) : Operation<ZosmfSystemAPICalls, ZosmfGetSystemInfoResponse>(connection, httpClient, ZosmfSystemAPICalls::class.java) {
  override fun buildCall(runnerAPI: ZosmfSystemAPICalls): Call<ZosmfGetSystemInfoResponse> {
    return runnerAPI.getSystemInfo()
  }
}
