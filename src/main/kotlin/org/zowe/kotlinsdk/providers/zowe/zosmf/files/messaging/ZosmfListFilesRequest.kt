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

package org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import kotlinx.coroutines.runBlocking
import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.files.api.messaging.ListFilesRequest
import org.zowe.kotlinsdk.providers.zowe.Connection
import org.zowe.kotlinsdk.providers.zowe.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.HttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.definitions.ZosmfSymlinkMode

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-files-directories-unix-file-path">List the files and directories of a UNIX file path</a> */
class ZosmfListFilesRequest(
  override val connection: HttpConnection,

  /** path query param */
  @AvailableSince(ZVersion.ZOS_2_1) override val filter: String,

  /** depth query param */
  @AvailableSince(ZVersion.ZOS_2_3) val depth: Int? = null,

  /** limit query param */
  @AvailableSince(ZVersion.ZOS_2_3) val limit: Int? = null,

  /** filesys query param */
  @AvailableSince(ZVersion.ZOS_2_3) val fileSystem: String? = null,

  /** symlinks query param */
  @AvailableSince(ZVersion.ZOS_2_3) val followSymlinks: ZosmfSymlinkMode? = null,

  /** group query param */
  @AvailableSince(ZVersion.ZOS_2_3) val group: String? = null,

  /** mtime query param */
  @AvailableSince(ZVersion.ZOS_2_3) val mtime: String? = null,

  /** name query param */
  @AvailableSince(ZVersion.ZOS_2_3) val name: String? = null,

  /** size query param */
  @AvailableSince(ZVersion.ZOS_2_3) val size: String? = null,

  /** perm query param */
  @AvailableSince(ZVersion.ZOS_2_3) val perm: String? = null,

  /** type query param */
  @AvailableSince(ZVersion.ZOS_2_3) val type: String? = null,

  /** user query param */
  @AvailableSince(ZVersion.ZOS_2_3) val user: String? = null,

  /** X-IBM-Max-Items custom header */
  @AvailableSince(ZVersion.ZOS_2_1) override val maxItems: Int? = null,

  /** X-IBM-Lstat custom header */
  @AvailableSince(ZVersion.ZOS_2_1) override val lstat: Boolean? = null,

  /** X-IBM-Target-System default header */
  @AvailableSince(ZVersion.ZOS_2_4) override val targetSystem: String? = null,

  /** X-IBM-Target-System-User custom header */
  @AvailableSince(ZVersion.ZOS_2_4) override val targetSystemUser: String? = null,

  /** X-IBM-Target-System-Password custom header */
  @AvailableSince(ZVersion.ZOS_2_4) override val targetSystemPassword: String? = null,

  /** X-IBM-Session-Limit-Wait default header */
  @AvailableOnly(ZVersion.ZOS_2_4) override val sessionLimitWait: Int? = null,

  /** X-IBM-Async-Threshold default header */
  @AvailableSince(ZVersion.ZOS_2_1) override val asyncThreshold: Int? = null,

  /** X-IBM-Response-Timeout default header */
  @AvailableSince(ZVersion.ZOS_2_1) override val responseTimeout: Int? = null,

  /** X-IBM-Request-Acctnum default header */
  @AvailableSince(ZVersion.ZOS_2_5) override val requestAcctnum: String? = null,

  /** X-IBM-Request-Proc default header */
  @AvailableSince(ZVersion.ZOS_2_5) override val requestProc: String? = null,

  /** X-IBM-Request-Region default header */
  @AvailableSince(ZVersion.ZOS_2_5) override val requestRegion: String? = null,
) : HttpRequest, ListFilesRequest, ZosmfListFilesRequestHeaders {

  override val method = HttpMethod.Get

  override val path = "/zosmf/restfiles/fs"

  override val headers = getHeadersMap()

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

  override fun produceHttpResponse(clientResponse: HttpResponse): org.zowe.kotlinsdk.providers.zowe.HttpResponse? {
    return runBlocking {
      val response = clientResponse.body<ZosmfListFilesResponse>()
      response.status = clientResponse.status
      response
    }
  }

}