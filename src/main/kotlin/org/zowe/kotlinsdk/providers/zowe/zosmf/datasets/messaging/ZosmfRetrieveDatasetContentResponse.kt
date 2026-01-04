/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging

import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.DataType
import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.datasets.api.messaging.RetrieveDatasetContentRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.RetrieveDatasetContentResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-data-set-member">Retrieve the contents of a z/OS dataset or member</a> */
class ZosmfRetrieveDatasetContentResponse(
  override val status: Status,
  requestDataType: DataType,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val fetchedText: String? = null,
  override val fetchChannel: ByteReadChannel? = null,
  val contentLength: Long? = null,
  val channelSize: Int = RetrieveDatasetContentRequest.DEFAULT_CHANNEL_SIZE
) : ZosmfHttpResponse(), RetrieveDatasetContentResponse {
  @AvailableSince(ZVersion.ZOS_2_1) override val fetchedDataType =
    if (status.type == StatusType.SUCCESS) requestDataType else DataType.ERROR

  override fun readAsIs(): List<ByteArray> {
    return when(fetchedDataType) {
      DataType.BINARY -> {
        if (fetchChannel == null) throw Exception("Channel to read BINARY data is not defined")
        if (contentLength == null) throw Exception("Content length must be defined to read BINARY data")

        val chunks = mutableListOf<ByteArray>()
        var totalRead = 0L
        val nextChunk = ByteArray(channelSize)

        while (totalRead < contentLength) {
          val remainingBytes = (contentLength - totalRead).toInt()
          val toRead = minOf(remainingBytes, channelSize)

          val read = runBlocking {
            fetchChannel.readAvailable(nextChunk, 0, toRead)
          }
          if (read <= 0) break

          chunks.add(nextChunk.copyOf(read))
          totalRead += read
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
