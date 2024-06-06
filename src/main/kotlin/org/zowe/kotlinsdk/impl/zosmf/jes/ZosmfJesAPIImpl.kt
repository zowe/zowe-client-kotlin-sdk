//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.jes

import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.core.jes.JesAPI
import org.zowe.kotlinsdk.core.jes.data.GetJobRequest
import org.zowe.kotlinsdk.core.jes.data.GetJobResponse
import org.zowe.kotlinsdk.impl.zosmf.CallCustomizer
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.jes.data.ZosmfGetJobRequest
import org.zowe.kotlinsdk.impl.zosmf.jes.operations.GetJobByCorrelatorOperation
import org.zowe.kotlinsdk.impl.zosmf.jes.operations.GetJobByNameAndIdOperation

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/3.1.0?topic=services-zos-jobs-rest-interface
 */
class ZosmfJesAPIImpl(
  val connection: Connection,
  val httpClient: OkHttpClient,
  private val callCustomizer: CallCustomizer? = null
) : JesAPI {
  // TODO: doc
  override fun getJob(params: GetJobRequest): GetJobResponse {
    val zosmfParams = params as ZosmfGetJobRequest
    val hasNameAndId = zosmfParams.jobId?.isNotEmpty() == true && zosmfParams.jobName?.isNotEmpty() == true
    val hasCorrelator = zosmfParams.jobCorrelator?.isNotEmpty() == true
    return if (hasNameAndId)
      GetJobByNameAndIdOperation(zosmfParams, connection, httpClient).runOperation(callCustomizer)
    else if (hasCorrelator)
      GetJobByCorrelatorOperation(zosmfParams, connection, httpClient).runOperation(callCustomizer)
    else
      throw Exception("For the 'getJob' operation, either 'jobName' and 'jobid' or 'jobCorrelator' should be specified")
  }
}



