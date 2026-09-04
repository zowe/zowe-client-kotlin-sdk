/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging

import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.files.api.messaging.ListFilesResponse
import org.zowe.kotlinsdk.core.files.data.FileItem
import org.zowe.kotlinsdk.core.files.data.FilePermissions
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.files.definitions.SshFileItem

/**
 * Response of the [SshListFilesRequest], produced from the "ls -l" command output.
 * The expected output format is:
 * ```
 * total 96
 * drwxr-xr-x   2 IBMUSER  SYS1     8192 Sep  3 10:22 subdir
 * -rw-r--r--   1 IBMUSER  SYS1      123 Sep  3 10:22 file.txt
 * lrwxrwxrwx   1 IBMUSER  SYS1        8 Sep  3 10:24 link -> file.txt
 * ```
 * The first line is the total size summary, it is skipped.
 * Each of the following lines is a single item, where the file mode is the first 10 characters
 * and the file name always starts at the [NAME_COLUMN_IDX] column
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-ls-list-file-directory-names-attributes">ls — List file and directory names and attributes</a>
 * @param sshCmdResponse the SSH command result to produce the response from
 */
class SshListFilesResponse(private val sshCmdResponse: SshCmdResponse) : SshResponse, ListFilesResponse {
  companion object {
    /** The separator between a symbolic link name and the path it points to in the "ls -l" output */
    private const val SYMLINK_SEPARATOR = " -> "

    /** The index of a character the file name starts at in a single "ls -l" output line */
    private const val NAME_COLUMN_IDX = 54
  }

  override var status: Status = SshListFilesStatus(sshCmdResponse)

  override val items: List<FileItem>

  /**
   * Produce the USS items list from the "ls -l" command output.
   * The file type and the permissions are recognized by the leading mode field of each line.
   * A symbolic link name field additionally carries the link target, it is split out to [SshFileItem.target]
   * @return the list of the recognized USS items. Empty if the command returned no items
   */
  private fun produceFilesListFromSshCmdOutput(): List<FileItem> {
    return sshCmdResponse.output
      .split("\n")
      .drop(1)
      .filter { it.isNotEmpty() }
      .map {
        val fileTypeChar = it.first()
        val fileType =
          if (fileTypeChar == 'd') FileItem.FileType.DIRECTORY
          else FileItem.FileType.FILE

        // A symbolic link is reported by "ls -l" as "<link name> -> <target path>"
        val nameField = it.substring(NAME_COLUMN_IDX)
        val (fileName, target) =
          if (fileTypeChar == 'l') {
            val separatorIdx = nameField.indexOf(SYMLINK_SEPARATOR)
            if (separatorIdx >= 0) {
              nameField.substring(0, separatorIdx) to nameField.substring(separatorIdx + SYMLINK_SEPARATOR.length)
            } else {
              nameField to ""
            }
          } else {
            nameField to null
          }

        val fileModeStr = it.substring(1, 10)
        val fileMode = FilePermissions.fromString(fileModeStr)
        SshFileItem(fileName, fileType, fileMode, target)
      }
  }

  init {
    items = produceFilesListFromSshCmdOutput()
  }
}
