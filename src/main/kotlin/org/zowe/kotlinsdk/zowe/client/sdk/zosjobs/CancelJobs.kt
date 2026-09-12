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
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.zowe.client.sdk.zosjobs

import org.zowe.kotlinsdk.*
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import org.zowe.kotlinsdk.zowe.client.sdk.zosjobs.input.ModifyJobParams
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Response

/**
 * CancelJobs class to handle Job cancel
 */
class CancelJobs(
  var connection: ZOSConnection,
  var httpClient: OkHttpClient = ZosmfOkHttpClient.getOkHttpClient(connection)
) {

  init {
    connection.checkConnection()
  }

  var response: Response<*>? = null

  /**
   * Cancel a job that resides in a z/OS data set.
   *
   * @param jobName name of job to cancel
   * @param jobId   job id
   * @param version version number
   * @return job document with details about the canceled job
   * @throws Exception error canceling
   */
  fun cancelJob(jobName: String, jobId: String, version: RequestVersion): CancelJobRequest {
    return cancelJobCommon(ModifyJobParams(jobName, jobId, version))
  }

  /**
   * Cancel a job that resides in a z/OS data set.
   *
   * @param job     job document wanting to cancel
   * @param version version number
   * @return job document with details about the canceled job
   * @throws Exception error canceling
   */
  fun cancelJobForJob(job: Job, version: RequestVersion): CancelJobRequest {
    return cancelJobCommon(ModifyJobParams(job.jobName, job.jobId, version))
  }

  /**
   * Cancel a job that resides in a z/OS data set.
   *
   * @param params [ModifyJobParams] cancel job parameters
   * @return job document with details about the canceled job
   * @throws Exception error canceling
   */
  fun cancelJobCommon(params: ModifyJobParams): CancelJobRequest {
    val url = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
    val jesApi = buildApi<JESApi>(url, httpClient)
    val call = jesApi.cancelJobRequest(
      basicCredentials = Credentials.basic(connection.user, connection.password),
      jobName = params.jobName,
      jobId = params.jobId,
      body = CancelJobRequestBody(RequestTypes.CANCEL, params.version!!)
    )
    response = call.execute()
    validateResponse(response)
    return response?.body() as CancelJobRequest? ?: throw Exception("No body returned")
  }
}
