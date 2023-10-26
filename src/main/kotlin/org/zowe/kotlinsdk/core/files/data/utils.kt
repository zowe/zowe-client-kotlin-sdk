//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.files.data

val EMPTY_FILE_PERMISSIONS = FilePermissions(PermissionMode.NONE, PermissionMode.NONE, PermissionMode.NONE)

fun stringToPermissionMode(modeString: CharSequence): PermissionMode {
  return when (modeString) {
    "---" -> PermissionMode.NONE
    "--x" -> PermissionMode.EXECUTE
    "-w-" -> PermissionMode.WRITE
    "-wx" -> PermissionMode.WRITE_EXECUTE
    "r--" -> PermissionMode.READ
    "r-x" -> PermissionMode.READ_EXECUTE
    "rw-" -> PermissionMode.READ_WRITE
    "rwx" -> PermissionMode.READ_WRITE_EXECUTE
    else -> PermissionMode.NONE
  }
}


// TODO: doc
enum class PermissionMode {
  NONE,
  EXECUTE,
  WRITE,
  WRITE_EXECUTE,
  READ,
  READ_EXECUTE,
  READ_WRITE,
  READ_WRITE_EXECUTE
}

class FilePermissions(
  var owner: PermissionMode,
  var group: PermissionMode = PermissionMode.NONE,
  var all: PermissionMode = PermissionMode.NONE,
  var prefix: String = ""
) {

  constructor(modeString: CharSequence, prefix: String = "") :
      this(
        stringToPermissionMode(modeString.subSequence(1, 4)),
        stringToPermissionMode(modeString.subSequence(4, 7)),
        stringToPermissionMode(modeString.subSequence(7, 10)),
        prefix
      )

}