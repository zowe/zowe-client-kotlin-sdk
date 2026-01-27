/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.jes

import org.zowe.kotlinsdk.core.jes.api.JesAPI
import org.zowe.kotlinsdk.core.jes.api.messaging.GetJobRequest
import org.zowe.kotlinsdk.core.jes.api.messaging.ListJobsRequest
import org.zowe.kotlinsdk.providers.zowe.SshRequestRunner
import org.zowe.kotlinsdk.providers.zowe.ZoweInternalAPI
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.messaging.SshGetJobResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.messaging.SshListJobsResponse

// TODO: doc
@ZoweInternalAPI
class SshJesAPI(private val requestRunner: SshRequestRunner) : JesAPI {
  override suspend fun getJob(params: GetJobRequest): SshGetJobResponse {
    return requestRunner.runRequest(params) as SshGetJobResponse
  }

  override suspend fun listJobs(params: ListJobsRequest): SshListJobsResponse {
    return requestRunner.runRequest(params) as SshListJobsResponse
  }
}