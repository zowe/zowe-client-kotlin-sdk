//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.system

import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.core.system.SystemAPI
import org.zowe.kotlinsdk.core.system.data.GetSystemInfoRequest
import org.zowe.kotlinsdk.core.system.data.GetSystemInfoResponse
import org.zowe.kotlinsdk.impl.zosmf.CallCustomizer
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.system.data.ZosmfGetSystemInfoRequest
import org.zowe.kotlinsdk.impl.zosmf.system.operations.GetSystemInfoOperation

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/3.1.0?topic=services-zosmf-information-retrieval-service
 */
class ZosmfSystemAPIImpl(
  val connection: Connection,
  val httpClient: OkHttpClient,
  private val callCustomizer: CallCustomizer? = null
) : SystemAPI {
  // TODO: doc
  override fun getSystemInfo(params: GetSystemInfoRequest): GetSystemInfoResponse {
    return GetSystemInfoOperation(params as ZosmfGetSystemInfoRequest, connection, httpClient).runOperation(callCustomizer)
  }
}
