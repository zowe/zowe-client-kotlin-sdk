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

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__3">z/OS UNIX file utilities: Request body</a>
 * @property set one or more of the following: alps
 * @property reset one or more of the following: alps
 * @property request indicates the function extattr
 */
@Serializable
data class ZosmfFileExtAttributesUtilityRequestBody(
  @SerialName("set")
  @property:AvailableSince(ZVersion.ZOS_2_1) val set: String? = null,

  @SerialName("reset")
  @property:AvailableSince(ZVersion.ZOS_2_1) val reset: String? = null
) {
  @SerialName("request")
  @EncodeDefault
  @AvailableSince(ZVersion.ZOS_2_1) val request = "extattr"
}
