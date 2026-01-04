/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.DeleteDatasetRequest
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.SshResponse

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=subcommands-delete-command">DELETE command</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=command-delete-operands">DELETE command operands</a>
 * @property dsName data set or data set + member to delete
 */
class SshDeleteDatasetRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val dsName: String
) : SshRequest, DeleteDatasetRequest {
  override var sshCommand: String = "tsocmd \"DELETE $dsName\""

  override suspend fun produceResponseObject(clientResponse: Any): SshResponse {
    return SshDeleteDatasetResponse(clientResponse as SshCmdResponse)
  }
}
