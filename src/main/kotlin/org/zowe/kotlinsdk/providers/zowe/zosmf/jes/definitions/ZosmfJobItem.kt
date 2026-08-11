/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.jes.definitions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.jes.data.JobItem

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=zjri-json-document-specifications-zos-jobs-rest-interface-requests#JSONDocumentSpecifications__JobDocumentContents__title__1">JSON document specifications for z/OS jobs REST interface requests: Job document</a>
 * @property id the jobid response param
 * @property name the jobname response param
 * @property subsystem the subsystem response param
 * @property owner the owner response param
 * @property jobStatus the [ZosmfJobStatus] object with the actual status of the job
 * @property type the [ZosmfJobType] object of the job type
 * @property jobClass the class response param
 * @property returnCode the retcode response param
 * @property jobUrl the url response param
 * @property jobFilesUrl the files-url response param
 * @property jobCorrelator the job-correlator response param
 * @property jobPhase the phase response param
 * @property jobPhaseName the phase-name response param
 * @property stepData the step-data response param
 * @property execSystem the exec-system response param
 * @property execMember the exec-member response param
 * @property execSubmitted the exec-submitted response param
 * @property execStarted the exec-started response param
 * @property execEnded the exec-ended response param
 * @property reasonNotRunning the reason-not-running response param
 */
@Serializable
class ZosmfJobItem(
  @SerialName("jobid")
  @property:AvailableSince(ZVersion.ZOS_2_1) override val id: String = "",

  @SerialName("jobname")
  @property:AvailableSince(ZVersion.ZOS_2_1) override val name: String = "",

  @SerialName("subsystem")
  @property:AvailableSince(ZVersion.ZOS_2_1) val subsystem: String? = null,

  @SerialName("owner")
  @property:AvailableSince(ZVersion.ZOS_2_1) override val owner: String = "",

  @SerialName("status")
  @property:AvailableSince(ZVersion.ZOS_2_1) val jobStatus: ZosmfJobStatus? = null,

  @SerialName("type")
  @property:AvailableSince(ZVersion.ZOS_2_1) val type: ZosmfJobType? = null,

  @SerialName("class")
  @property:AvailableSince(ZVersion.ZOS_2_1) val jobClass: String? = null,

  @SerialName("retcode")
  @property:AvailableSince(ZVersion.ZOS_2_1) val returnCode: String? = null,

  @SerialName("url")
  @property:AvailableSince(ZVersion.ZOS_2_1) val jobUrl: String? = null,

  @SerialName("files-url")
  @property:AvailableSince(ZVersion.ZOS_2_1) val jobFilesUrl: String? = null,

  @SerialName("job-correlator")
  @property:AvailableSince(ZVersion.ZOS_2_1) val jobCorrelator: String? = null,

  @SerialName("phase")
  @property:AvailableSince(ZVersion.ZOS_2_1) val jobPhase: Int? = null,

  @SerialName("phase-name")
  @property:AvailableSince(ZVersion.ZOS_2_1) val jobPhaseName: String? = null,

  @SerialName("step-data")
  @property:AvailableSince(ZVersion.ZOS_2_2) val stepData: List<ZosmfJobStepData> = emptyList(),

  @SerialName("exec-system")
  @property:AvailableSince(ZVersion.ZOS_2_4) val execSystem: String? = null,

  @SerialName("exec-member")
  @property:AvailableSince(ZVersion.ZOS_2_4) val execMember: String? = null,

  @SerialName("exec-submitted")
  @property:AvailableSince(ZVersion.ZOS_2_4) val execSubmitted: String? = null,

  @SerialName("exec-started")
  @property:AvailableSince(ZVersion.ZOS_2_4) val execStarted: String? = null,

  @SerialName("exec-ended")
  @property:AvailableSince(ZVersion.ZOS_2_4) val execEnded: String? = null,

  @SerialName("reason-not-running")
  @property:AvailableSince(ZVersion.ZOS_2_1) val reasonNotRunning: String? = null,
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
