/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.core.jes.api

import org.zowe.kotlinsdk.core.API
import org.zowe.kotlinsdk.core.jes.api.messaging.GetJobRequest
import org.zowe.kotlinsdk.core.jes.api.messaging.GetJobResponse

/** JES API specification to provide functions to work with JES jobs  */
interface JesAPI : API {
  /**
   * Get job info by the provided parameters
   * @param params [GetJobRequest] instance to get the job locator for the request from
   * @return [GetJobResponse] instance with the request result
   */
  suspend fun getJob(params: GetJobRequest): GetJobResponse
}
