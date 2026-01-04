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

import org.zowe.kotlinsdk.core.Request

/**
 * Represents basic request to write data to a dataset
 * @property dsName name of the dataset into which the content will be written
 * @property content content that will be written to the dataset
 * @property contentType the content type to be written (TEXT or BINARY)
 */
interface WriteToDatasetRequest : Request {
  val dsName: String
  val content: ByteArray
  val contentType: ContentType

  enum class ContentType { TEXT, BINARY }
}
