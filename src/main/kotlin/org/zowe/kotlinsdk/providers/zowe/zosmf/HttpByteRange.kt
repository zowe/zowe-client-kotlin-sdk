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

/**
 * Representation of the "Range" standard header value, applicable to the binary data reads only.
 * Is formed as "bytes=<first byte position>-<last byte position>", where any of the positions might be omitted:
 *   HttpByteRange(0, 499) -> "bytes=0-499" (the first 500 bytes)
 *   HttpByteRange(firstBytePos = 500) -> "bytes=500-" (everything starting from the byte 500)
 *   HttpByteRange(lastBytePos = 500) -> "bytes=-500" (the last 500 bytes)
 * @property firstBytePos the first byte position to read the data from. If omitted, the [lastBytePos] is
 *                        treated as the number of the trailing bytes to read
 * @property lastBytePos the last byte position to read the data to. If omitted, the data is read to the end
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-retrieve-contents-zos-unix-file#ReadUnixFile__title__3">Retrieve the contents of a z/OS UNIX file: Standard headers</a>
 */
data class HttpByteRange(private val firstBytePos: Long? = null, private val lastBytePos: Long? = null) {
  init {
    require(firstBytePos != null || lastBytePos != null) {
      "At least one of the byte positions must be provided"
    }
    require(firstBytePos == null || firstBytePos >= 0) {
      "The first byte position must not be negative, got $firstBytePos"
    }
    require(lastBytePos == null || lastBytePos >= 0) {
      "The last byte position must not be negative, got $lastBytePos"
    }
    require(firstBytePos == null || lastBytePos == null || lastBytePos >= firstBytePos) {
      "The last byte position must not be less than the first one, got $firstBytePos-$lastBytePos"
    }
  }

  override fun toString(): String {
    return when {
      firstBytePos == null -> "bytes=-$lastBytePos"
      lastBytePos == null -> "bytes=$firstBytePos-"
      else -> "bytes=$firstBytePos-$lastBytePos"
    }
  }
}
