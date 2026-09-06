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
 * @property owner the user ID or UID (as a JSON string)
 * @property group the group ID or GID (as a JSON string)
 * @property links this applies an owner change to the file or directory pointed to by any encountered links'
 * @property recursive when 'true', changes all the files and subdirectories in that directory to belong to the specified owner and group, if :group is specified (chown -R)
 * @property request indicates the function chown
 */
@Serializable
data class ZosmfChangeFileOwnerRequestBody(
  @property:AvailableSince(ZVersion.ZOS_2_1) val owner: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) val group: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val links: Links? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) val recursive: Boolean? = null
) {
  @SerialName("request")
  @EncodeDefault
  @AvailableSince(ZVersion.ZOS_2_1) val request = "chown"

  @Serializable
  enum class Links {
    @SerialName("follow") FOLLOW,
    @SerialName("change") CHANGE
  }
}
