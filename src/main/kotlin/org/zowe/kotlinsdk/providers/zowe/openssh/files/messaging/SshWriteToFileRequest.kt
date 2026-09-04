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
import org.zowe.kotlinsdk.core.DataType
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import org.zowe.kotlinsdk.core.files.api.messaging.WriteToFileRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest

/**
 * Write to USS file SSH request.
 * The content is passed to the standard input of the "cp" command, the same way it is done
 * for the data sets, so the conversion is controlled by the "cp" command data type option.
 * The "cp" command creates the file if it does not exist yet and overwrites its content if it does,
 * so the directory to hold the file is the only thing that is expected to be there already
 * @property connection the SSH connection object to perform the operation with
 * @property filePath the USS file path to write the content to
 * @property content the content to write to the file
 * @property dataType the data type to write the content as
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-cp-copy-files">cp — Copy files</a>
 */
class SshWriteToFileRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val filePath: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val content: ByteArray,
  @property:AvailableSince(ZVersion.ZOS_2_2) val dataType: DataType = DataType.TEXT,
) : SshRequest, WriteToFileRequest {
  private val dataTypeArg = when (dataType) {
    DataType.TEXT -> "-T"
    DataType.BINARY -> "-B"
    DataType.ERROR -> ""
  }

  override var sshCommand = "cp $dataTypeArg '/dev/fd0' '$filePath'"
  override val multilineContent: ByteArray = content

  override suspend fun produceResponseObject(clientResponse: Any): SshWriteToFileResponse {
    return SshWriteToFileResponse(clientResponse as SshCmdResponse)
  }

  /**
   * Execute the writeToFile request.
   * If the data type is error, forms appropriate response object with error status,
   * otherwise writes the [content] to the file with the "cp" command
   * @param client the [SSHClient] to execute the SSH command with
   * @return the [SshWriteToFileResponse] object with respective status
   */
  override suspend fun execSshRequest(client: SSHClient): SshWriteToFileResponse {
    if (dataType == DataType.ERROR) {
      return produceResponseObject(SshCmdResponse(1, output = "DATA TYPE ERROR"))
    }

    val clientResponse = performSshPlainRequest(client)
    return produceResponseObject(clientResponse)
  }
}
