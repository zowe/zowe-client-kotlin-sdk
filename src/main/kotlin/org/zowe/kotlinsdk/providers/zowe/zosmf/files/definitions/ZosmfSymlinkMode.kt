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

/** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-list-files-directories-unix-file-path#ListUNIXfiles__table_treetraversal">List the files and directories of a UNIX file path: Tree traversal parameters</a> */
@Serializable
enum class ZosmfSymlinkMode {
  @SerialName("follow") FOLLOW,
  @SerialName("report") REPORT
}
