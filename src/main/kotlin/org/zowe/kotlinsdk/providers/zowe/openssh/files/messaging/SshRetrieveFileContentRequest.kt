/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging

import net.schmizz.sshj.SSHClient
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.ChanneledRequest
import org.zowe.kotlinsdk.core.DataType
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import org.zowe.kotlinsdk.core.files.api.messaging.RetrieveFileContentRequest
import org.zowe.kotlinsdk.providers.zowe.SshChannel
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest

/**
 * Retrieve USS file content SSH request.
 * The content is copied to the standard output of the SSH command, the same way it is done
 * for the data sets, so the conversion is controlled by the "cp" command data type option
 * @property connection the SSH connection object to perform the operation with
 * @property filePath the USS file path to get content of
 * @property dataType the data type to retrieve the content as
 * @property channelSize the channel size to read data in portions from SSH channel
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-cp-copy-files">cp — Copy files</a>
 */
class SshRetrieveFileContentRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val filePath: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val dataType: DataType,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val channelSize: Int = ChanneledRequest.DEFAULT_CHANNEL_SIZE,
) : SshRequest, RetrieveFileContentRequest {
  private val dataTypeArg = when (dataType) {
    DataType.TEXT -> "-T"
    DataType.BINARY -> "-B"
    DataType.ERROR -> ""
  }

  override var sshCommand = "cp $dataTypeArg '$filePath' '/dev/fd1'"

  override suspend fun produceResponseObject(clientResponse: Any): SshRetrieveFileContentResponse {
    return SshRetrieveFileContentResponse(clientResponse as SshCmdResponse, dataType)
  }

  /**
   * Execute the retrieveFileContent request.
   * If the data type is binary, produces an SSH channel to fetch content from.
   * If the data type is text, produces the immediate response.
   * If the data type is error, forms appropriate response object with error status
   * @param client the [SSHClient] to execute the SSH command with
   * @return the [SshRetrieveFileContentResponse] object with respective status
   */
  override suspend fun execSshRequest(client: SSHClient): SshRetrieveFileContentResponse {
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
      SshRetrieveFileContentResponse(sshCmdResponse, dataType, sshChannel)
    } else {
      produceResponseObject(sshCmdResponse)
    }
  }
}
