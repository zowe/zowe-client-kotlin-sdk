//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.jes.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.jes.data.GetJobResponse

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/3.1.0?topic=zjri-json-document-specifications-zos-jobs-rest-interface-requests
 */
class ZosmfGetJobResponse(
  /** jobid response param */
  @SerializedName("jobid")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) private val jesJobId: String,

  /** jobname response param */
  @SerializedName("jobname")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) private val jesJobName: String,

  /** subsystem response param */
  @SerializedName("subsystem")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val subSystem: String? = null,

  /** owner response param */
  @SerializedName("owner")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val owner: String? = null,

  /** status response param */
  @SerializedName("status")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val status: JobStatus? = null,

  /** type response param */
  @SerializedName("type")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val type: JobType? = null,

  /** class response param */
  @SerializedName("class")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val jobClass: String? = null,

  /** retcode response param */
  @SerializedName("retcode")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val returnCode: String? = null,

  /** url response param */
  @SerializedName("url")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val url: String? = null,

  /** files-url response param */
  @SerializedName("files-url")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val filesUrl: String? = null,

  /** job-correlator response param */
  @SerializedName("job-correlator")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val jobCorrelator: String? = null,

  /** phase response param */
  @SerializedName("phase")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val phase: Int? = null,

  /** phase-name response param */
  @SerializedName("phase-name")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val phaseName: String? = null,

  /** step-data response param */
  @SerializedName("step-data")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_2) val stepData: List<JobStepData> = emptyList(),

  /** exec-system response param */
  @SerializedName("exec-system")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_4) val execSystem: String? = null,

  /** exec-member response param */
  @SerializedName("exec-member")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_4) val execMember: String? = null,

  /** exec-submitted response param */
  @SerializedName("exec-submitted")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_4) val execSubmitted: String? = null,

  /** exec-started response param */
  @SerializedName("exec-started")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_4) val execStarted: String? = null,

  /** exec-ended response param */
  @SerializedName("exec-ended")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_4) val execEnded: String? = null,

  /** reason-not-running response param */
  @SerializedName("reason-not-running")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val reasonNotRunning: String? = null,
) : GetJobResponse(jobId = jesJobId, jobName = jesJobName) {
  enum class JobStatus(val value: String) {
    @SerializedName("INPUT")
    INPUT("INPUT"),

    @SerializedName("ACTIVE")
    ACTIVE("ACTIVE"),

    @SerializedName("OUTPUT")
    OUTPUT("OUTPUT");

    override fun toString(): String {
      return value
    }
  }

  enum class JobType(val value: String) {
    @SerializedName("JOB")
    JOB("JOB"),

    @SerializedName("STC")
    STC("STC"),

    @SerializedName("TSU")
    TSU("TSU");

    override fun toString(): String {
      return value
    }
  }
}
