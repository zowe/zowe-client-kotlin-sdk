/*
 * Copyright (c) 2024 IBA Group.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   IBA Group
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.core.files.data

val EMPTY_FILE_PERMISSIONS = FilePermissions(PermissionSet(), PermissionSet(), PermissionSet())

/** Permission set class to represent a permissions applied to a specific access category */
data class PermissionSet(
  val read: Boolean = false,
  val write: Boolean = false,
  val execute: Boolean = false
) {
  override fun toString(): String =
    (if (read) "r" else "-") +
    (if (write) "w" else "-") +
    (if (execute) "x" else "-")

  companion object {
    fun fromString(str: String): PermissionSet {
      require(str.length == 3) { "PermissionSet string must be 3 characters long" }
      return PermissionSet(
        read = str[0] == 'r',
        write = str[1] == 'w',
        execute = str[2] == 'x'
      )
    }
  }
}

/** USS file permissions holder class */
open class FilePermissions(
  var owner: PermissionSet,
  var group: PermissionSet = PermissionSet(),
  var others: PermissionSet = PermissionSet(),
  var prefix: String = ""
) {
  override fun toString(): String = "$prefix$owner$group$others"

  companion object {
    fun fromString(str: String): FilePermissions {
      val (prefix, modeStr) =
        if (str.length == 10) str.substring(0, 1) to str.substring(1)
        else "" to str

      require(modeStr.length == 9) { "FilePermissions string must be 9 characters long" }
      return FilePermissions(
        owner = PermissionSet.fromString(modeStr.substring(0, 3)),
        group = PermissionSet.fromString(modeStr.substring(3, 6)),
        others = PermissionSet.fromString(modeStr.substring(6, 9)),
        prefix
      )
    }
  }
}
