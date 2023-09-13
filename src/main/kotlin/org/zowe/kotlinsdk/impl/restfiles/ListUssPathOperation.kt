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

import okhttp3.Credentials
import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.Operation
import org.zowe.kotlinsdk.core.restfiles.RestfilesAPI
import retrofit2.Call

// TODO: doc
class ListUssPathOperation(
  private val ussPath: String,
  private val listParams: USSListParams,
  private val connection: Connection,
  httpClient: OkHttpClient
) : Operation<RestfilesAPI, ListUssPathResponse>(connection, httpClient, RestfilesAPI::class.java) {
  override fun buildCall(runnerAPI: RestfilesAPI): Call<ListUssPathResponse> {
    return runnerAPI.listUssPath(
      authorizationToken = Credentials.basic(connection.user, connection.password),
      path = ussPath,
      xIBMMaxItems = listParams.limit,
      xIBMLstat = listParams.lstat,
      depth = listParams.depth,
      fileSystem = listParams.fileSystem,
      followSymlinks = listParams.followSymlinks,
      group = listParams.group,
      mtime = listParams.mtime,
      name = listParams.name,
      size = listParams.size,
      perm = listParams.perm,
      type = listParams.type,
      user = listParams.user
    )
  }
}