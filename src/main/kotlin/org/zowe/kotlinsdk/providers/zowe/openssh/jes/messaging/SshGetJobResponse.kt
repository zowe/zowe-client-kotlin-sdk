/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.jes.messaging

import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.jes.api.messaging.GetJobResponse
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.definitions.SshJobExecData
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.definitions.SshJobItem
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.definitions.SshJobStepData
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.extractJobValue

/**
 * Get job SSH command response
 * @property job the job instance that contains all the info about the job
 */
class SshGetJobResponse(cmdResponse: SshCmdResponse) : SshResponse, GetJobResponse {
  override val status: Status = SshGetJobStatus(cmdResponse)

  private val inputStatusQueues = listOf("CONVERSION", "SETUP")
  private val activeStatusQueues = listOf("EXECUTION", "INPUT")
  private val outputStatusQueues = listOf("PRINT", "OUTPUT", "XMITTER", "RECEIVE", "SPIN", "PURGE")

  /**
   * Get the job status by the queue the job is placed in
   * @param queue the queue (from ISFEXEC ST) name the job is currently in
   * @return the [SshJobItem.SshJobStatus] or null if the queue is "UNKNOWN"
   */
  private fun getJobStatusFromQueue(queue: String): SshJobItem.SshJobStatus? {
    val indepQueue = queue.replace(" (JES3)", "")
    return when {
      inputStatusQueues.contains(indepQueue) -> SshJobItem.SshJobStatus.INPUT
      activeStatusQueues.contains(indepQueue) -> SshJobItem.SshJobStatus.ACTIVE
      outputStatusQueues.contains(indepQueue) -> SshJobItem.SshJobStatus.OUTPUT
      else -> null // UNKNOWN and unparsed queue
    }
  }

  /**
   * Produce the [SshJobItem] from the [cmdResponse].
   * If the command response status is not [StatusType.SUCCESS], produces an empty job with invalid parameters
   * @return the [SshJobItem] instance with prefilled parameters
   */
  private fun produceSshJobItem(cmdResponse: SshCmdResponse): SshJobItem {
    return if (status.type == StatusType.SUCCESS) {
      val jobOutputParts = cmdResponse.output
        .split(Regex("""=== JOB \d+ OUTPUT START ==="""))
        .drop(1)
        .first()
      val jobId = extractJobValue(jobOutputParts, "Job ID")
      val jobName = extractJobValue(jobOutputParts, "Job name")
      val jobOwner = extractJobValue(jobOutputParts, "Job owner")
      val subsystem = extractJobValue(jobOutputParts, "Job subsystem")
      val jobQueue = extractJobValue(jobOutputParts, "Job queue")
      val jobStatus = getJobStatusFromQueue(jobQueue)
      val jobTypeStr = extractJobValue(jobOutputParts, "Job type")
      val jobType = SshJobItem.SshJobType.valueOf(jobTypeStr)
      val jobClass = if (jobType == SshJobItem.SshJobType.STC || jobType == SshJobItem.SshJobType.TSU) jobTypeStr
        else extractJobValue(jobOutputParts, "Job class")
      val jobRc = extractJobValue(jobOutputParts, "Job RC").ifEmpty { null }
      val phaseNum = extractJobValue(jobOutputParts, "Job phase \\(num\\)").toInt()
      val phaseName = extractJobValue(jobOutputParts, "Job phase name")
      val stepData = SshJobStepData.parseJobStepData(jobOutputParts)
      val execData = SshJobExecData(jobOutputParts)
      SshJobItem(
        jobId,
        jobName,
        jobOwner,
        subsystem,
        jobStatus,
        jobType,
        jobClass,
        jobRc,
        phaseNum,
        phaseName,
        stepData,
        execData.execSystem,
        execData.execMember,
        execData.execSubmitted,
        execData.execStarted,
        execData.execEnded
      )
    } else {
      SshJobItem("UNDEF", "UNDEF", "UNDEF")
    }
  }

  override val job = produceSshJobItem(cmdResponse)
}
