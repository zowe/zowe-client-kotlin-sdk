/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging

import io.ktor.http.HttpMethod
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.files.api.messaging.ListFilesRequest
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.definitions.ZosmfSymlinkMode

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-files-directories-unix-file-path">List the files and directories of a UNIX file path</a>
 * @property filter the path query param
 * @property depth the depth query param
 * @property limit the limit query param
 * @property fileSystem the filesys query param
 * @property followSymlinks the symlinks query param
 * @property group the group query param
 * @property mtime the mtime query param
 * @property name the name query param
 * @property size the size query param
 * @property perm the perm query param
 * @property type the type query param
 * @property user the user query param
 */
class ZosmfListFilesRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val filter: String,
  @property:AvailableSince(ZVersion.ZOS_2_3) val depth: Int? = null,
  @property:AvailableSince(ZVersion.ZOS_2_3) val limit: Int? = null,
  @property:AvailableSince(ZVersion.ZOS_2_3) val fileSystem: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_3) val followSymlinks: ZosmfSymlinkMode? = null,
  @property:AvailableSince(ZVersion.ZOS_2_3) val group: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_3) val mtime: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_3) val name: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_3) val size: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_3) val perm: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_3) val type: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_3) val user: String? = null,
  override val headers: ZosmfListFilesRequestHeaders = ZosmfListFilesRequestHeaders()
) : ZosmfHttpRequest, ListFilesRequest {
  override val method = HttpMethod.Get
  override val path = "/zosmf/restfiles/fs"

  override val parameters = mutableMapOf(
    "path" to filter,
    "group" to group,
    "mtime" to mtime,
    "name" to name,
    "size" to size,
    "perm" to perm,
    "type" to type,
    "user" to user,
    "depth" to depth?.toString(),
    "limit" to limit?.toString(),
    "filesys" to fileSystem,
    "symlinks" to followSymlinks?.toString()
  )

  override val body = null
  override val responseClass = ZosmfListFilesResponse::class.java
}