//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.restfiles

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import okhttp3.ResponseBody
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.XIBMMigratedRecall
import retrofit2.Call
import retrofit2.http.*

// TODO: doc
interface RestfilesAPI {
  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("/zosmf/restfiles/ds/{dataset-name}")
  fun retrieveDatasetContent(
    @Header("Authorization") authorizationToken: String,
    @Header("If-None-Match") ifNoneMatch: String? = null,
    @Header("X-IBM-Data-Type") xIBMDataType: XIBMDataType? = null,
    @Header("X-IBM-Return-Etag") xIBMReturnEtag: Boolean? = null,
    @Header("X-IBM-Migrated-Recall") xIBMMigratedRecall: XIBMMigratedRecall? = null,
    @Header("X-IBM-Record-Range") xIBMRecordRange: XIBMRecordRange? = null,
    @Header("X-IBM-Obtain-ENQ") xIBMObtainENQ: XIBMObtainENQ? = null,
    @Header("X-IBM-Release-ENQ") xIBMReleaseENQ: Boolean? = null,
    @Header("X-IBM-Session-Ref") xIBMSessionRef: String? = null,
    @Path("dataset-name") datasetName: String,
    @Query("search") search: String? = null,
    @Query("research") research: String? = null,
    @Query("insensitive") insensitive: Boolean? = null,
    @Query("maxreturnsize") maxReturnSize: Int? = null
  ): Call<ResponseBody>

  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("/zosmf/restfiles/ds/-({volser})/{dataset-name}")
  fun retrieveDatasetContent(
    @Header("Authorization") authorizationToken: String,
    @Header("If-None-Match") ifNoneMatch: String? = null,
    @Header("X-IBM-Data-Type") xIBMDataType: XIBMDataType? = null,
    @Header("X-IBM-Return-Etag") xIBMReturnEtag: Boolean? = null,
    @Header("X-IBM-Migrated-Recall") xIBMMigratedRecall: XIBMMigratedRecall? = null,
    @Header("X-IBM-Record-Range") xIBMRecordRange: XIBMRecordRange? = null,
    @Header("X-IBM-Obtain-ENQ") xIBMObtainENQ: XIBMObtainENQ? = null,
    @Header("X-IBM-Release-ENQ") xIBMReleaseENQ: Boolean? = null,
    @Header("X-IBM-Session-Ref") xIBMSessionRef: String? = null,
    @Path("volser") volser: String,
    @Path("dataset-name") datasetName: String,
    @Query("search") search: String? = null,
    @Query("research") research: String? = null,
    @Query("insensitive") insensitive: Boolean? = null,
    @Query("maxreturnsize") maxReturnSize: Int? = null
  ): Call<ResponseBody>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/ds/{dataset-name}")
  fun writeToDataset(
    @Header("Authorization") authorizationToken: String,
    @Header("If-Match") ifMatch: String? = null,
    @Header("X-IBM-Data-Type") xIBMDataType: XIBMDataType? = null,
    @Header("X-IBM-Migrated-Recall") xIBMMigratedRecall: XIBMMigratedRecall? = null,
    @Header("X-IBM-Obtain-ENQ") xIBMObtainENQ: XIBMObtainENQ? = null,
    @Header("X-IBM-Release-ENQ") xIBMReleaseENQ: Boolean? = null,
    @Header("X-IBM-Session-Ref") xIBMSessionRef: String? = null,
    @Header("Content-Type") contentType: String? = "application/octet-stream",
    @Body content: ByteArray,
    @Path("dataset-name") datasetName: String
  ): Call<Void>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/ds/{dataset-name}({member-name})")
  fun writeToDatasetMember(
    @Header("Authorization") authorizationToken: String,
    @Header("If-Match") ifMatch: String? = null,
    @Header("X-IBM-Data-Type") xIBMDataType: XIBMDataType? = null,
    @Header("X-IBM-Migrated-Recall") xIBMMigratedRecall: XIBMMigratedRecall? = null,
    @Header("X-IBM-Obtain-ENQ") xIBMObtainENQ: XIBMObtainENQ? = null,
    @Header("X-IBM-Release-ENQ") xIBMReleaseENQ: Boolean? = null,
    @Header("X-IBM-Session-Ref") xIBMSessionRef: String? = null,
    @Header("Content-Type") contentType: String? = "application/octet-stream",
    @Body content: ByteArray,
    @Path("dataset-name") datasetName: String,
    @Path("member-name") memberName: String
  ): Call<Void>

  @AvailableSince(ZVersion.ZOS_2_1)
  @POST("/zosmf/restfiles/ds/{dataset-name}")
  fun createDataset(
    @Header("Authorization") authorizationToken: String,
    @Path("dataset-name") datasetName: String,
    @Body body: CreateDatasetBody
  ): Call<Void>

  @AvailableSince(ZVersion.ZOS_2_1)
  @DELETE("/zosmf/restfiles/ds/{dataset-name}")
  fun deleteDataset(
    @Header("Authorization") authorizationToken: String,
    @Path("dataset-name") datasetName: String,
  ): Call<Void>

  @DELETE("/zosmf/restfiles/ds/{dataset-name}({member-name})")
  fun deleteDatasetMember(
    @Header("Authorization") authorizationToken: String,
    @Path("dataset-name") datasetName: String,
    @Path("member-name") memberName: String,
  ): Call<Void>

  /**
   * Copy from - to
   *
   * **SEQ** -> **SEQ**
   *
   * **PDS MEMBER** -> **SEQ** (overwrites content)
   *
   * **PDS MEMBER or MEMBERS** -> **PDS** (adds or replaces)
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/ds/{to-data-set-name}")
  fun copyToDataset(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-BPXK-AUTOCVT") xIBMBpxkAutoCvt: XIBMBpxkAutoCvt? = null,
    @Header("X-IBM-Migrated-Recall") xIBMMigratedRecall: XIBMMigratedRecall? = null,
    @Body body: ZOSCopyBody.FromDataset,
    @Path("to-data-set-name") toDatasetName: String
  ): Call<Void>

  /**
   * Volser for uncatalogued datasets
   *
   * Copy from - to
   *
   * **SEQ** -> **SEQ**
   *
   * **PDS MEMBER** -> **SEQ**
   *
   * **PDS MEMBER** or **MEMBERS** -> **PDS**
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/ds/-({to-volser})/{to-data-set-name}")
  fun copyToDataset(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-BPXK-AUTOCVT") xIBMBpxkAutoCvt: XIBMBpxkAutoCvt? = null,
    @Header("X-IBM-Migrated-Recall") xIBMMigratedRecall: XIBMMigratedRecall? = null,
    @Body body: ZOSCopyBody.FromDataset,
    @Path("to-volser") toVolser: String,
    @Path("to-data-set-name") toDatasetName: String
  ): Call<Void>

}