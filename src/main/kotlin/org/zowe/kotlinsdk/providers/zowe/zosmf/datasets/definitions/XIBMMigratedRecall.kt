/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-members-zos-data-set#ListDataSetMembers__title__3">List the members of a z/OS data set: Custom headers</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-data-set-member#GetReadDataSet__title__4">Retrieve the contents of a z/OS data set or member: Custom headers</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-write-data-zos-data-set-member#PutWriteDataSet__title__4">Write data to a z/OS data set or member: Custom headers</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__5">z/OS data set and member utilities: Custom headers</a>
 */
@Serializable
enum class XIBMMigratedRecall {
  @SerialName("wait") WAIT,
  @SerialName("nowait") NOWAIT,
  @SerialName("error") ERROR
}
