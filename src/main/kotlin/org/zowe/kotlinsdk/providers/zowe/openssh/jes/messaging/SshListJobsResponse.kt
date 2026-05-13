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
import org.zowe.kotlinsdk.core.jes.api.messaging.ListJobsResponse
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.definitions.SshJobItem
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.parseSingleJob

/**
 * List jobs SSH command response.
 * Parses the multi-job SSH output and produces a list of [SshJobItem] instances.
 * @property status the status of the SSH command execution
 * @property jobs the list of parsed [SshJobItem] from the command output
 */
class SshListJobsResponse(cmdResponse: SshCmdResponse) : SshResponse, ListJobsResponse {
  override val status: Status = SshRexxScriptStatus(cmdResponse)

  /**
   * Produce the list of [SshJobItem] from the [cmdResponse].
   * Splits the output by job markers and parses each block.
   * Returns an empty list if the command was not successful.
   */
  private fun produceSshJobItems(cmdResponse: SshCmdResponse): List<SshJobItem> {
    if (status.type != StatusType.SUCCESS) return emptyList()

    // Split by the job output start marker, drop the preamble before the first job
    val jobParts = cmdResponse.output
      .split(Regex("""=== JOB \d+ OUTPUT START ==="""))
      .drop(1)

    return jobParts.map { parseSingleJob(it) }
  }

  override val jobs: List<SshJobItem> = produceSshJobItems(cmdResponse)
}
