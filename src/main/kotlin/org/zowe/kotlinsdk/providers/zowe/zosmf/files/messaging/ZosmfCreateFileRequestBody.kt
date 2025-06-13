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

package org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.definitions.ZosmfFileMode

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-create-unix-file-directory#CreateUnixFile__title__2">Create a UNIX file or directory: Request Body</a> */
@Serializable
data class ZosmfCreateFileRequestBody(
  /** The request type */
  @SerialName("type")
  @AvailableSince(ZVersion.ZOS_2_1) val type: ZosmfFileType,

  /** Specifies the file or directory permission bits to be used in creating the file or directory */
  @SerialName("mode")
  @AvailableSince(ZVersion.ZOS_2_1) val mode: ZosmfFileMode
) {
  @Serializable
  enum class ZosmfFileType {
    @SerialName("file") FILE,

    @OptIn(ExperimentalSerializationApi::class)
    @SerialName("directory")
    @JsonNames("dir")
    FOLDER
  }
}
