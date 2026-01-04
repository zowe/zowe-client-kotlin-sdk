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
import org.zowe.kotlinsdk.core.Response

/**
 * Represents basic response for [RetrieveDatasetContentRequest]
 * @property fetchedDataType the fetched data type. Usually content is fetched either as TEXT or as BINARY stream.
 *                           if for some reason the data is not returned correctly or some error occurred during
 *                           a request, the data type is ERROR then
 * @property fetchedText the actual text, fetched with the request. In BINARY mode it is null
 * @property fetchChannel if the data is being fetched in BINARY format, this property provides the stream point
 *                        to connect to for the data reading in bytes
 */
interface RetrieveDatasetContentResponse : Response {
  val fetchedDataType: DataType
  val fetchedText: String?
  val fetchChannel: Any?

  /**
   * Read the data set content as a list of byte arrays.
   * If the data is in BINARY format, it will try to fetch all the data from the [fetchChannel],
   * so use this function wisely (not recommended for production use)
   * @return the list of byte arrays as the content
   */
  fun readAsIs(): List<ByteArray>
}
