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

class ZosUssFile (
    var connection: ZOSConnection,
    var httpClient: OkHttpClient = UnsafeOkHttpClient.unsafeOkHttpClient
) {
    init {
        connection.checkConnection()
    }

    var response: Response<*>? = null

    /**
     * Deletes USS file
     *
     * @param filePath path of the file or directory (e.g. u/jiahj/text.txt)
     * @return http response object
     * @throws Exception error processing request
     */
    fun deleteFile(filePath: String): Response<*> {
        val url = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
        val dataApi = buildApi<DataAPI>(url, httpClient)
        val call = dataApi.deleteUssFile(
            authorizationToken = Credentials.basic(connection.user, connection.password),
            filePath = FilePath(filePath)
        )
        response = call.execute()
        validateResponse(response)
        return response ?: throw Exception("No response returned")
    }

    /**
     * Creates a new file or directory with specified parameters
     *
     * @param filePath path of the file or directory (e.g. u/jiahj/text.txt)
     * @param params [CreateUssFile] parameters
     * @return http response object
     * @throws Exception error processing request
     */
    fun createFile(filePath: String, params: CreateUssFile): Response<*> {
        val url = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
        val dataApi = buildApi<DataAPI>(url, httpClient)
        val call = dataApi.createUssFile(
            authorizationToken = Credentials.basic(connection.user, connection.password),
            filePath = FilePath(filePath),
            body = params
            )
        response = call.execute()
        validateResponse(response)
        return response ?: throw Exception("No response returned")
    }

    /**
     * Writes plain text to USS file. Creates new if not exist
     *
     * @param filePath path of the file or directory (e.g. u/jiahj/text.txt)
     * @param text plain text to be written to file
     * @return http response object
     * @throws Exception error processing request
     */
    fun writeToFile(filePath: String, text: ByteArray): Response<*> {
        val url = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
        val dataApi = buildApiWithBytesConverter<DataAPI>(url, httpClient)
        val call = dataApi.writeToUssFile(
            authorizationToken = Credentials.basic(connection.user, connection.password),
            filePath = FilePath(filePath),
            body = text
        )
        response = call.execute()
        validateResponse(response)
        return response ?: throw Exception("No response returned")
    }

    /**
     * Writes to USS binary file. Creates new if not exist
     *
     * @param filePath path of the file or directory (e.g. u/jiahj/text.txt)
     * @param inputFile file to be written to
     * @return http response object
     * @throws Exception error processing request
     */
    fun writeToFileBin(filePath: String, inputFile: ByteArray): Response<*> {
        val url = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
        val dataApi = buildApiWithBytesConverter<DataAPI>(url, httpClient)
        val call = dataApi.writeToUssFile(
            authorizationToken = Credentials.basic(connection.user, connection.password),
            filePath = FilePath(filePath),
            body = inputFile,
            xIBMDataType = XIBMDataType(XIBMDataType.Type.BINARY),
            contentType = "application/octet-stream"
        )
        response = call.execute()
        validateResponse(response)
        return response ?: throw Exception("No response returned")
    }
}
