/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.files.definitions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.files.data.EMPTY_FILE_PERMISSIONS
import org.zowe.kotlinsdk.core.files.data.FileItem
import org.zowe.kotlinsdk.core.files.data.FileItem.FileType
import org.zowe.kotlinsdk.core.files.data.FilePermissions

/** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-list-files-directories-unix-file-path#ListUNIXfiles__title__11">List the files and directories of a UNIX file path: Example response</a> */
@Serializable
class ZosmfFileItem(
  /** name response param */
  @SerialName("name")
  @property:AvailableSince(ZVersion.ZOS_2_1) override val name: String,

  /** mode response param */
  @SerialName("mode")
  @property:AvailableSince(ZVersion.ZOS_2_1) private val mode: String? = null,

  /** size response param */
  @SerialName("size")
  @property:AvailableSince(ZVersion.ZOS_2_1) val size: Long? = null,

  /** uid response param */
  @SerialName("uid")
  @property:AvailableSince(ZVersion.ZOS_2_1) val uid: Long? = null,

  /** user response param */
  @SerialName("user")
  @property:AvailableSince(ZVersion.ZOS_2_1) val user: String? = null,

  /** gid response param */
  @SerialName("gid")
  @property:AvailableSince(ZVersion.ZOS_2_1) val gid: Long? = null,

  /** group response param */
  @SerialName("group")
  @property:AvailableSince(ZVersion.ZOS_2_1) val group: String? = null,

  /** mtime response param */
  @SerialName("mtime")
  @property:AvailableSince(ZVersion.ZOS_2_1) val mtime: String? = null,

  /** target response param */
  @SerialName("target")
  @property:AvailableSince(ZVersion.ZOS_2_1) val target: String? = null,
) : FileItem {
  override val fileType: FileType
    get() = if (mode?.first() == 'd') FileType.DIRECTORY else FileType.FILE

  override val fileMode: FilePermissions
    get() = mode?.let { FilePermissions.fromString(it) } ?: EMPTY_FILE_PERMISSIONS

  override val isSymlink: Boolean
    get() = target != null
}
