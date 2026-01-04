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
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import org.zowe.kotlinsdk.core.datasets.AttributesLevel
import org.zowe.kotlinsdk.core.datasets.api.messaging.WriteToDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest

/**
 * Uses a combination of "tsocmd LISTDS" and "cp" commands. "LISTDS" is used to check whether the data set
 * or the data set member exists, "cp" - to write the content
 * @see <a href="https://www.ibm.com/docs/en/zos/3.2.0?topic=descriptions-cp-copy-file">cp - Copy a file</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=subcommands-listds-command">LISTDS command</a>
 * @property dsName the data set or data set + member name to write the content to
 * @property content the content to write
 * @property contentType the content type to write
 */
class SshWriteToDatasetRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val dsName: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val content: ByteArray,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val contentType: WriteToDatasetRequest.ContentType =
    WriteToDatasetRequest.ContentType.TEXT
) : SshRequest, WriteToDatasetRequest {
  private val contentTypeStr = when (contentType) {
    WriteToDatasetRequest.ContentType.TEXT -> "-T"
    WriteToDatasetRequest.ContentType.BINARY -> "-B"
  }

  override var sshCommand = "cp $contentTypeStr '/dev/fd0' \"//'$dsName'\""
  override val multilineContent: ByteArray = content

  override suspend fun produceResponseObject(clientResponse: Any): SshWriteToDatasetResponse {
    return SshWriteToDatasetResponse(clientResponse as SshCmdResponse)
  }

  /**
   * Executes "LISTDS" for data sets to check if the data set exists first,
   * then executes "cp" command to write the provided [content].
   * Executes "cp" straightaway for a data set member to write the provided [content]
   * @param client the [SSHClient] to execute the commands with
   * @return [SshWriteToDatasetResponse] object when the commands are executed
   */
  override suspend fun execSshRequest(client: SSHClient): SshWriteToDatasetResponse {
    if (!dsName.contains("(") || !dsName.contains(")")) {
      val listDatasetsRequest = SshListDatasetsRequest(connection, dsName, attributesLevel = AttributesLevel.NAME)
      val listDatasetsResponse = listDatasetsRequest.execSshRequest(client) as SshListDatasetsResponse
      if (listDatasetsResponse.status.type != StatusType.SUCCESS) {
        return produceResponseObject(
          SshCmdResponse(
            1,
            output = "ERROR OCCURRED DURING SEARCH FOR THE DATA SET OR MEMBER TO WRITE THE CONTENT TO." +
              "\nDETAILS OF THE \"LIST\" OPERATION:\n${listDatasetsResponse.status.text}"
          )
        )
      }
    }

    val clientResponse = performSshPlainRequest(client)
    return produceResponseObject(clientResponse)
  }
}
