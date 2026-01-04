/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.jes

import org.zowe.kotlinsdk.core.jes.api.JesAPI
import org.zowe.kotlinsdk.core.jes.api.messaging.GetJobRequest
import org.zowe.kotlinsdk.providers.zowe.SshRequestRunner
import org.zowe.kotlinsdk.providers.zowe.ZoweInternalAPI
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.messaging.SshGetJobResponse

// TODO: doc
@ZoweInternalAPI
class SshJesAPI(private val requestRunner: SshRequestRunner) : JesAPI {
  override suspend fun getJob(params: GetJobRequest): SshGetJobResponse {
    return requestRunner.runRequest(params) as SshGetJobResponse
  }

  // listJobs???
  // tsocmd "STATUS IZUSVR1" - list jobs (names + status only)
  // Status = OUTPUT:
  // IKJ56192I JOB TESTJ1(TSU01234) ON OUTPUT QUEUE
  // Status = ACTIVE:
  // IKJ56211I JOB TESTJ2(STC01234) EXECUTING
}