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
import org.zowe.kotlinsdk.core.DataType
import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.datasets.api.messaging.RetrieveDatasetContentResponse
import org.zowe.kotlinsdk.providers.zowe.SshChannel
import org.zowe.kotlinsdk.providers.zowe.SshChannelState
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=tc-oput-copy-mvs-data-set-member-into-zos-unix-file">OPUT - Copy an MVS data set member into a z/OS UNIX file</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=tc-oput-copy-mvs-data-set-member-into-zos-unix-file#tsooput__title__6">OPUT - Copy an MVS data set member into a z/OS UNIX file: Return codes</a>
 */
class SshRetrieveDatasetContentResponse(
  sshCmdResponse: SshCmdResponse,
  requestDataType: DataType,
  override val fetchChannel: SshChannel? = null
) : SshResponse, RetrieveDatasetContentResponse {
  override var status: Status = SshStatus(sshCmdResponse)

  @AvailableSince(ZVersion.ZOS_2_1) override val fetchedDataType: DataType =
    if (status.type == StatusType.SUCCESS) requestDataType else DataType.ERROR
  @AvailableSince(ZVersion.ZOS_2_1) override val fetchedText =
    if (fetchChannel == null) sshCmdResponse.output else null

  override fun readAsIs(): List<ByteArray> {
    return when(fetchedDataType) {
      DataType.BINARY -> {
        if (fetchChannel == null) throw Exception("Channel to read BINARY data is not defined")

        val chunks = mutableListOf<ByteArray>()
        fetchChannel.use {
          while (fetchChannel.state != SshChannelState.COMPLETE) {
            chunks.add(fetchChannel.readNextPortion())
          }
        }
        chunks
      }

      DataType.TEXT -> {
        fetchedText
          ?.toByteArray()
          ?.let { listOf(it) }
          ?: throw Exception("No data returned by the client")
      }

      else -> throw Exception("Impossible to read data in ERROR")
    }
  }
}
