/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBA Group 2020
 */

package org.zowe.kotlinsdk

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import retrofit2.Call
import retrofit2.http.*
import java.lang.IllegalArgumentException

interface JESApi {

  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("/zosmf/restjobs/jobs/{job-name}/{job-id}")
  fun getJob(
    @Header("Authorization") basicCredentials: String,
    @Path("job-name") jobName: String,
    @Path("job-id") jobId: String,
    @AvailableSince(ZVersion.ZOS_2_2)
    @Query("step-data") useStepData: UseStepData = UseStepData.DISABLE,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("exec-data") execData: ExecData? = null
  ): Call<Job>

  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("/zosmf/restjobs/jobs/{job-correlator}")
  fun getJob(
    @Header("Authorization") basicCredentials: String,
    @Path("job-correlator") jobCorrelator: String,
    @Query("step-data") useStepData: UseStepData = UseStepData.DISABLE,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("exec-data") execData: ExecData? = null
  ): Call<Job>

  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("/zosmf/restjobs/jobs")
  fun getFilteredJobs(
    @Header("Authorization") basicCredentials: String,
    @Query("owner") owner: String? = null,
    @Query("prefix") prefix: String? = null,
    @Query("jobid") jobId: String? = null,
    @Query("max-jobs") maxCount: Int? = null,
    @Query("user-correlator") userCorrelator: String? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("exec-data") execData: ExecData? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("status") status: ActiveStatus? = null
  ): Call<List<Job>>

  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("/zosmf/restjobs/jobs/{job-name}/{job-id}/files")
  fun getJobSpoolFiles(
    @Header("Authorization") basicCredentials: String,
    @Path("job-name") jobName: String,
    @Path("job-id") jobId: String
  ): Call<List<SpoolFile>>

  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("/zosmf/restjobs/jobs/{job-correlator}/files")
  fun getJobSpoolFiles(
    @Header("Authorization") basicCredentials: String,
    @Path("job-correlator") jobCorrelator: String,
  ): Call<List<SpoolFile>>

  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("/zosmf/restjobs/jobs/{job-name}/{job-id}/files/{file-id}/records")
  fun getSpoolFileRecords(
    @Header("Authorization") basicCredentials: String,
    @Path("job-name") jobName: String,
    @Path("job-id") jobId: String,
    @Path("file-id") fileId: Int,
    @Query("mode") mode: BinaryMode = BinaryMode.TEXT,
    @Header("X-IBM-Record-Range") range: RecordRange? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("fileEncoding") fileEncoding: String? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("search") searchFor: String? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("research") searchForRegular: String? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("insensitive") isSearchCaseSensitive: Boolean? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("maxreturnsize") maxreturnsize: Int? = null
    ): Call<ByteArray>

  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("/zosmf/restjobs/jobs/{job-correlator}/files/{file-id}/records")
  fun getSpoolFileRecords(
    @Header("Authorization") basicCredentials: String,
    @Path("job-correlator") jobCorrelator: String,
    @Path("file-id") fileId: Int,
    @Query("mode") mode: BinaryMode = BinaryMode.TEXT,
    @Header("X-IBM-Record-Range") range: RecordRange? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("fileEncoding") fileEncoding: String? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("search") searchFor: String? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("research") searchForRegular: String? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("insensitive") isSearchCaseSensitive: Boolean? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("maxreturnsize") maxreturnsize: Int? = null
  ): Call<ByteArray>

  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("/zosmf/restjobs/jobs/{job-name}/{job-id}/files/JCL/records")
  fun getJCLRecords(
    @Header("Authorization") basicCredentials: String,
    @Path("job-name") jobName: String,
    @Path("job-id") jobId: String,
    @Query("mode") mode: BinaryMode = BinaryMode.TEXT,
    @Header("X-IBM-Record-Range") range: RecordRange? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("fileEncoding") fileEncoding: String? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("search") searchFor: String? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("research") searchForRegular: String? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("insensitive") isSearchCaseSensitive: Boolean? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("maxreturnsize") maxreturnsize: Int? = null
  ): Call<ByteArray>

  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("/zosmf/restjobs/jobs/{job-correlator}/files/JCL/records")
  fun getJCLRecords(
    @Header("Authorization") basicCredentials: String,
    @Path("job-correlator") jobCorrelator: String,
    @Query("mode") mode: BinaryMode = BinaryMode.TEXT,
    @Header("X-IBM-Record-Range") range: RecordRange? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("fileEncoding") fileEncoding: String? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("search") searchFor: String? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("research") searchForRegular: String? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("insensitive") isSearchCaseSensitive: Boolean? = null,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Query("maxreturnsize") maxreturnsize: Int? = null
  ): Call<ByteArray>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restjobs/jobs")
  fun submitJobRequest(
    @Header("Authorization") basicCredentials: String,
    @Header("Content-type") contentType: ContentType = ContentType.TEXT_PLAIN,
    @Header("X-IBM-Intrdr-Class") intrdrclass: String? = null,
    @Header("X-IBM-Intrdr-Recfm") recfm: Intrdr_Recfm? = null,
    @Header("X-IBM-Intrdr-Lrecl") lrecl: String? = null,
    @Header("X-IBM-Intrdr-Mode") mode: Intrdr_Mode = Intrdr_Mode.TEXT,
    @Header("X-IBM-User-Correlator") userCorrelator: String? = null,
    @Header("X-IBM-JCL-Symbol-name") symbolName: String? = null,
    @Header("X-IBM-Notification-URL") notificationURL: String? = null,
    @Query("JESB") jesb: String? = null,
    @Body body: String,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Header("X-IBM-Intrdr-FileEncoding") fileEncoding: String? = null
  ): Call<SubmitJobRequest>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restjobs/jobs")
  fun submitJobRequest(
    @Header("Authorization") basicCredentials: String,
    @Header("Content-type") contentType: ContentType = ContentType.APP_JSON,
    @Header("X-IBM-Intrdr-Class") intrdrclass: String? = null,
    @Header("X-IBM-Intrdr-Recfm") recfm: Intrdr_Recfm? = null,
    @Header("X-IBM-Intrdr-Lrecl") lrecl: String? = null,
    @Header("X-IBM-Intrdr-Mode") mode: Intrdr_Mode? = null,
    @Header("X-IBM-User-Correlator") userCorrelator: String? = null,
    @Header("X-IBM-JCL-Symbol-name") symbolName: String? = null,
    @Header("X-IBM-Notification-URL") notificationURL: String? = null,
    @Query("JESB") jesb: String? = null,
    @Body body: SubmitFileNameBody,
    @AvailableSince(ZVersion.ZOS_2_4)
    @Header("X-IBM-Intrdr-FileEncoding") fileEncoding: String? = null
  ): Call<SubmitJobRequest>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restjobs/jobs/{jobname}/{jobid}")
  fun holdJobRequest(
    @Header("Authorization") basicCredentials: String,
    @Path("jobname") jobName: String,
    @Path("jobid") jobId: String,
    @Body body: HoldJobRequestBody
  ): Call<HoldJobRequest>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restjobs/jobs/{job-correlator}")
  fun holdJobRequest(
    @Header("Authorization") basicCredentials: String,
    @Path("job-correlator") jobCorrelator: String,
    @Body body: HoldJobRequestBody
  ): Call<HoldJobRequest>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restjobs/jobs/{jobname}/{jobid}")
  fun releaseJobRequest(
    @Header("Authorization") basicCredentials: String,
    @Path("jobname") jobName: String,
    @Path("jobid") jobId: String,
    @Body body: ReleaseJobRequestBody
  ): Call<ReleaseJobRequest>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restjobs/jobs/{job-correlator}")
  fun releaseJobRequest(
    @Header("Authorization") basicCredentials: String,
    @Path("job-correlator") jobCorrelator: String,
    @Body body: ReleaseJobRequestBody
  ): Call<ReleaseJobRequest>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restjobs/jobs/{jobname}/{jobid}")
  fun cancelJobRequest(
    @Header("Authorization") basicCredentials: String,
    @Path("jobname") jobName: String,
    @Path("jobid") jobId: String,
    @Body body: CancelJobRequestBody
  ): Call<CancelJobRequest>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restjobs/jobs/{job_correlator}")
  fun cancelJobRequest(
    @Header("Authorization") basicCredentials: String,
    @Path("job-correlator") jobCorrelator: String,
    @Body body: CancelJobRequestBody
  ): Call<CancelJobRequest>

