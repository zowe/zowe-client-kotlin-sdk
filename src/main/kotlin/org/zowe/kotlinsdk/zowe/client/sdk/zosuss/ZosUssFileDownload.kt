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
import okhttp3.ResponseBody
import org.zowe.kotlinsdk.*
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import retrofit2.Response
import java.io.InputStream

class ZosUssFileDownload (
    var connection: ZOSConnection,
    var httpClient: OkHttpClient = UnsafeOkHttpClient.unsafeOkHttpClient
) {
    init {
        connection.checkConnection()
    }

    var response: Response<*>? = null

    /**
     * Retrieves the contents of USS file
     *
     * @param filePath path of the file (e.g. u/jiahj/text.txt)
     * @return http response object
     * @throws Exception error processing request
     */
    fun retrieveContents(filePath: String): InputStream {
        val url = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
        val dataApi = buildApi<DataAPI>(url, httpClient)
        val call = dataApi.retrieveUssFileContent(
            authorizationToken = Credentials.basic(connection.user, connection.password),
            filePath = FilePath(filePath)
        )
        response = call.execute()
        validateResponse(response)
        return (response?.body() as ResponseBody).byteStream() ?: throw Exception("No stream returned")
    }
}
