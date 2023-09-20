//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.datasets

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.restfiles.XIBMAttr
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.XIBMMigratedRecall
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.XIBMAttributes
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.ZosmfListDatasetMembersResponse
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.ZosmfListDatasetsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

// TODO: doc
internal interface ZosmfDatasetsAPICalls {
  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("zosmf/restfiles/ds")
  fun listDatasets(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-Async-Threshold") asyncThreshold: Int?,
    @Header("X-IBM-Response-Timeout") responseTimeout: Int?,
    @Header("X-IBM-Session-Limit-Wait") sessionLimitWait: Int?,
    @Header("X-IBM-Request-Acctnum") requestAcctnum: String?,
    @Header("X-IBM-Request-Proc") requestProc: String?,
    @Header("X-IBM-Request-Region") requestRegion: String?,
    @Header("X-IBM-Target-System") targetSystem: String?,
    @Header("X-IBM-Max-Items") maxItems: Int?,
    @Header("X-IBM-Attributes") attributes: XIBMAttributes?,
    @Header("X-IBM-Target-System-User") targetSystemUser: String?,
    @Header("X-IBM-Target-System-Password") targetSystemPassword: String?,
    @Query("dslevel") dslevel: String,
    @Query("volser") volumeSerial: String?,
    @Query("start") start: String?
  ): Call<ZosmfListDatasetsResponse>

  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("zosmf/restfiles/ds/{dataset-name}/member")
  fun listDatasetMembers(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-Async-Threshold") asyncThreshold: Int?,
    @Header("X-IBM-Response-Timeout") responseTimeout: Int?,
    @Header("X-IBM-Session-Limit-Wait") sessionLimitWait: Int?,
    @Header("X-IBM-Request-Acctnum") requestAcctnum: String?,
    @Header("X-IBM-Request-Proc") requestProc: String?,
    @Header("X-IBM-Request-Region") requestRegion: String?,
    @Header("X-IBM-Target-System") targetSystem: String?,
    @Header("X-IBM-Max-Items") maxItems: Int?,
    @Header("X-IBM-Attributes") attributes: XIBMAttributes?,
    @Header("X-IBM-Migrated-Recall") migratedRecall: XIBMMigratedRecall?,
    @Header("X-IBM-Target-System-User") targetSystemUser: String?,
    @Header("X-IBM-Target-System-Password") targetSystemPassword: String?,
    @Path("dataset-name") datasetName: String,
    @Query("start") start: String?,
    @Query("pattern") pattern: String?
  ): Call<ZosmfListDatasetMembersResponse>
}
