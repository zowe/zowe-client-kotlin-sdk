/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging

import net.schmizz.sshj.SSHClient
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.DeleteDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.SshConnection
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.SshResponse

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=subcommands-delete-command">DELETE command</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=command-delete-operands">DELETE command operands</a>
 */
class SshDeleteDatasetRequest(
  override val connection: SshConnection,
  /** Data set or data set + member to delete */
  @property:AvailableSince(ZVersion.ZOS_2_1) override val dsName: String
) : SshRequest, DeleteDatasetRequest {
  override var sshCommand: String = "tsocmd \"DELETE $dsName\""

  override fun execRequest(client: SSHClient): SshResponse {
    client
      .startSession()
      .use {
        val status = performSshPlainRequest(client, it)
        return SshDeleteDatasetResponse(status)
      }
  }
}
