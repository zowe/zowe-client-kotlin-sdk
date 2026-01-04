/*
 * Copyright (c) 2024 IBA Group.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   IBA Group
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.core.datasets.api.messaging

import org.zowe.kotlinsdk.core.DataType
import org.zowe.kotlinsdk.core.Request

/**
 * Represents basic request to retrieve dataset content
 * @property dsName name of the dataset whose content will be retrieved
 * @property dataType the data type to fetch (text or binary)
 * @property channelSize the channel size for channeled data read
 */
interface RetrieveDatasetContentRequest : Request {
  val dsName: String
  val dataType: DataType
  val channelSize: Int
    get() = DEFAULT_CHANNEL_SIZE

  companion object {
    const val DEFAULT_CHANNEL_SIZE = 32768
    const val SLOW_INTERNET_CHANNEL_SIZE = 8192
    const val FAST_INTERNET_CHANNEL_SIZE = 65536
  }
}
