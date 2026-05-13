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

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import org.zowe.kotlinsdk.core.jes.api.messaging.GetJobRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.definitions.SshJobStepData
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.definitions.SshJobExecData
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.produceIsfexecStRexxScript
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.produceRexxScriptRunFile

/**
 * Get job SSH request. Gets the job by the job name and job ID.
 * NOTE: functionality is tested for JES2 only.
 *       In theory, it should work with JES3 as well, if not - reach out to us with the examples.
 *       The exec-data and step-data functionality is designed to work with JES2 only
 * @property jobName the job name to search for
 * @property jobId the job ID to search for
 * @property isFetchStepData the parameter to fetch and parse [SshJobStepData] if true.
 *                           NOTE: the step-data is fetched correctly only for the jobs that are in the OUTPUT state
 * @property isFetchExecData the parameter to fetch and parse [SshJobExecData] if true.
 */
class SshGetJobRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val jobName: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val jobId: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val isFetchStepData: Boolean = false,
  @property:AvailableSince(ZVersion.ZOS_2_2) val isFetchExecData: Boolean = false
) : SshRequest, GetJobRequest {

  private val cmdName: String = "get_job"

  override var sshCommand: String = ""
    get() {
      return produceRexxScriptRunFile(cmdName) {
        produceIsfexecStRexxScript(
          jobPrefix = jobName,
          jobId = jobId,
          jobOwner = null,
          isFetchStepData = isFetchStepData,
          isFetchExecData = isFetchExecData
        )
      }
    }

  override suspend fun produceResponseObject(clientResponse: Any): SshGetJobResponse {
    return SshGetJobResponse(clientResponse as SshCmdResponse)
  }

}
