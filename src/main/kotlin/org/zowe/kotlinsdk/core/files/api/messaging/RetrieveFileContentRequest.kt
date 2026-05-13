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

package org.zowe.kotlinsdk.core.files.api.messaging

import org.zowe.kotlinsdk.core.ChanneledRequest
import org.zowe.kotlinsdk.core.DataType

/**
 * Represents basic request to retrieve file contents
 * @property filePath the file path to retrieve a content by
 * @property dataType the data type to fetch (text or binary)
 */
interface RetrieveFileContentRequest : ChanneledRequest {
  val filePath: String
  val dataType: DataType
}
