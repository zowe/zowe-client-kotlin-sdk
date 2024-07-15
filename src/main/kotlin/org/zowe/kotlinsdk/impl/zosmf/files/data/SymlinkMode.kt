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

/**
 * symlinks query param
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-files-directories-unix-file-path">List the files and directories of a UNIX file path</a>
 * */
enum class SymlinkMode(private val symlinksVal: String) {
  FOLLOW("follow"),
  REPORT("report");

  override fun toString(): String {
    return symlinksVal
  }
}