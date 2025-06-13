/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.files.definitions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-unix-file-directory#DeleteUnixFile__title__4">Delete a UNIX file or directory: Custom headers</a> */
@Serializable
enum class XIBMOption {
  @SerialName("recursive") RECURSIVE
}
