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
import org.zowe.kotlinsdk.impl.restfiles.ListDatasetMembersResponse
import org.zowe.kotlinsdk.impl.restfiles.ListUssPathResponse
import retrofit2.Call
import retrofit2.http.*

// TODO: doc
interface RestfilesAPI {
  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("zosmf/restfiles/ds/{dataset-name}/member")
  fun listDatasetMembers(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-Attributes") xIBMAttr: XIBMAttr = XIBMAttr(),
    @Header("X-IBM-Max-Items") xIBMMaxItems: Int = 0,
    @Header("X-IBM-Migrated-Recall") xIBMMigratedRecall: XIBMMigratedRecall? = null,
    @Path("dataset-name") datasetName: String,
    @Query("start") start: String? = null,
    @Query("pattern") pattern: String? = null
  ): Call<ListDatasetMembersResponse>

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
  @GET("/zosmf/restfiles/ds/{dataset-name}({member-name})")
  fun retrieveMemberContent(
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
    @Path("member-name") memberName: String,
    @Query("search") search: String? = null,
    @Query("research") research: String? = null,
    @Query("insensitive") insensitive: Boolean? = null,
    @Query("maxreturnsize") maxReturnSize: Int? = null
  ): Call<String>

  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("/zosmf/restfiles/ds/-({volser})/{dataset-name}({member-name})")
  fun retrieveMemberContent(
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
    @Path("member-name") memberName: String,
    @Query("search") search: String? = null,
    @Query("research") research: String? = null,
    @Query("insensitive") insensitive: Boolean? = null,
    @Query("maxreturnsize") maxReturnSize: Int? = null
  ): Call<String>

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
  @PUT("/zosmf/restfiles/ds/-({volser})/{dataset-name}")
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
    @Path("volser") volser: String,
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
  @PUT("/zosmf/restfiles/ds/-({volser})/{dataset-name}({member-name})")
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
    @Path("volser") volser: String,
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

  @AvailableSince(ZVersion.ZOS_2_1)
  @DELETE("/zosmf/restfiles/ds/-({volume})/{dataset-name}")
  fun deleteDataset(
    @Header("Authorization") authorizationToken: String,
    @Path("volume") volume: String,
    @Path("dataset-name") datasetName: String,
  ): Call<Void>

  @AvailableSince(ZVersion.ZOS_2_1)
  @DELETE("/zosmf/restfiles/ds/{dataset-name}({member-name})")
  fun deleteDatasetMember(
    @Header("Authorization") authorizationToken: String,
    @Path("dataset-name") datasetName: String,
    @Path("member-name") memberName: String,
  ): Call<Void>

