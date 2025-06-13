/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.jes.definitions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.jes.data.JobItem

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=zjri-json-document-specifications-zos-jobs-rest-interface-requests#JSONDocumentSpecifications__JobDocumentContents__title__1">JSON document specifications for z/OS jobs REST interface requests: Job document</a> */
@Serializable
class ZosmfJobItem(
  /** jobid response param */
  @SerialName("jobid")
  @AvailableSince(ZVersion.ZOS_2_1) override val id: String,

  /** jobname response param */
  @SerialName("jobname")
  @AvailableSince(ZVersion.ZOS_2_1) override val name: String,

  /** subsystem response param */
  @SerialName("subsystem")
  @AvailableSince(ZVersion.ZOS_2_1) val subsystem: String? = null,

  /** owner response param */
  @SerialName("owner")
  @AvailableSince(ZVersion.ZOS_2_1) override val owner: String,

  /** status response param */
  @SerialName("status")
  @AvailableSince(ZVersion.ZOS_2_1) val jobStatus: ZosmfJobStatus? = null,

  /** type response param */
  @SerialName("type")
  @AvailableSince(ZVersion.ZOS_2_1) val type: ZosmfJobType? = null,

  /** class response param */
  @SerialName("class")
  @AvailableSince(ZVersion.ZOS_2_1) val jobClass: String? = null,

  /** retcode response param */
  @SerialName("retcode")
  @AvailableSince(ZVersion.ZOS_2_1) val returnCode: String? = null,

  /** url response param */
  @SerialName("url")
  @AvailableSince(ZVersion.ZOS_2_1) val jobUrl: String? = null,

  /** files-url response param */
  @SerialName("files-url")
  @AvailableSince(ZVersion.ZOS_2_1) val jobFilesUrl: String? = null,

  /** job-correlator response param */
  @SerialName("job-correlator")
  @AvailableSince(ZVersion.ZOS_2_1) val jobCorrelator: String? = null,

  /** phase response param */
  @SerialName("phase")
  @AvailableSince(ZVersion.ZOS_2_1) val jobPhase: Int? = null,

  /** phase-name response param */
  @SerialName("phase-name")
  @AvailableSince(ZVersion.ZOS_2_1) val jobPhaseName: String? = null,

  /** step-data response param */
  @SerialName("step-data")
  @AvailableSince(ZVersion.ZOS_2_2) val stepData: List<ZosmfJobStepData> = emptyList(),

  /** exec-system response param */
  @SerialName("exec-system")
  @AvailableSince(ZVersion.ZOS_2_4) val execSystem: String? = null,

  /** exec-member response param */
  @SerialName("exec-member")
  @AvailableSince(ZVersion.ZOS_2_4) val execMember: String? = null,

  /** exec-submitted response param */
  @SerialName("exec-submitted")
  @AvailableSince(ZVersion.ZOS_2_4) val execSubmitted: String? = null,

  /** exec-started response param */
  @SerialName("exec-started")
  @AvailableSince(ZVersion.ZOS_2_4) val execStarted: String? = null,

  /** exec-ended response param */
  @SerialName("exec-ended")
  @AvailableSince(ZVersion.ZOS_2_4) val execEnded: String? = null,

  /** reason-not-running response param */
  @SerialName("reason-not-running")
  @AvailableSince(ZVersion.ZOS_2_1) val reasonNotRunning: String? = null,
) : JobItem {
  @Serializable
  enum class ZosmfJobStatus {
    @SerialName("INPUT") INPUT,
    @SerialName("ACTIVE") ACTIVE,
    @SerialName("OUTPUT") OUTPUT
  }

  @Serializable
  enum class ZosmfJobType {
    @SerialName("JOB") JOB,
    @SerialName("STC") STC,
    @SerialName("TSU") TSU
  }
}
