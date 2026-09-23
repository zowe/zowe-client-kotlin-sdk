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
import org.zowe.kotlinsdk.zowe.client.sdk.zosjobs.input.SubmitJclParams
import org.zowe.kotlinsdk.zowe.client.sdk.zosjobs.input.SubmitJobParams
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Response

/**
 * Class to handle submitting of z/OS batch jobs via z/OSMF
 */
class SubmitJobs(
  var connection: ZOSConnection,
  var httpClient: OkHttpClient = ZosmfOkHttpClient.getOkHttpClient(connection)
) {

  init {
    connection.checkConnection()
  }

  var response: Response<*>? = null

  /**
   * Submit a job that resides in a z/OS data set.
   *
   * @param jobDataSet job Dataset to be translated into [SubmitJobParams] object
   * @return job document with details about the submitted job
   * @throws Exception error on submitting
   */
  fun submitJob(jobDataSet: String): SubmitJobRequest {
    return this.submitJobCommon(SubmitJobParams(jobDataSet))
  }

  /**
   * Submit a job that resides in a z/OS data set.
   *
   * @param params [SubmitJobParams] object
   * @return job document with details about the submitted job
   * @throws Exception error on submitting
   */
  fun submitJobCommon(params: SubmitJobParams): SubmitJobRequest {
    val baseUrl = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
    val jesApi = buildApi<JESApi>(baseUrl, httpClient)
    val call = jesApi.submitJobRequest(
      basicCredentials = Credentials.basic(connection.user, connection.password),
      body = SubmitFileNameBody(params.jobDataSet),
      symbolName = params.jclSymbols
    )
    response = call.execute()
    validateResponse(response)
    return response?.body() as SubmitJobRequest? ?: throw Exception("No body returned")
  }

  /**
   * Submit a string of JCL to run
   *
   * @param jcl JCL content that you want to be submitted
   * @param internalReaderRecfm record format of the jcl you want to submit. "F" (fixed) or "V" (variable)
   * @param internalReaderLrecl logical record length of the jcl you want to submit
   * @return job document with details about the submitted job
   * @throws Exception error on submitting
   */
  fun submitJcl(jcl: String, internalReaderRecfm: Intrdr_Recfm, internalReaderLrecl: String): SubmitJobRequest {
    return submitJclCommon(SubmitJclParams(jcl, internalReaderRecfm, internalReaderLrecl))
  }

  /**
   * Submit a JCL string to run
   *
   * @param params [SubmitJclParams] object
   * @return job document with details about the submitted job
   * @throws Exception error on submitting
   */
  fun submitJclCommon(params: SubmitJclParams): SubmitJobRequest {
    val url = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
    val jesApi = buildApi<JESApi>(url, httpClient)
    val call = jesApi.submitJobRequest(
      basicCredentials = Credentials.basic(connection.user, connection.password),
      body = SubmitFileNameBody(params.jcl),
      recfm = params.internalReaderRecfm,
      lrecl = params.internalReaderLrecl,
      symbolName = params.jclSymbols
    )
    response = call.execute()
    validateResponse(response)
    return response?.body() as SubmitJobRequest? ?: throw Exception("No body returned")
  }

}
