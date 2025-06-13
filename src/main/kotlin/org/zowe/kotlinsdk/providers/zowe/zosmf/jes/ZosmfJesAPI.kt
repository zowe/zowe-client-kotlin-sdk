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

package org.zowe.kotlinsdk.providers.zowe.zosmf.jes

import kotlinx.coroutines.runBlocking
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.RequestRunner
import org.zowe.kotlinsdk.core.jes.api.JesAPI
import org.zowe.kotlinsdk.core.jes.api.messaging.GetJobRequest
import org.zowe.kotlinsdk.core.jes.api.messaging.GetJobResponse

// TODO: OptIn mechanism
/**
 * Implementation of Jes API for z/OSMF REST API to work with JES jobs
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=services-zos-jobs-rest-interface">z/OS jobs REST interface</a>
 */
class ZosmfJesAPI(private val requestRunner: RequestRunner) : JesAPI {

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-obtain-status-job">Obtain the status of a job</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-obtain-status-job#izuhpinfo_api_getjobstatus__title__6">Obtain the status of a job: Expected Response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override fun getJob(params: GetJobRequest): GetJobResponse {
    return runBlocking {
      requestRunner.runRequest(params) as GetJobResponse
    }
  }

}
