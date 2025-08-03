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

package org.zowe.kotlinsdk.providers.zowe.ssh.datasets.messaging

import net.schmizz.sshj.SSHClient
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetsRequest
import org.zowe.kotlinsdk.providers.zowe.SshConnection
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import java.io.ByteArrayOutputStream

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=subcommands-listds-command">LISTDS command</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=command-listds-operands">LISTDS command operands</a>
 */
class SshListDatasetsRequest(
  override val connection: SshConnection,

  /** data_set operand */
  @AvailableSince(ZVersion.ZOS_2_1) override val mask: String,

  /** STATUS operand */
  @AvailableSince(ZVersion.ZOS_2_1) val shouldReturnStatus: Boolean = false,

  /** HISTORY operand */
  @AvailableSince(ZVersion.ZOS_2_1) val shouldReturnHistory: Boolean = false,

  /** LABEL operand */
  @AvailableSince(ZVersion.ZOS_2_1) val shouldReturnLabel: Boolean = false,

  /** CATALOG operand */
  @AvailableSince(ZVersion.ZOS_2_1) val catalogName: String? = null,
) : SshRequest, ListDatasetsRequest {

  private val status = if (shouldReturnStatus) "STATUS" else ""
  private val history = if (shouldReturnHistory) "HISTORY" else ""
  private val label = if (shouldReturnLabel) "LABEL" else ""
  private val catalog = if (catalogName != null) "CATALOG($catalogName)" else ""

  override val sshCommand = "tsocmd LISTDS \"'$mask'\" $status $history $label $catalog"

  // TODO: doc
  override fun execRequest(client: SSHClient): SshResponse {
    client.connect(connection.host, connection.port)
    try {
      client.auth(connection.username, connection.authMethods)

      val session = client.startSession()
      session.use {
        val cmd = it.exec(sshCommand)

        val output = ByteArrayOutputStream()
        cmd.inputStream.copyTo(output)

        cmd.join()
        return SshListDatasetsResponse(output.toString())
      }
    } finally {
      client.disconnect()
    }
  }

}
