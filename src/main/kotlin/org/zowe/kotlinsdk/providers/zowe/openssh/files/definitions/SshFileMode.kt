/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.files.definitions

import org.zowe.kotlinsdk.core.files.data.FilePermissions
import org.zowe.kotlinsdk.core.files.data.PermissionSet

/**
 * SSH-specific USS file mode representation.
 * The USS commands operate the octal form of the mode, so the [toOctalString] conversion is provided
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-chmod-change-mode-file-directory">chmod — Change the mode of a file or directory</a>
 */
class SshFileMode(
  sshOwner: PermissionSet,
  sshGroup: PermissionSet = PermissionSet(),
  sshOthers: PermissionSet = PermissionSet()
) : FilePermissions(owner = sshOwner, group = sshGroup, others = sshOthers) {

  /**
   * Represent the mode as an octal number string, the way the USS commands expect it to be provided
   * @return the three digits octal mode string
   */
  fun toOctalString(): String {
    return listOf(owner, group, others).joinToString("") { permissionSet ->
      val digit = (if (permissionSet.read) 4 else 0) +
        (if (permissionSet.write) 2 else 0) +
        (if (permissionSet.execute) 1 else 0)
      digit.toString()
    }
  }

  companion object {
    fun fromString(str: String): SshFileMode {
      val filePermissions = FilePermissions.fromString(str)
      return SshFileMode(
        sshOwner = filePermissions.owner,
        sshGroup = filePermissions.group,
        sshOthers = filePermissions.others
      )
    }
  }
}
