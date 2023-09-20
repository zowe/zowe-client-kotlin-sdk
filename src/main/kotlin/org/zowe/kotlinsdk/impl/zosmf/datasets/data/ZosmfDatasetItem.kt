//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.datasets.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.data.DatasetItem

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://ibm.com/docs/en/zos/2.5.0?topic=zdsfri-json-document-specifications-zos-data-set-file-rest-interface-requests#RESTFILES_JSONDocumentSpecifications__dataSetListWithAttributes__title__1
 */
class ZosmfDatasetItem(
  /** dsname response param */
  @SerializedName("dsname")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfDatasetName: String,

  /** migr response param */
  @SerializedName("migr")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfIsMigrated: ZosmfIsMigrated? = null,

  /** blksz response param */
  @SerializedName("blksz")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfBlockSize: String? = null,

  /** dsorg response param */
  @SerializedName("dsorg")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfDatasetOrganization: ZosmfDatasetOrganization? = null,

  /** lrecl response param */
  @SerializedName("lrecl")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfRecordLength: String? = null,

  /** recfm response param */
  @SerializedName("recfm")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfRecordFormat: ZosmfRecordFormat? = null,

  /** sizex response param */
  @SerializedName("sizex")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfSizeInTracks: Int? = null,

  /** spaceu response param */
  @SerializedName("spaceu")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfSpaceUnits: ZosmfSpaceUnits? = null,

  /** vol response param */
  @SerializedName("vol")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfVolumeSerial: String? = null,

  /** extx response param */
  @SerializedName("extx")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfExtentsUsed: String? = null,

  /** mvol response param */
  @SerializedName("mvol")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfIsMultipleVolumes: ZosmfIsMultipleVolumes? = null,

  /** used response param */
  @SerializedName("used")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfUsedTracksOrBlocks: String? = null,

  /** catnm response param */
  @SerializedName("catnm")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val catalogName: String? = null,

  /** cdate response param */
  @SerializedName("cdate")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val creationDate: String? = null,

  /** dev response param */
  @SerializedName("dev")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val deviceType: String? = null,

  /** dsntp response param */
  @SerializedName("dsntp")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val usedTracksOrPagesPercent: String? = null,

  /** edate response param */
  @SerializedName("edate")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val expirationDate: String? = null,

  /** ovf response param */
  @SerializedName("ovf")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val spaceOverflowIndicator: String? = null,

  /** rdate response param */
  @SerializedName("rdate")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val lastReferenceDate: String? = null,

  /** vols response param */
  @SerializedName("vols")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_3) val volumeSerials: String? = null,
) : DatasetItem(
  datasetName = zosmfDatasetName,
  isMigrated = zosmfIsMigrated?.value,
  blockSize = intOrNullFromQuestion(zosmfBlockSize),
  datasetOrganization = zosmfDatasetOrganization?.value,
  recordLength = intOrNullFromQuestion(zosmfRecordLength),
  recordFormat = zosmfRecordFormat?.value,
  sizeInTracks = zosmfSizeInTracks,
  spaceUnits = zosmfSpaceUnits?.value,
  volumeSerial = zosmfVolumeSerial
) {
  val extentsUsed = intOrNullFromQuestion(zosmfExtentsUsed)
  val isMultipleVolumes = zosmfIsMultipleVolumes?.value
  val usedTracksOrBlocks = intOrNullFromQuestion(zosmfUsedTracksOrBlocks)

  interface BooleanValue {
    val value: Boolean
  }

  enum class ZosmfIsMigrated(override val value: Boolean) : BooleanValue {
    @SerializedName("YES")
    YES(true),

    @SerializedName("NO")
    NO(false)
  }

  interface DatasetOrganizationValue {
    val value: DatasetOrganization
  }

  enum class ZosmfDatasetOrganization(override val value: DatasetOrganization) : DatasetOrganizationValue {
    @SerializedName("PO")
    PO(DatasetOrganization.PO),

    @SerializedName("PO-E")
    POE(DatasetOrganization.POE),

    @SerializedName("PS")
    PS(DatasetOrganization.PS),

    @SerializedName("VS")
    VS(DatasetOrganization.VS)
  }

  interface RecordFormatValue {
    val value: RecordFormat
  }

  enum class ZosmfRecordFormat(override val value: RecordFormat) : RecordFormatValue {
    @SerializedName("F")
    F(RecordFormat.F),

    @SerializedName("FB")
    FB(RecordFormat.FB),

    @SerializedName("V")
    V(RecordFormat.V),

    @SerializedName("VB")
    VB(RecordFormat.VB),

    @SerializedName("U")
    U(RecordFormat.U),

    @SerializedName("?")
    VSAM(RecordFormat.VSAM),

    @SerializedName("VA")
    VA(RecordFormat.VA)
  }

  interface SpaceUnitsValue {
    val value: SpaceUnits
  }

  enum class ZosmfSpaceUnits(override val value: SpaceUnits) : SpaceUnitsValue {
    @SerializedName("TRACKS")
    TRACKS(SpaceUnits.TRACKS),

    @SerializedName("BLOCKS")
    BLOCKS(SpaceUnits.BLOCKS),

    @SerializedName("CYLINDERS")
    CYLINDERS(SpaceUnits.CYLINDERS),

    @SerializedName("BYTES")
    BYTES(SpaceUnits.BYTES),

    @SerializedName("KILOBYTES")
    KILOBYTES(SpaceUnits.KILOBYTES),

    @SerializedName("MEGABYTES")
    MEGABYTES(SpaceUnits.MEGABYTES)
  }

  enum class ZosmfIsMultipleVolumes(override val value: Boolean) : BooleanValue {
    @SerializedName("Y")
    Y(true),

    @SerializedName("N")
    N(false)
  }
}
