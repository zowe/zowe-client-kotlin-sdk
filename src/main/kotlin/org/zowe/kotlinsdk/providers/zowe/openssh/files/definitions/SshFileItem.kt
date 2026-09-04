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

import org.zowe.kotlinsdk.core.files.data.FileItem
import org.zowe.kotlinsdk.core.files.data.FilePermissions

/**
 * SSH USS item, produced by SSH response handling functions
 * @property name the name of the item, without the path it is located at
 * @property fileType the type of the item. A symbolic link is reported as [FileItem.FileType.FILE],
 *                    regardless of the type of the item it points to
 * @property fileMode the permissions of the item
 * @property target the path the symbolic link points to, null if the item is not a symbolic link
 */
class SshFileItem(
  override val name: String,
  override val fileType: FileItem.FileType,
  override val fileMode: FilePermissions,
  val target: String? = null
) : FileItem {
  /** Identifies if the item is a symbolic link. Computed by the presence of the [target] */
  override val isSymlink: Boolean
    get() = target != null
}
