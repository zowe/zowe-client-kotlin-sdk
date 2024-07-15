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

package org.zowe.kotlinsdk.impl.zosmf.files.api.messaging

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * The request body for link file or directory request
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
 * */
data class ZosmfLinkFileBody(
  /** The file or directory to link */
  @AvailableSince(ZVersion.ZOS_2_1)
  var from: String,

  /** Indicates the link type as a symbol link or an external link */
  @AvailableSince(ZVersion.ZOS_2_1)
  var type: String,

  /** When "true", it links the files recursively, linking all the files and subdirectories specified by the source into a directory (ln -R) */
  @AvailableSince(ZVersion.ZOS_2_1)
  var recursive: Boolean? = null,

  /** When it is "true", it forces a link between files and deletes any conflicting path names that do not have confirmation (ln -f) */
  @AvailableSince(ZVersion.ZOS_2_1)
  var force: Boolean? = null
) {
  /** Indicates the function link */
  @AvailableSince(ZVersion.ZOS_2_1)
  val request = "link"
}
