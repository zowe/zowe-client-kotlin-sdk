// Copyright © 2020 IBA Group, a.s. All rights reserved. Use of this source code is governed by Eclipse Public License – v 2.0 that can be found at: https://www.eclipse.org/legal/epl-2.0/

package org.zowe.kotlinsdk.zowe.client.sdk.zosuss

import org.zowe.kotlinsdk.*
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
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
        if (response?.isSuccessful != true) {
            throw Exception(response?.errorBody()?.string())
        }
        return (response?.body() as ResponseBody).byteStream() ?: throw Exception("No stream returned")
    }
}
