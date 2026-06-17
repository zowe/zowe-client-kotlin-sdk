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

import net.schmizz.sshj.SSHClient
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.ChanneledRequest
import org.zowe.kotlinsdk.core.DataType
import org.zowe.kotlinsdk.core.datasets.api.messaging.RetrieveDatasetContentRequest
import org.zowe.kotlinsdk.providers.zowe.SshChannel
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest

/**
 * Retrieve data set or member content SSH request
 * @property connection the SSH connection object to perform the operation with
 * @property dsName the data set or data set + member path to get content of
 * @property dataType the data type to retrieve the content as
 * @property channelSize the channel size to read data in portions from SSH channel
 * @property conversionTableOrYes the conversion table path or "YES" string (refer to the parameter description for more info)
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=tc-oput-copy-mvs-data-set-member-into-zos-unix-file">OPUT - Copy an MVS data set member into a z/OS UNIX file</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=tc-oput-copy-mvs-data-set-member-into-zos-unix-file#tsooput__title__4">OPUT - Copy an MVS data set member into a z/OS UNIX file: Parameters</a>
 */
class SshRetrieveDatasetContentRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val dsName: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val dataType: DataType,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val channelSize: Int = ChanneledRequest.DEFAULT_CHANNEL_SIZE,
  @property:AvailableSince(ZVersion.ZOS_2_2) val conversionTableOrYes: String? = null,
) : SshRequest, RetrieveDatasetContentRequest {
  private val dataTypeArg = when (dataType) {
    DataType.TEXT -> "-T"
    DataType.BINARY -> "-B"
    DataType.ERROR -> ""
  }

  override var sshCommand = "cp $dataTypeArg \"//'$dsName'\" '/dev/fd1'"

  override suspend fun produceResponseObject(clientResponse: Any): SshRetrieveDatasetContentResponse {
    return SshRetrieveDatasetContentResponse(clientResponse as SshCmdResponse, dataType)
  }

  /**
   * Execute the retrieveDatasetContent request.
   * If the data type is binary, produces an SSH channel to fetch content from.
   * If the data type is text, produces the immediate response.
   * If the data type is error, forms appropriate response object with error status
   * @param client the [SSHClient] to execute the SSH command with
   * @return the [SshRetrieveDatasetContentResponse] object with respective status
   */
  override suspend fun execSshRequest(client: SSHClient): SshRetrieveDatasetContentResponse {
    val (sshCmdResponse, sshChannel) = when (dataType) {
      DataType.BINARY -> {
        val session = client.startSession()
        val channel = SshChannel(session, sshCommand, channelSize)
        val cmdResponse = SshCmdResponse()
        cmdResponse to channel
      }
      DataType.TEXT -> performSshPlainRequest(client) to null
      else -> SshCmdResponse(1, output = "DATA TYPE ERROR") to null
    }
    return if (sshChannel != null) {
      SshRetrieveDatasetContentResponse(sshCmdResponse, dataType, sshChannel)
    } else {
      produceResponseObject(sshCmdResponse)
    }
  }
}
