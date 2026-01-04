/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.data.DatasetItem

// TODO: rework respectively according to SshDatasetItem
/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=zdsfri-json-document-specifications-zos-data-set-file-rest-interface-requests#RESTFILES_JSONDocumentSpecifications__itemkeypairsattributebase">JSON document specifications for z/OS data set and file REST interface requests: Data set list with attributes document</a>
 * @property datasetName dsname response param
 * @property zosmfBlockSize blksz response param
 * @property catalogName catnm response param
 * @property creationDate cdate response param
 * @property deviceType dev response param
 * @property usedTracksOrPagesPercent dsntp response param
 * @property zosmfDatasetOrganization dsorg response param
 * @property expirationDate edate response param
 * @property zosmfExtentsUsed extx response param
 * @property zosmfRecordLength lrecl
 * @property zosmfIsMigrated migr response param
 * @property zosmfIsMultipleVolumes mvol response param
 * @property spaceOverflowIndicator ovf response param
 * @property lastReferenceDate rdate response param
 * @property zosmfRecordFormat recfm response param
 * @property zosmfSizeInTracks sizex response param
 * @property zosmfSpaceUnits spacu response param
 * @property zosmfUsedTracksOrBlocks used response param
 * @property volumeSerial vol response param
 * @property volumeSerials vols response param
 */
