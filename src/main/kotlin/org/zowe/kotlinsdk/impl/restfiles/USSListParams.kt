//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.restfiles

import org.zowe.kotlinsdk.core.restfiles.SymlinkMode

// TODO: doc
class USSListParams(
  val limit: Int = 0,
  val lstat: Boolean = false,
  val group: String? = null,
  val mtime: String? = null,
  val name: String? = null,
  val size: String? = null,
  val perm: String? = null,
  val type: String? = null,
  val user: String? = null,
  val depth: Int = 1,
  val fileSystem: String? = null,
  val followSymlinks: SymlinkMode? = null
)