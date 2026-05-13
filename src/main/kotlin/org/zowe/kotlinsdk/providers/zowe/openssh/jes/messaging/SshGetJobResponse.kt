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
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.definitions.SshJobItem
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.parseSingleJob

/**
 * Get job SSH command response
 * @property job the job instance that contains all the info about the job
 */
class SshGetJobResponse(cmdResponse: SshCmdResponse) : SshResponse, GetJobResponse {
  override val status: Status = SshRexxScriptStatus(cmdResponse)

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
      parseSingleJob(jobOutputParts)
    } else {
      SshJobItem("UNDEF", "UNDEF", "UNDEF")
    }
  }

  override val job = produceSshJobItem(cmdResponse)
}
