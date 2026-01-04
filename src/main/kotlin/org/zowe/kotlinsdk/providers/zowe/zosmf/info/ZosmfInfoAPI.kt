/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.info

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.RequestRunner
import org.zowe.kotlinsdk.core.info.api.InfoAPI
import org.zowe.kotlinsdk.core.info.api.messaging.GetSystemInfoRequest
import org.zowe.kotlinsdk.core.info.api.messaging.GetSystemInfoResponse
import org.zowe.kotlinsdk.providers.zowe.ZoweInternalAPI

/**
 * Implementation of Info API for z/OSMF REST API to work with system information retrieval service
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=services-zosmf-information-retrieval-service">z/OSMF information retrieval service</a>
 */
@ZoweInternalAPI
class ZosmfInfoAPI(private val requestRunner: RequestRunner) : InfoAPI {

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=service-retrieve-zosmf-information">Retrieve z/OSMF information</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=service-retrieve-zosmf-information#GETMethodRetrieveZOSMFConfiguration__title__8">Retrieve z/OSMF information: Expected Response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun getSystemInfo(params: GetSystemInfoRequest): GetSystemInfoResponse {
    return requestRunner.runRequest(params) as GetSystemInfoResponse
  }

}
