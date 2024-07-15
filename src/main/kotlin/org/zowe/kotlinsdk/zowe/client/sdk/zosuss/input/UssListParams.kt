/*
 * Copyright (c) 2020-2024 IBA Group.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   IBA Group
 *   Zowe Community
 */

package org.zowe.kotlinsdk.zowe.client.sdk.zosuss.input

import org.zowe.kotlinsdk.SymlinkMode

/**
 * This interface defines the options that can be sent into the list data set function
 */
@Deprecated(
    "Scheduled for removal since v1.0.0",
    ReplaceWith("USSListParams", "org.zowe.kotlinsdk.impl.restfiles")
)
class UssListParams(

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
