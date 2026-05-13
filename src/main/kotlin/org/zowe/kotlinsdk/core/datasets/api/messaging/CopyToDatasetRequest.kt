/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.core.datasets.api.messaging

import org.zowe.kotlinsdk.core.Request
import org.zowe.kotlinsdk.core.datasets.data.FromEntity

/**
 * Represents a basic copy to a data set or member request
 * @property fromEntity the entity to copy to a data set or member from
 * @property toDsName name of the new data set or the target data set name if member is being copied
 * @property toMemberName the new name of the data set member to copy to (if member is being copied).
 *                        Null if the data set itself to be copied
 */
interface CopyToDatasetRequest : Request {
  val fromEntity: FromEntity
  val toDsName: String
  val toMemberName: String?
}
