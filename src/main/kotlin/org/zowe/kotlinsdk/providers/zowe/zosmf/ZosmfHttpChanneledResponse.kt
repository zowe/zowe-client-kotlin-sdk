/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.runBlocking
import org.zowe.kotlinsdk.core.ChanneledRequest
import org.zowe.kotlinsdk.core.ChanneledResponse
import org.zowe.kotlinsdk.core.DataType

/**
 * Abstract class for z/OSMF REST API channeled response classes.
 * Provides abstraction for a generic channeled response processing
 * @property fetchChannel a [ByteReadChannel] to read bytes from
 */
abstract class ZosmfHttpChanneledResponse(
  override val fetchChannel: ByteReadChannel? = null,
  open val contentLength: Long? = null,
  open val channelSize: Int = ChanneledRequest.DEFAULT_CHANNEL_SIZE
) : ZosmfHttpResponse(), ChanneledResponse {
  override fun readAsIs(): List<ByteArray> {
    return when(fetchedDataType) {
      DataType.BINARY -> {
        if (fetchChannel == null) throw Exception("Channel to read BINARY data is not defined")
        if (contentLength == null) throw Exception("Content length must be defined to read BINARY data")

        val chunks = mutableListOf<ByteArray>()
        var totalRead = 0L
        val nextChunk = ByteArray(channelSize)

        while (totalRead < (contentLength ?: throw Exception("Channel to read BINARY data is not defined"))) {
          val remainingBytes = (
            (contentLength ?: throw Exception("Channel to read BINARY data is not defined"))
              - totalRead
          ).toInt()
          val toRead = minOf(remainingBytes, channelSize)

          val read = runBlocking {
            fetchChannel?.readAvailable(nextChunk, 0, toRead)
              ?: throw Exception("Channel to read BINARY data is not defined")
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