  @AvailableSince(ZVersion.ZOS_2_1)
  @DELETE("/zosmf/restfiles/ds/-({volume})/{dataset-name}({member-name})")
  fun deleteDatasetMember(
    @Header("Authorization") authorizationToken: String,
    @Path("volume") volume: String,
    @Path("dataset-name") datasetName: String,
    @Path("member-name") memberName: String,
  ): Call<Void>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/ds/{to-data-set-name}")
  fun renameDataset(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-Migrated-Recall") xIBMMigratedRecall: XIBMMigratedRecall? = null,
    @Body body: RenameDatasetBody,
    @Path("to-data-set-name") toDatasetName: String
  ): Call<Void>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/ds/{to-data-set-name}({member-name})")
  fun renameDatasetMember(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-Migrated-Recall") xIBMMigratedRecall: XIBMMigratedRecall? = null,
    @Body body: RenameDatasetBody,
    @Path("to-data-set-name") toDatasetName: String,
    @Path("member-name") memberName: String
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

  /**
   * **SEQ** -> **PDS MEMBER**
   *
   * **PDS MEMBER** -> **PDS MEMBER**
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/ds/{to-data-set-name}({member-name})")
  fun copyToDatasetMember(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-BPXK-AUTOCVT") xIBMBpxkAutoCvt: XIBMBpxkAutoCvt? = null,
    @Header("X-IBM-Migrated-Recall") xIBMMigratedRecall: XIBMMigratedRecall? = null,
    @Body body: ZOSCopyBody.FromDataset,
    @Path("to-data-set-name") toDatasetName: String,
    @Path("member-name") memberName: String
  ): Call<Void>

  /**
   * Volser for uncatalogued
   *
   * **SEQ** -> **PDS MEMBER**
   *
   * **PDS MEMBER** -> **PDS MEMBER**
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/ds/-({to-volser})/{to-data-set-name}({member-name})")
  fun copyToDatasetMemberFromUssFile(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-BPXK-AUTOCVT") xIBMBpxkAutoCvt: XIBMBpxkAutoCvt? = null,
    @Header("X-IBM-Migrated-Recall") xIBMMigratedRecall: XIBMMigratedRecall? = null,
    @Body body: ZOSCopyBody.FromDataset,
    @Path("to-volser") toVolser: String,
    @Path("to-data-set-name") toDatasetName: String,
    @Path("member-name") memberName: String
  ): Call<Void>

  /**
   * **USS FILE** -> **SEQ** (truncates contents)
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/ds/{to-data-set-name}")
  fun copyToDatasetFromUss(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-BPXK-AUTOCVT") xIBMBpxkAutoCvt: XIBMBpxkAutoCvt? = null,
    @Header("X-IBM-Migrated-Recall") xIBMMigratedRecall: XIBMMigratedRecall? = null,
    @Body body: ZOSCopyBody.FromFile,
    @Path("to-data-set-name") toDatasetName: String
  ): Call<Void>

  /**
   * **USS FILE** -> **PDS MEMBER**
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/ds/{to-data-set-name}({member-name})")
  fun copyToDatasetMemberFromUssFile(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-BPXK-AUTOCVT") xIBMBpxkAutoCvt: XIBMBpxkAutoCvt? = null,
    @Header("X-IBM-Migrated-Recall") xIBMMigratedRecall: XIBMMigratedRecall? = null,
    @Body body: ZOSCopyBody.FromFile,
    @Path("to-data-set-name") toDatasetName: String,
    @Path("member-name") memberName: String
  ): Call<Void>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/ds/{dataset-name}")
  fun recallMigratedDataset(
    @Header("Authorization") authorizationToken: String,
    @Body body: HRecallBody = HRecallBody(),
    @Path("dataset-name") datasetName: String
  ): Call<Void>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/ds/{dataset-name}")
  fun migrateDataset(
    @Header("Authorization") authorizationToken: String,
    @Body body: HMigrateBody = HMigrateBody(),
    @Path("dataset-name") datasetName: String
  ): Call<Void>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/ds/{dataset-name}")
  fun deleteMigratedDataset(
    @Header("Authorization") authorizationToken: String,
    @Body body: HDeleteBody = HDeleteBody(),
    @Path("dataset-name") datasetName: String
  ): Call<Void>

  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("zosmf/restfiles/fs")
  fun listUssPath(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-Max-Items") xIBMMaxItems: Int = 0,
    @Header("X-IBM-Lstat") xIBMLstat: Boolean = false,
    @Query("path") path: String,
    @Query("depth") depth: Int = 1,
    @Query("filesys") fileSystem: String? = null,
    @Query("symlinks") followSymlinks: SymlinkMode? = null,
    @Query("group") group: String? = null,
    @Query("mtime") mtime: String? = null,
    @Query("name") name: String? = null,
    @Query("size") size: String? = null,
    @Query("perm") perm: String? = null,
    @Query("type") type: String? = null,
    @Query("user") user: String? = null
  ): Call<ListUssPathResponse>

  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("/zosmf/restfiles/fs/{filepath-name}")
  fun retrieveUssFileContent(
    @Header("Authorization") authorizationToken: String,
    @Header("If-None-Match") ifNoneMatch: String? = null,
    @Header("Range") range: Int? = null,
    @Header("X-IBM-Record-Range") xIBMRecordRange: XIBMRecordRange? = null,
    @Header("X-IBM-Data-Type") xIBMDataType: XIBMDataType? = null,
    @Header("Accept-Encoding") acceptEncoding: String? = null, //"gzip",
    @Path("filepath-name", encoded = true) filePath: FilePath,
    @Query("search") search: String? = null,
    @Query("research") research: String? = null,
    @Query("insensitive") insensitive: Boolean? = null,
    @Query("maxreturnsize") maxReturnSize: Int? = null
  ): Call<ResponseBody>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/fs/{filepath-name}")
  fun writeToUssFile(
    @Header("Authorization") authorizationToken: String,
    @Header("If-Match") ifNoneMatch: String? = null,
    @Header("X-IBM-Data-Type") xIBMDataType: XIBMDataType? = null,
    @Header("Accept-Encoding") acceptEncoding: String? = null, //"gzip",
    @Header("Content-Type") contentType: String? = "text/plain", //"application/octet-stream",
    @Path("filepath-name", encoded = true) filePath: FilePath,
    @Body body: ByteArray
  ): Call<Void>

  @AvailableSince(ZVersion.ZOS_2_1)
  @POST("/zosmf/restfiles/fs/{filepath-name}")
  fun createUssFile(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-Override-Umask") xIBMOverrideUmask: Boolean = true,
    @Path("filepath-name", encoded = true) filePath: FilePath,
    @Body body: CreateUssFile
  ): Call<Void>

  @AvailableSince(ZVersion.ZOS_2_1)
  @DELETE("/zosmf/restfiles/fs/{filepath-name}")
  fun deleteUssFile(
    @Header("Authorization") authorizationToken: String,
    @Path("filepath-name", encoded = true) filePath: FilePath,
    @Header("X-IBM-Option") xIBMOption: XIBMOption? = null
  ): Call<Void>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/fs/{filepath-name}")
  fun changeFileMode(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-BPXK-AUTOCVT") xIBMBpxkAutoCvt: XIBMBpxkAutoCvt? = null,
    @Body body: ChangeMode,
    @Path("filepath-name") filePath: FilePath,
  ): Call<Void>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/fs/{filepath-name}")
  fun changeFileOwner(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-BPXK-AUTOCVT") xIBMBpxkAutoCvt: XIBMBpxkAutoCvt? = null,
    @Body body: ChangeOwner,
    @Path("filepath-name") filePath: FilePath,
  ): Call<Void>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/fs/{filepath-name}")
  fun changeFileTag(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-BPXK-AUTOCVT") xIBMBpxkAutoCvt: XIBMBpxkAutoCvt? = null,
    @Body body: ChangeTag,
    @Path("filepath-name") filePath: FilePath,
  ): Call<ResponseBody>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/fs/{filepath-name}")
  fun moveUssFile(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-BPXK-AUTOCVT") xIBMBpxkAutoCvt: XIBMBpxkAutoCvt? = null,
    @Body body: MoveUssFile,
    @Path("filepath-name") filePath: FilePath,
  ): Call<Void>

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/fs/{filepath-name}")
  fun copyUssFile(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-BPXK-AUTOCVT") xIBMBpxkAutoCvt: XIBMBpxkAutoCvt? = null,
    @Body body: USSCopyBody.FromFileOrDir,
    @Path("filepath-name", encoded = true) filePath: FilePath,
  ): Call<Void>

  /**
   * **SEQ** -> **USS FILE**
   *
   * **PDS MEMBER** -> **USS FILE**
   *
   * **WARNING:** PDS -> USS DIR doesn't work
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restfiles/fs/{filepath-name}")
  fun copyDatasetOrMemberToUss(
    @Header("Authorization") authorizationToken: String,
    @Header("X-IBM-BPXK-AUTOCVT") xIBMBpxkAutoCvt: XIBMBpxkAutoCvt? = null,
    @Body body: USSCopyBody.FromDataset,
    @Path("filepath-name") filePath: FilePath,
  ): Call<Void>

}