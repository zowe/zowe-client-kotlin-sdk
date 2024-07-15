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
import retrofit2.Response

class ZosUssCopy (
    var connection: ZOSConnection,
    var httpClient: OkHttpClient = UnsafeOkHttpClient.unsafeOkHttpClient
) {
    init {
        connection.checkConnection()
    }

    var response: Response<*>? = null

    /**
     * Copies USS file
     *
     * @param filePath path of the file or directory (e.g. u/jiahj/text.txt)
     * @param destPath path where to copy the file
     * @param replace if true file in the target destination will be overwritten
     * @return http response object
     * @throws Exception error processing request
     */
    fun copy(filePath: String, destPath: String, replace: Boolean): Response<*> {
        val url = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
        val dataApi = buildApi<DataAPI>(url, httpClient)
        val call = dataApi.copyUssFile(
            authorizationToken = Credentials.basic(connection.user, connection.password),
            body = CopyDataUSS.CopyFromFileOrDir(
                from = filePath,
                overwrite = replace
            ),
            filePath = FilePath(destPath)
        )
        response = call.execute()
        validateResponse(response)
        return response ?: throw Exception("No response returned")
    }

    /**
     * Copies USS file to sequential dataset
     *
     * @param filePath path of the file or directory (e.g. u/jiahj/text.txt)
     * @param dsn dataset where to copy the file
     * @param replace if true information in the target dataset will be replaced
     * @return http response object
     * @throws Exception error processing request
     */
    fun copyToDS(filePath: String, dsn: String, replace: Boolean): Response<*> {
        val url = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
        val dataApi = buildApi<DataAPI>(url, httpClient)
        val call = dataApi.copyToDatasetFromUss(
            authorizationToken = Credentials.basic(connection.user, connection.password),
            body = CopyDataZOS.CopyFromFile(
                file = CopyDataZOS.CopyFromFile.File(
                    fileName = filePath
                ),
                replace = replace
            ),
            toDatasetName = dsn
        )
        response = call.execute()
        validateResponse(response)
        return response ?: throw Exception("No response returned")
    }

    /**
     * Copies USS file to dataset member
     *
     * @param filePath path of the file or directory (e.g. u/jiahj/text.txt)
     * @param dsn dataset where to copy the file
     * @param member dataset member where to copy the file
     * @param replace if true information in the target member will be replaced
     * @return http response object
     * @throws Exception error processing request
     */
    fun copyToMember(filePath: String, dsn: String, member: String, replace: Boolean): Response<*> {
        val url = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
        val dataApi = buildApi<DataAPI>(url, httpClient)
        val call = dataApi.copyToDatasetMemberFromUssFile(
            authorizationToken = Credentials.basic(connection.user, connection.password),
            body = CopyDataZOS.CopyFromFile(
                file = CopyDataZOS.CopyFromFile.File(
                    fileName = filePath
                ),
                replace = replace
            ),
            toDatasetName = dsn,
            memberName = member
        )
        response = call.execute()
        validateResponse(response)
        return response ?: throw Exception("No response returned")
    }

}
