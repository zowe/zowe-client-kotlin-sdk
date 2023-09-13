//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.restfiles.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.core.zosmf.restfiles.data.DatasetItem

// TODO: doc
internal class DatasetItemGson(
  @SerializedName("dsname")
  @Expose
  val datasetName: String,

  @SerializedName("blksz")
  @Expose
  val blockSizeRaw: String?,

  @SerializedName("catnm")
  @Expose
  val catalogName: String?,

  @SerializedName("cdate")
  @Expose
  val creationDate: String?,
  
  @SerializedName("dev")
  @Expose
  val deviceType: String?,

  @SerializedName("dsntp")
  @Expose
  val usedTracksOrPagesPercent: String?,

  @SerializedName("dsorg")
  @Expose
  val datasetOrganization: DatasetItem.DatasetOrganization?,

  @SerializedName("edate")
  @Expose
  val expirationDate: String?,

  @SerializedName("extx")
  @Expose
  val extendsUsedRaw: String?,

  @SerializedName("lrecl")
  @Expose
  val recordLengthRaw: String?,

  @SerializedName("migr")
  @Expose
  val isMigrated: DatasetItem.IsMigrated?,

  @SerializedName("mvol")
  @Expose
  val isMultipleVolumes: DatasetItem.IsMultipleVolumes?,

  @SerializedName("ovf")
  @Expose
  val spaceOverflowIndicator: String?,

  @SerializedName("rdate")
  @Expose
  val lastReferenceDate: String?,

  @SerializedName("recfm")
  @Expose
  val recordFormat: DatasetItem.RecordFormat?,

  @SerializedName("sizex")
  @Expose
  val sizeInTracks: Int?,

  @SerializedName("spaceu")
  @Expose
  val spaceUnits: DatasetItem.SpaceUnits?,

  @SerializedName("used")
  @Expose
  val used: String?,

  @SerializedName("vol")
  @Expose
  val volumeSerial: String?,

  @SerializedName("vols")
  @Expose
  val volumeSerials: String?,
) {
  private val blockSize: Int?
    get() = if (blockSizeRaw != null && !blockSizeRaw.isQuestion()) Integer.parseInt(blockSizeRaw) else null

  private val extendsUsed: Int?
    get() = if (extendsUsedRaw != null && !extendsUsedRaw.isQuestion()) Integer.parseInt(extendsUsedRaw) else null

  private val recordLength: Int?
    get() = if (recordLengthRaw != null && !recordLengthRaw.isQuestion()) Integer.parseInt(recordLengthRaw) else null

  private val usedTracksOrBlocks: Int?
    get() = if (used != null && !used.isQuestion()) Integer.parseInt(used) else null

  val converted: DatasetItem = DatasetItem(
    datasetName, 
    blockSize, 
    catalogName,
    creationDate,
    deviceType,
    usedTracksOrPagesPercent,
    datasetOrganization,
    expirationDate,
    extendsUsed,
    recordLength,
    isMigrated,
    isMultipleVolumes,
    spaceOverflowIndicator,
    lastReferenceDate,
    recordFormat,
    sizeInTracks,
    spaceUnits,
    usedTracksOrBlocks,
    volumeSerial,
    volumeSerials
  )

  private fun String?.isQuestion() : Boolean {
    return this == "?"
  }
  
}