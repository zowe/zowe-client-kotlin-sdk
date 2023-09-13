//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.zosmf.restjobs.data

import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/2.5.0?topic=zjri-json-document-specifications-zos-jobs-rest-interface-requests#JSONDocumentSpecifications__JobDocumentContents__title__1
 */
class JobDocument(
  /** jobid response param */
  @AvailableSince(ZVersion.ZOS_2_1) val jobId: String,

  /** jobname response param */
  @AvailableSince(ZVersion.ZOS_2_1) val jobName: String,

  /** subsystem response param */
  @AvailableSince(ZVersion.ZOS_2_1) val subSystem: String? = null,

  /** owner response param */
  @AvailableSince(ZVersion.ZOS_2_1) val owner: String,

  /** status response param */
  @AvailableSince(ZVersion.ZOS_2_1) val status: JobStatus? = null,

  /** type response param */
  @AvailableSince(ZVersion.ZOS_2_1) val type: JobType? = null,

  /** class response param */
  @AvailableSince(ZVersion.ZOS_2_1) val jobClass: String? = null,

  // TODO: create enum?
  /** retcode response param */
  @AvailableSince(ZVersion.ZOS_2_1) val returnCode: String? = null,

  /** url response param */
  @AvailableSince(ZVersion.ZOS_2_1) val url: String,

  /** files-url response param */
  @AvailableSince(ZVersion.ZOS_2_1) val filesUrl: String,

  /** job-correlator response param */
  @AvailableSince(ZVersion.ZOS_2_1) val jobCorrelator: String? = null,

  /** phase response param */
  @AvailableSince(ZVersion.ZOS_2_1) val phase: Int,

  /** phase-name response param */
  @AvailableSince(ZVersion.ZOS_2_1) val phaseName: String,

  /** step-data response param */
  @AvailableSince(ZVersion.ZOS_2_2) val stepData: List<JobStepDataDocument> = emptyList(),

  /** exec-system response param */
  @AvailableSince(ZVersion.ZOS_2_4) val execSystem: String? = null,

  /** exec-member response param */
  @AvailableSince(ZVersion.ZOS_2_4) val execMember: String? = null,

  /** exec-submitted response param */
  @AvailableSince(ZVersion.ZOS_2_4) val execSubmitted: String? = null,

  /** exec-started response param */
  @AvailableSince(ZVersion.ZOS_2_4) val execStarted: String? = null,

  /** exec-ended response param */
  @AvailableSince(ZVersion.ZOS_2_4) val execEnded: String? = null,

  /** reason-not-running response param */
  @AvailableSince(ZVersion.ZOS_2_1) val reasonNotRunning: String? = null,
) {
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