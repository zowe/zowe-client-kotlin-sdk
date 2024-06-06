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

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.impl.zosmf.jes.data.ExecData
import org.zowe.kotlinsdk.impl.zosmf.jes.data.UseStepData
import org.zowe.kotlinsdk.impl.zosmf.jes.data.ZosmfGetJobResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

// TODO: doc
internal interface ZosmfJesAPICalls {
  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("/zosmf/restjobs/jobs/{job-name}/{job-id}")
  fun getJobByNameAndId(
    @Header("Authorization") authorizationToken: String,
    @Path("job-name") jobName: String,
    @Path("job-id") jobId: String,
    @Query("step-data") useStepData: UseStepData?,
    @Query("user-correlator") userCorrelator: String?,
    @Query("exec-data") execData: ExecData?
  ): Call<ZosmfGetJobResponse>

  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("/zosmf/restjobs/jobs/{job-correlator}")
  fun getJobByCorrelator(
    @Header("Authorization") authorizationToken: String,
    @Path("job-correlator") jobCorrelator: String,
    @Query("step-data") useStepData: UseStepData?,
    @Query("user-correlator") userCorrelator: String?,
    @Query("exec-data") execData: ExecData?
  ): Call<ZosmfGetJobResponse>
}