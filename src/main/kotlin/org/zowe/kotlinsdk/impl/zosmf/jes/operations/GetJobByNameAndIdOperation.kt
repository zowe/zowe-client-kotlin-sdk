//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.jes.operations

import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.Operation
import org.zowe.kotlinsdk.impl.zosmf.jes.ZosmfJesAPICalls
import org.zowe.kotlinsdk.impl.zosmf.jes.data.ZosmfGetJobRequest
import org.zowe.kotlinsdk.impl.zosmf.jes.data.ZosmfGetJobResponse
import retrofit2.Call

// TODO: doc
internal class GetJobByNameAndIdOperation(
  private val params: ZosmfGetJobRequest,
  private val connection: Connection,
  httpClient: OkHttpClient
) : Operation<ZosmfJesAPICalls, ZosmfGetJobResponse>(connection, httpClient, ZosmfJesAPICalls::class.java) {
  override fun buildCall(runnerAPI: ZosmfJesAPICalls): Call<ZosmfGetJobResponse> {
    return runnerAPI.getJobByNameAndId(
      authorizationToken = connection.basicCredentials,
      jobName = params.jobName ?: throw Exception("'jobName' is not specified"),
      jobId = params.jobId ?: throw Exception("'jobId' is not specified"),
      useStepData = params.stepData,
      execData = params.execData,
      userCorrelator = params.userCorrelator
    )
  }
}
