/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.core

/**
 * An abstraction to provide a channeled request functionality idea to process by a [RequestRunner]
 * @property channelSize the channel size for channeled data read
 */
interface ChanneledRequest : Request {
  val channelSize: Int
    get() = DEFAULT_CHANNEL_SIZE

  companion object {
    const val DEFAULT_CHANNEL_SIZE = 32768
    const val SLOW_INTERNET_CHANNEL_SIZE = 8192
    const val FAST_INTERNET_CHANNEL_SIZE = 65536
  }
}
