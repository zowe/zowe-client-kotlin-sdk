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
import org.zowe.kotlinsdk.core.DataType
import org.zowe.kotlinsdk.core.datasets.api.messaging.RetrieveDatasetContentRequest
import org.zowe.kotlinsdk.providers.zowe.SshChannel
import org.zowe.kotlinsdk.providers.zowe.SshConnection
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus

/**
 * Retrieve data set or member content SSH request
 * @param connection the SSH connection object to perform the operation with
 * @param dsName the data set or data set + member path to get content of
 * @param dataType the data type to retrieve the content as
 * @param conversionTableOrYes the conversion table path or "YES" string (refer to the parameter description for more info)
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=tc-oput-copy-mvs-data-set-member-into-zos-unix-file">OPUT - Copy an MVS data set member into a z/OS UNIX file</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=tc-oput-copy-mvs-data-set-member-into-zos-unix-file#tsooput__title__4">OPUT - Copy an MVS data set member into a z/OS UNIX file: Parameters</a>
 */
class SshRetrieveDatasetContentRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val dataType: DataType,
  @property:AvailableSince(ZVersion.ZOS_2_1) val conversionTableOrYes: String? = null
) : SshRequest, RetrieveDatasetContentRequest {
  private val dataTypeStr = when (dataType) {
    DataType.TEXT -> "TEXT"
    DataType.BINARY -> "BINARY"
    DataType.ERROR -> ""
  }

  private val conversionRules = if (conversionTableOrYes != null) "CONVERT($conversionTableOrYes)" else ""

  override var sshCommand = "tsocmd \"OPUT '$dsName' '/dev/fd1' $dataTypeStr $conversionRules\""

  override fun execRequest(client: SSHClient): SshResponse {
    val session = client.startSession()
    return if (dataType == DataType.BINARY) {
      val sshChannel = SshChannel(session, sshCommand)
      val status = sshChannel.status
      SshRetrieveDatasetContentResponse(status, dataType)
    } else {
      session
        .use {
          val status = if (dataType == DataType.TEXT) {
            performSshPlainRequest(client, it)
          } else {
            SshStatus(1, null, "INCORRECT DATA TYPE TO READ", "")
          }
          SshRetrieveDatasetContentResponse(status, dataType)
        }
    }
  }
}
