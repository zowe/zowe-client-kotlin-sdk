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

package org.zowe.kotlinsdk.zowe.client.sdk.zosfiles

import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.zowe.kotlinsdk.DataAPI
import org.zowe.kotlinsdk.UnsafeOkHttpClient
import org.zowe.kotlinsdk.buildApi
import org.zowe.kotlinsdk.validateResponse
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import org.zowe.kotlinsdk.zowe.client.sdk.zosfiles.input.DownloadParams
import retrofit2.Response
import java.io.InputStream

/**
 * [ZosDsnDownload] class that provides download DataSet function
 */
@Deprecated(
  "Scheduled for removal since v1.0.0",
  ReplaceWith("DatasetsAPI", "org.zowe.kotlinsdk.core.datasets.api")
)
class ZosDsnDownload (
  var connection: ZOSConnection,
  var httpClient: OkHttpClient = UnsafeOkHttpClient.unsafeOkHttpClient
) {

  init {
    connection.checkConnection()
  }

  var response: Response<*>? = null

  /**
   * Downloads a sequential dataset or dataset member content
   *
   * @param datasetName name of a sequential dataset e.g. DATASET.SEQ.DATA
   *                    or a dataset member e.g. DATASET.LIB(MEMBER))
   * @param params [DownloadParams]
   * @return a content stream
   * @throws Exception error processing request
   */
  @Deprecated(
    "Scheduled for removal since v1.0.0",
    ReplaceWith("DatasetsAPI.retrieveDatasetContent(params)", "org.zowe.kotlinsdk.core.datasets.api")
  )
  fun downloadDsn(datasetName: String, params: DownloadParams): InputStream {
    val url = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
    val dataApi = buildApi<DataAPI>(url, httpClient)
    val call = if (params.volume != null) {
      dataApi.retrieveDatasetContent(
        authorizationToken = Credentials.basic(connection.user, connection.password),
        datasetName = datasetName,
        volser = params.volume,
        xIBMReturnEtag = params.returnEtag
      )
    } else {
      dataApi.retrieveDatasetContent(
        authorizationToken = Credentials.basic(connection.user, connection.password),
        datasetName = datasetName
      )
    }
    response = call.execute()
    validateResponse(response)
    return (response?.body() as ResponseBody).byteStream() ?: throw Exception("No stream returned")
  }

}