@Serializable
class ZosmfDatasetItem(
  @SerialName("dsname")
  @property:AvailableSince(ZVersion.ZOS_2_1) override val datasetName: String,

  @SerialName("blksz")
  @property:AvailableSince(ZVersion.ZOS_2_1) private val zosmfBlockSize: String? = null,

  @SerialName("catnm")
  @property:AvailableSince(ZVersion.ZOS_2_1) val catalogName: String? = null,

  @SerialName("cdate")
  @property:AvailableSince(ZVersion.ZOS_2_1) val creationDate: String? = null,

  @SerialName("dev")
  @property:AvailableSince(ZVersion.ZOS_2_1) val deviceType: String? = null,

  @SerialName("dsntp")
  @property:AvailableSince(ZVersion.ZOS_2_1) val usedTracksOrPagesPercent: String? = null,

  @SerialName("dsorg")
  @property:AvailableSince(ZVersion.ZOS_2_1) private val zosmfDatasetOrganization: ZosmfDatasetOrganization? = null,

  @SerialName("edate")
  @property:AvailableSince(ZVersion.ZOS_2_1) val expirationDate: String? = null,

  @SerialName("extx")
  @property:AvailableSince(ZVersion.ZOS_2_1) private val zosmfExtentsUsed: String? = null,

  @SerialName("lrecl")
  @property:AvailableSince(ZVersion.ZOS_2_1) private val zosmfRecordLength: String? = null,

  @SerialName("migr")
  @property:AvailableSince(ZVersion.ZOS_2_1) private val zosmfIsMigrated: ZosmfIsMigrated? = null,

  @SerialName("mvol")
  @property:AvailableSince(ZVersion.ZOS_2_1) private val zosmfIsMultipleVolumes: ZosmfIsMultipleVolumes? = null,

  @SerialName("ovf")
  @property:AvailableSince(ZVersion.ZOS_2_1) val spaceOverflowIndicator: String? = null,

  @SerialName("rdate")
  @property:AvailableSince(ZVersion.ZOS_2_1) val lastReferenceDate: String? = null,

  @SerialName("recfm")
  @property:AvailableSince(ZVersion.ZOS_2_1) private val zosmfRecordFormat: ZosmfRecordFormat? = null,

  @SerialName("sizex")
  @property:AvailableSince(ZVersion.ZOS_2_1) private val zosmfSizeInTracks: Int? = null,

  @SerialName("spacu")
  @property:AvailableSince(ZVersion.ZOS_2_1) private val zosmfSpaceUnits: ZosmfSpaceUnits? = null,

  @SerialName("used")
  @property:AvailableSince(ZVersion.ZOS_2_1) private val zosmfUsedTracksOrBlocks: String? = null,

  @SerialName("vol")
  @property:AvailableSince(ZVersion.ZOS_2_1) override val volumeSerial: String? = null,

  @SerialName("vols")
  @property:AvailableSince(ZVersion.ZOS_2_3) val volumeSerials: String? = null,
) : DatasetItem {

  @Serializable
  enum class ZosmfIsMultipleVolumes {
    @SerialName("Y") Y,
    @SerialName("N") N;

    fun toPlain(): Boolean = when (this) {
      Y -> true
      N -> false
    }
  }

  /** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=set-dsorg-recfm-lrecl-blksize-operands">DSORG, RECFM, LRECL, and BLKSIZE operands</a> */
  @Serializable
  enum class ZosmfDatasetOrganization {
    @SerialName("PO") PO,
    @SerialName("PO-E") POE,
    @SerialName("PS") PS,
    @SerialName("VS") VS;

    fun toDatasetOrganization(): DatasetItem.DatasetOrganization = when (this) {
      PO -> DatasetItem.DatasetOrganization.PO
      POE -> DatasetItem.DatasetOrganization.POE
      PS -> DatasetItem.DatasetOrganization.PS
      VS -> DatasetItem.DatasetOrganization.VS
    }
  }

  @Serializable
  enum class ZosmfIsMigrated {
    @SerialName("YES") YES,
    @SerialName("NO") NO;

    fun toPlain(): Boolean = when (this) {
      YES -> true
      NO -> false
    }
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=statement-recfm-parameter">RECFM parameter</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=set-dsorg-recfm-lrecl-blksize-operands">DSORG, RECFM, LRECL, and BLKSIZE operands</a>
   */
  @Serializable
  enum class ZosmfRecordFormat {
    @SerialName("F") F,
    @SerialName("FA") FA,
    @SerialName("FM") FM,
    @SerialName("FS") FS,
    @SerialName("FT") FT,
    @SerialName("FB") FB,
    @SerialName("FBA") FBA,
    @SerialName("FBM") FBM,
    @SerialName("FBS") FBS,
    @SerialName("FBT") FBT,
    @SerialName("V") V,
    @SerialName("VA") VA,
    @SerialName("VM") VM,
    @SerialName("VS") VS,
    @SerialName("VT") VT,
    @SerialName("VB") VB,
    @SerialName("VBA") VBA,
    @SerialName("VBM") VBM,
    @SerialName("VBS") VBS,
    @SerialName("VBT") VBT,
    @SerialName("VBST") VBST,
    @SerialName("U") U,
    @SerialName("UT") UT,
    @SerialName("D") D,
    @SerialName("DS") DS,
    @SerialName("DB") DB,
    @SerialName("DBS") DBS,
    @SerialName("?") VSAM;

    fun toRecordFormat(): DatasetItem.RecordFormat = when (this) {
      F -> DatasetItem.RecordFormat.F
      FA -> DatasetItem.RecordFormat.F
      FM -> DatasetItem.RecordFormat.F
      FS -> DatasetItem.RecordFormat.F
      FT -> DatasetItem.RecordFormat.F
      FB -> DatasetItem.RecordFormat.FB
      FBA -> DatasetItem.RecordFormat.FB
      FBM -> DatasetItem.RecordFormat.FB
      FBS -> DatasetItem.RecordFormat.FB
      FBT -> DatasetItem.RecordFormat.FB
      V -> DatasetItem.RecordFormat.V
      VA -> DatasetItem.RecordFormat.V
      VM -> DatasetItem.RecordFormat.V
      VS -> DatasetItem.RecordFormat.V
      VT -> DatasetItem.RecordFormat.V
      VB -> DatasetItem.RecordFormat.VB
      VBA -> DatasetItem.RecordFormat.VB
      VBM -> DatasetItem.RecordFormat.VB
      VBS -> DatasetItem.RecordFormat.VB
      VBT -> DatasetItem.RecordFormat.VB
      VBST -> DatasetItem.RecordFormat.VB
      U -> DatasetItem.RecordFormat.U
      UT -> DatasetItem.RecordFormat.U
      D -> DatasetItem.RecordFormat.U
      DS -> DatasetItem.RecordFormat.U
      DB -> DatasetItem.RecordFormat.U
      DBS -> DatasetItem.RecordFormat.U
      VSAM -> DatasetItem.RecordFormat.VSAM
    }
  }

  @Serializable
  enum class ZosmfSpaceUnits {
    @SerialName("TRACKS") TRACKS,
    @SerialName("BLOCKS") BLOCKS,
    @SerialName("CYLINDERS") CYLINDERS,
    @SerialName("BYTES") BYTES,
    @SerialName("KILOBYTES") KILOBYTES,
    @SerialName("MEGABYTES") MEGABYTES;

    fun toSpaceUnits(): DatasetItem.SpaceUnits = when (this) {
      TRACKS -> DatasetItem.SpaceUnits.TRACKS
      BLOCKS -> DatasetItem.SpaceUnits.BLOCKS
      CYLINDERS -> DatasetItem.SpaceUnits.CYLINDERS
      BYTES -> DatasetItem.SpaceUnits.BYTES
      KILOBYTES -> DatasetItem.SpaceUnits.KILOBYTES
      MEGABYTES -> DatasetItem.SpaceUnits.MEGABYTES
    }
  }

  override val blockSize: Int?
    get() = intOrNullFromQuestion(zosmfBlockSize)

  override val datasetOrganization: DatasetItem.DatasetOrganization?
    get() = zosmfDatasetOrganization?.toDatasetOrganization()

  val extentsUsed = intOrNullFromQuestion(zosmfExtentsUsed)

  override val recordLength: Int?
    get() = intOrNullFromQuestion(zosmfRecordLength)

  override val isMigrated: Boolean?
    get() = zosmfIsMigrated?.toPlain()

  val isMultipleVolumes = zosmfIsMultipleVolumes?.toPlain()

  override val recordFormat: DatasetItem.RecordFormat?
    get() = zosmfRecordFormat?.toRecordFormat()

  override val sizeInTracks: Int?
    get() = zosmfSizeInTracks

  override val spaceUnits: DatasetItem.SpaceUnits?
    get() = zosmfSpaceUnits?.toSpaceUnits()

  val usedTracksOrBlocks = intOrNullFromQuestion(zosmfUsedTracksOrBlocks)

}