  @AvailableSince(ZVersion.ZOS_2_1)
  @DELETE("/zosmf/restjobs/jobs/{jobname}/{jobid}")
  fun cancelJobPurgeOutRequest(
    @Header("Authorization") basicCredentials: String,
    @Header("X-IBM-Job-Modify-Version") version : ProcessMethod = ProcessMethod.SYNCHRONOUS,
    @Path("jobname") jobName: String,
    @Path("jobid") jobId: String
  ): Call<CancelJobPurgeOutRequest>

  @AvailableSince(ZVersion.ZOS_2_1)
  @DELETE("/zosmf/restjobs/jobs/{job_correlator}")
  fun cancelJobPurgeOutRequest(
    @Header("Authorization") basicCredentials: String,
    @Header("X-IBM-Job-Modify-Version") version : ProcessMethod? = null,
    @Path("job-correlator") jobCorrelator: String
  ): Call<CancelJobPurgeOutRequest>
}

enum class UseStepData(val value: String) {
  ENABLE("Y"),
  DISABLE("N");


  override fun toString(): String {
    return value
  }
}

enum class ContentType(val value: String) {
  TEXT_PLAIN("text/plain"),
  APP_STREAM("application/octet_stream"),
  APP_JSON("application/json");

  override fun toString(): String {
    return value
  }
}

enum class Intrdr_Recfm(val value: String) {
  F("F"),
  V("V");

  override fun toString(): String {
    return value
  }
}

enum class Intrdr_Mode(val value: String) {
  TEXT("TEXT"),
  RECORD("RECORD"),
  BINARY("BINARY");

  override fun toString(): String {
    return value
  }
}

enum class ExecData(val value: String) {
  YES("Y"),
  NO("N");


  override fun toString(): String {
    return value
  }
}

enum class ActiveStatus(val value:String) {
  ACTIVE("active"),
  DISABLE("disable");


  override fun toString(): String {
    return value
  }
}

enum class BinaryMode(val value: String) {
  BINARY("binary"),
  RECORD("record"),
  TEXT("text");

  override fun toString(): String {
    return value
  }
}

enum class ProcessMethod(val value: String) {
  ASYNCHRONOUS("1.0"),
  SYNCHRONOUS("2.0");

  override fun toString(): String {
    return value
  }
}

class RecordRange private constructor(var start: Int? = null, var end: Int? = null){
  companion object Factory{
    fun withBounds(start: Int, end: Int): RecordRange {
      if(start<0 || start > 999 || end < 0 || end > 999 || start > end)
        throw IllegalArgumentException("Illegal bounds for record range. Correct boundaries is [0 ... 999].")
      return RecordRange(start, end)
    }
    fun withOffset(start: Int, offset: Int): RecordRange = withBounds(start, start + offset)
  }

  override fun toString(): String {
    return "$start-$end"
  }

}
