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

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__3">z/OS UNIX file utilities: Request body</a> */
@Serializable
data class ZosmfLinkFileRequestBody(
  /** The file or directory to link */
  @SerialName("from")
  @property:AvailableSince(ZVersion.ZOS_2_1) val from: String,

  /** Indicates the link type as a symbol link or an external link */
  @SerialName("type")
  @property:AvailableSince(ZVersion.ZOS_2_1) val type: String,

  /** When "true", it links the files recursively, linking all the files and subdirectories specified by the source into a directory (ln -R) */
  @SerialName("recursive")
  @property:AvailableSince(ZVersion.ZOS_2_1) val recursive: Boolean? = null,

  /** When it is "true", it forces a link between files and deletes any conflicting path names that do not have confirmation (ln -f) */
  @SerialName("force")
  @property:AvailableSince(ZVersion.ZOS_2_1) val force: Boolean? = null
) {
  /** Indicates the function link */
  @SerialName("request")
  @AvailableSince(ZVersion.ZOS_2_1) val request = "link"
}
