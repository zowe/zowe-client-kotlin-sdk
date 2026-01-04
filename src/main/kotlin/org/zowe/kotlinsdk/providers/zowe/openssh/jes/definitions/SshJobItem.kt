/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.jes.definitions

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.jes.data.JobItem
import java.time.LocalDateTime

/**
 * The job item, produced from the SSH get job command response
 * @property id the job ID
 * @property name the job name
 * @property owner the job owner
 * @property subsystem the subsystem (JES2 or JES3) the job is executed on
 * @property jobStatus the actual [SshJobStatus]
 * @property type the [SshJobType]
 * @property jobClass the class the job is executed under (STC or TSU if the type is JOB)
 * @property returnCode if the job is finished, the return code of the job execution
 * @property jobPhase the phase number the job currently in
 * @property jobPhaseName the phase name the job currently in
 * @property stepData the [SshJobStepData] of the job (if fetched)
 * @property execSystem the system the job is executed on
 * @property execMember the node the job is executed on
 * @property execSubmitted the submission date and time of the job
 * @property execStarted the start date and time of the job
 * @property execEnded the end date and time of the job
 */
class SshJobItem(
  @property:AvailableSince(ZVersion.ZOS_2_2) override val id: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val name: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val owner: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val subsystem: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val jobStatus: SshJobStatus? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val type: SshJobType? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val jobClass: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val returnCode: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val jobPhase: Int? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val jobPhaseName: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val stepData: List<SshJobStepData> = emptyList(),
  @property:AvailableSince(ZVersion.ZOS_2_2) val execSystem: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val execMember: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val execSubmitted: LocalDateTime? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val execStarted: LocalDateTime? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val execEnded: LocalDateTime? = null,
) : JobItem {
  enum class SshJobStatus { INPUT, ACTIVE, OUTPUT }
  enum class SshJobType { JOB, STC, TSU }
}
