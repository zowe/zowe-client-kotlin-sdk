// Copyright (c) 2024 IBA Group.
//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Contributors:
//   IBA Group
//   Zowe Community

package org.zowe.kotlinsdk.impl.zosmf.files.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.files.data.EMPTY_FILE_PERMISSIONS
import org.zowe.kotlinsdk.core.files.data.FileItem
import org.zowe.kotlinsdk.core.files.data.FilePermissions

/**
 * Represents UNIX file or directory on a z/OS system
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-files-directories-unix-file-path">List the files and directories of a UNIX file path</a>
 */
class ZosmfFileItem(
  /** name response param */
  @SerializedName("name")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) private val ussFileName: String,

  /** mode response param */
  @SerializedName("mode")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val mode: String? = null,

  /** size response param */
  @SerializedName("size")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val size: Long? = null,

  /** uid response param */
  @SerializedName("uid")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val uid: Long? = null,

  /** user response param */
  @SerializedName("user")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val user: String? = null,

  /** gid response param */
  @SerializedName("gid")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val gid: Long? = null,

  /** group response param */
  @SerializedName("group")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val group: String? = null,

  /** mtime response param */
  @SerializedName("mtime")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val mtime: String? = null,

  /** target response param */
  @SerializedName("target")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val target: String? = null,
) : FileItem(
  name = ussFileName,
  fileType = if (mode?.first() == 'd') FileType.DIRECTORY else FileType.FILE,
  fileMode = mode?.let { FilePermissions(it) } ?: EMPTY_FILE_PERMISSIONS,
  isSymlink = target != null
)
