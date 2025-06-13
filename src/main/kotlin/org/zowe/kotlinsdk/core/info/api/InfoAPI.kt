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

package org.zowe.kotlinsdk.core.info.api

import org.zowe.kotlinsdk.core.API
import org.zowe.kotlinsdk.core.info.api.messaging.GetSystemInfoRequest
import org.zowe.kotlinsdk.core.info.api.messaging.GetSystemInfoResponse

/** Info API specification to provide functions to work with system information retrieval functions  */
interface InfoAPI : API {
  /**
   * Get the system information
   * @param params [GetSystemInfoRequest] parameters of information fetching
   * @return [GetSystemInfoResponse] instance with the request result
   */
  fun getSystemInfo(params: GetSystemInfoRequest): GetSystemInfoResponse
}