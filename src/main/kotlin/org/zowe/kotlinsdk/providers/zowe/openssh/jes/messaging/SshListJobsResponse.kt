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

import org.zowe.kotlinsdk.core.Response
import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.jes.api.messaging.ListJobsResponse
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.definitions.SshJobItem

// TODO: doc
// TODO: implement
class SshListJobsResponse(cmdResponse: SshCmdResponse) : SshResponse, ListJobsResponse {
  override val status: Status
    get() = TODO("Not yet implemented")
  override val jobs: List<SshJobItem>
    get() = TODO("Not yet implemented")

  override suspend fun produceResponseObject(clientResponse: Any): Response {
    TODO("Not yet implemented")
  }

  override suspend fun execRequest(payload: Any): Response {
    TODO("Not yet implemented")
  }
}
