//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.files

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.XIBMAttributes
import org.zowe.kotlinsdk.impl.zosmf.files.data.SymlinkMode
import org.zowe.kotlinsdk.impl.zosmf.files.data.ZosmfListFilesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// TODO: doc
internal interface ZosmfFilesAPICalls {
  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("zosmf/restfiles/fs")
  fun listFiles(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-Async-Threshold") asyncThreshold: Int?,
    @Header("X-IBM-Response-Timeout") responseTimeout: Int?,
    @Header("X-IBM-Session-Limit-Wait") sessionLimitWait: Int?,
    @Header("X-IBM-Request-Acctnum") requestAcctnum: String?,
    @Header("X-IBM-Request-Proc") requestProc: String?,
    @Header("X-IBM-Request-Region") requestRegion: String?,
    @Header("X-IBM-Target-System") targetSystem: String?,
    @Header("X-IBM-Max-Items") maxItems: Int?,
    @Header("X-IBM-Lstat") xIBMLstat: Boolean?,
    @Header("X-IBM-Target-System-User") targetSystemUser: String?,
    @Header("X-IBM-Target-System-Password") targetSystemPassword: String?,
    @Query("path") path: String,
    @Query("depth") depth: Int?,
    @Query("limit") limit: Int?,
    @Query("filesys") fileSystem: String?,
    @Query("symlinks") followSymlinks: SymlinkMode?,
    @Query("group") group: String?,
    @Query("mtime") mtime: String?,
    @Query("name") name: String?,
    @Query("size") size: String?,
    @Query("perm") perm: String?,
    @Query("type") type: String?,
    @Query("user") user: String?
  ): Call<ZosmfListFilesResponse>
}
