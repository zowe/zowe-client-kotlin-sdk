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

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

data class Job(

  @SerializedName("jobid")
  @Expose
  val jobId: String,

  @SerializedName("jobname")
  @Expose
  val jobName: String,

  @SerializedName("subsystem")
  @Expose
  val subSystem: String? = null,

  @SerializedName("owner")
  @Expose
  val owner: String,

  @SerializedName("status")
  @Expose
  val status: Status? = null,

  @SerializedName("type")
  @Expose
  val type: JobType,

  @SerializedName("class")
  @Expose
  val jobClass: String? = null,

  @SerializedName("retcode")
  @Expose
  val returnedCode: String? = null,

  @SerializedName("url")
  @Expose
  val url: String,

  @SerializedName("files-url")
  @Expose
  val filesUrl: String,

  @SerializedName("job-correlator")
  @Expose
  val jobCorrelator: String? = null,

  @SerializedName("phase")
  @Expose
  val phase: Int,

  @SerializedName("phase-name")
  @Expose
  val phaseName: String,

  @SerializedName("step-data")
  @Expose
  val steps: List<StepData> = emptyList(),

  @SerializedName("reason-not-running")
  @Expose
  val reasonNotRunning: String? = null,

  @AvailableSince(ZVersion.ZOS_2_4)
  @SerializedName("exec-system")
  @Expose
  val execSystem: String? = null,

  @AvailableSince(ZVersion.ZOS_2_4)
  @SerializedName("exec-member")
  @Expose
  val execMember: String? = null,

  @AvailableSince(ZVersion.ZOS_2_4)
  @SerializedName("exec-submitted")
  @Expose
  val execSubmitted: String? = null,

  @AvailableSince(ZVersion.ZOS_2_4)
  @SerializedName("exec-started")
  @Expose
  val execStarted: String? = null,

  @AvailableSince(ZVersion.ZOS_2_4)
  @SerializedName("exec-ended")
  @Expose
  val execEnded: String? = null
) {

  enum class Status(val value: String) {
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
