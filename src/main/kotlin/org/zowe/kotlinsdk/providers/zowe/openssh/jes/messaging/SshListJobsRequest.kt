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
import org.zowe.kotlinsdk.core.jes.api.messaging.ListJobsRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.produceIsfexecStRexxScript
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.produceRexxScriptRunFile

// TODO: doc
class SshListJobsRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val jobPrefix: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val jobId: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val jobOwner: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val maxJobsToReturn: Int = 0,
  @property:AvailableSince(ZVersion.ZOS_2_2) val isFetchStepData: Boolean = false,
  @property:AvailableSince(ZVersion.ZOS_2_2) val isFetchExecData: Boolean = false,
) : SshRequest, ListJobsRequest {

  private val cmdName: String = "list_jobs"

  override var sshCommand: String = ""
    get() {
      return produceRexxScriptRunFile(cmdName) {
        produceIsfexecStRexxScript(
          jobPrefix = jobPrefix,
          jobId = jobId,
          jobOwner = jobOwner,
          isFetchStepData = isFetchStepData,
          isFetchExecData = isFetchExecData
        )
      }
    }

  override suspend fun produceResponseObject(clientResponse: Any): SshListJobsResponse {
    return SshListJobsResponse(clientResponse as SshCmdResponse)
  }

}
