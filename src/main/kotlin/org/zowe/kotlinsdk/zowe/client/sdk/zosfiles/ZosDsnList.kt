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
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.zowe.client.sdk.zosfiles

import org.zowe.kotlinsdk.*
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import org.zowe.kotlinsdk.zowe.client.sdk.zosfiles.input.ListParams
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Response

/**
 * ZosDsnList class that provides Dataset member list function
 *
 * @property conenction [ZOSConnection] object connection information
 * @property httpClient okHttpClient
 */
class ZosDsnList(
  var connection: ZOSConnection,
  var httpClient: OkHttpClient = ZosmfOkHttpClient.getOkHttpClient(connection)
) {

  init {
    connection.checkConnection()
  }

  var response: Response<*>? = null

  /**
   * Get a list of Dataset names
   *
   * @param dataSetName name of a dataset
   * @param params [ListParams] object
   * @return A String list of Dataset names
   * @throws Exception error processing request
   */
  fun listDsn(datasetName: String, listParams: ListParams): DataSetsList {
    val baseUrl = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
    val dataApi = buildApi<DataAPI>(baseUrl, httpClient)
    val call = dataApi.listDataSets(
      authorizationToken = Credentials.basic(connection.user, connection.password),
      xIBMAttr = XIBMAttr(listParams.attribute, listParams.returnTotalRows),
      xIBMMaxItems = listParams.maxLength ?: 0,
      xIBMResponseTimeout = listParams.responseTimeout,
      dsLevel = datasetName,
      volser = listParams.volume,
      start = listParams.start
    )
    response = call.execute()
    validateResponse(response)
    return response?.body() as DataSetsList? ?: throw Exception("No body returned")
  }

  /**
   * Get a list of members from a Dataset
   *
   * @param datasetName name of a dataset
   * @param listParams [ListParams] object
   * @return list of member names
   * @throws Exception error processing request
   */
  fun listDsnMembers(datasetName: String, listParams: ListParams): MembersList {
    val baseUrl = "${connection.protocol}://${connection.host}:${connection.zosmfPort}"
    val dataApi = buildApi<DataAPI>(baseUrl, httpClient)
    val call = dataApi.listDatasetMembers(
      authorizationToken = Credentials.basic(connection.user, connection.password),
      xIBMAttr = XIBMAttr(listParams.attribute, listParams.returnTotalRows),
      xIBMMaxItems = listParams.maxLength ?: 0,
      xIBMMigratedRecall = MigratedRecall.valueOf(listParams.recall ?: "WAIT"),
      datasetName = datasetName,
      start = listParams.start ,
      pattern = listParams.pattern
    )
    response = call.execute()
    validateResponse(response)
    return response?.body() as MembersList? ?: throw Exception("No body returned")
  }
}
