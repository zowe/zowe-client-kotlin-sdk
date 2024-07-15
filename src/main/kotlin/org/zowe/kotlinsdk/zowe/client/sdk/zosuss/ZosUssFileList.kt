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

package org.zowe.kotlinsdk.zowe.client.sdk.zosuss

import okhttp3.Credentials
import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.*
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import org.zowe.kotlinsdk.zowe.client.sdk.zosuss.input.UssListParams
import retrofit2.Response

class ZosUssFileList (
    var connection: ZOSConnection,
    var httpClient: OkHttpClient = UnsafeOkHttpClient.unsafeOkHttpClient
) {
    init {
        connection.checkConnection()
    }

    var response: Response<*>? = null

    /**
     * List the files and directories of a UNIX file path
     *
     * @param filePath path of the file (e.g. u/jiahj/)
     * @param params USS list fili parameters
     * @return http response object
     * @throws Exception error processing request
     */
    fun listFiles(filePath: String, params: UssListParams): UssFilesList {
        val url = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
        val dataApi = buildApi<DataAPI>(url, httpClient)
        val call = dataApi.listUssPath(
            authorizationToken = Credentials.basic(connection.user, connection.password),
            path = filePath,
            xIBMMaxItems = params.limit,
            xIBMLstat = params.lstat,
            depth = params.depth,
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
        response = call.execute()
        validateResponse(response)
        return response?.body() as UssFilesList? ?: throw Exception("No body returned")
    }
}
