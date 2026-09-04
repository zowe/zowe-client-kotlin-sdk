/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.datasets.definitions

import org.zowe.kotlinsdk.core.datasets.data.FromEntity

/**
 * The USS file to copy to a data set or a data set member from
 * @property entityName the absolute path of the USS file to copy from
 * @property type the type of the file content, defines the way the content is converted during the copy.
 *                Defaults to [FileType.TEXT]
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-cp-copy-file">cp - Copy a file</a>
 */
data class SshFromFile(
  override val entityName: String,
  val type: FileType = FileType.TEXT
) : FromEntity {
  /**
   * The type of the USS file content to copy, mapped to the respective "cp" command option
   * @property cpOption the "cp" command option to convert the content with
   */
  enum class FileType(val cpOption: String) {
    /** The content is converted from ASCII to EBCDIC, the lines are wrapped into records ("cp -T") */
    TEXT("-T"),

    /** The content is copied as is, no conversion is performed ("cp -B") */
    BINARY("-B"),

    /** The content is copied as an executable into a PDSE load library ("cp -X") */
    EXECUTABLE("-X")
  }

  /** Build the source operand of the "cp" command for the USS file */
  fun buildCpSourceOperand(): String {
    return "'$entityName'"
  }
}
