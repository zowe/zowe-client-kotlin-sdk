/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__3">z/OS UNIX file utilities: Request body</a>
 * @property from the file or directory to be moved
 * @property overwrite TODO: doc
 * @property request indicates the function move
 */
@Serializable
data class ZosmfMoveFileRequestBody(
  @SerialName("from")
  @property:AvailableSince(ZVersion.ZOS_2_1) val from: String,

  @SerialName("overwrite")
  @property:AvailableSince(ZVersion.ZOS_2_1) val overwrite: Boolean? = null
) {
  @SerialName("request")
  @AvailableSince(ZVersion.ZOS_2_1) val request = "move"
}
