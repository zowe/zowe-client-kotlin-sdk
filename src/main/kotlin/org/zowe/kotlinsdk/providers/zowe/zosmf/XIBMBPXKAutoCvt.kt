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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__5">z/OS data set and member utilities: Custom headers</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__2">z/OS UNIX file utilities: Custom headers</a>
 */
@Serializable
enum class XIBMBPXKAutoCvt {
  @SerialName("on") ON,
  @SerialName("all") ALL,
  @SerialName("off") OFF;
}
