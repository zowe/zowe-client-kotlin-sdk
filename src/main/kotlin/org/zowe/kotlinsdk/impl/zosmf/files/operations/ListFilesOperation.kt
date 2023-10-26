//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.files.operations

import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.Operation
import org.zowe.kotlinsdk.impl.zosmf.files.ZosmfFilesAPICalls
import org.zowe.kotlinsdk.impl.zosmf.files.data.ZosmfListFilesRequest
import org.zowe.kotlinsdk.impl.zosmf.files.data.ZosmfListFilesResponse
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Query

// TODO: doc
internal class ListFilesOperation (
  private val params: ZosmfListFilesRequest,
  private val connection: Connection,
  httpClient: OkHttpClient
) : Operation<ZosmfFilesAPICalls, ZosmfListFilesResponse>(connection, httpClient, ZosmfFilesAPICalls::class.java) {
  override fun buildCall(runnerAPI: ZosmfFilesAPICalls): Call<ZosmfListFilesResponse> {
    return runnerAPI.listFiles(
      authorizationToken = connection.basicCredentials,
      asyncThreshold = params.asyncThreshold,
      responseTimeout = params.responseTimeout,
      sessionLimitWait = params.sessionLimitWait,
      requestAcctnum = params.requestAcctnum,
      requestProc = params.requestProc,
      requestRegion = params.requestRegion,
      targetSystem = params.targetSystem,
      maxItems = params.maxItems,
      xIBMLstat = params.xIBMLstat,
      targetSystemUser = params.targetSystemUser,
      targetSystemPassword = params.targetSystemPassword,
      path = params.filter,
      depth = params.depth,
      limit = params.limit,
      fileSystem = params.fileSystem,
      followSymlinks = params.followSymlinks,
      group = params.group,
      mtime = params.mtime,
      name = params.name,
      size = params.size,
      perm = params.perm,
      type = params.type,
      user = params.user
    )
  }
}