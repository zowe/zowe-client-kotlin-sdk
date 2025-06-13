/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.data.DatasetItem

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=zdsfri-json-document-specifications-zos-data-set-file-rest-interface-requests#RESTFILES_JSONDocumentSpecifications__itemkeypairsattributebase">JSON document specifications for z/OS data set and file REST interface requests: Data set list with attributes document</a> */
@Serializable
class ZosmfDatasetItem(
  /** dsname response param */
  @SerialName("dsname")
  @AvailableSince(ZVersion.ZOS_2_1) override val datasetName: String,

  /** blksz response param */
  @SerialName("blksz")
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfBlockSize: String? = null,

  /** catnm response param */
  @SerialName("catnm")
  @AvailableSince(ZVersion.ZOS_2_1) val catalogName: String? = null,

  /** cdate response param */
  @SerialName("cdate")
  @AvailableSince(ZVersion.ZOS_2_1) val creationDate: String? = null,

  /** dev response param */
  @SerialName("dev")
  @AvailableSince(ZVersion.ZOS_2_1) val deviceType: String? = null,

  /** dsntp response param */
  @SerialName("dsntp")
  @AvailableSince(ZVersion.ZOS_2_1) val usedTracksOrPagesPercent: String? = null,

  /** dsorg response param */
  @SerialName("dsorg")
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfDatasetOrganization: ZosmfDatasetOrganization? = null,

  /** edate response param */
  @SerialName("edate")
  @AvailableSince(ZVersion.ZOS_2_1) val expirationDate: String? = null,

  /** extx response param */
  @SerialName("extx")
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfExtentsUsed: String? = null,

  /** lrecl response param */
  @SerialName("lrecl")
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfRecordLength: String? = null,

  /** migr response param */
  @SerialName("migr")
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfIsMigrated: ZosmfIsMigrated? = null,

  /** mvol response param */
  @SerialName("mvol")
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfIsMultipleVolumes: ZosmfIsMultipleVolumes? = null,

  /** ovf response param */
  @SerialName("ovf")
  @AvailableSince(ZVersion.ZOS_2_1) val spaceOverflowIndicator: String? = null,

  /** rdate response param */
  @SerialName("rdate")
  @AvailableSince(ZVersion.ZOS_2_1) val lastReferenceDate: String? = null,

  /** recfm response param */
  @SerialName("recfm")
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfRecordFormat: ZosmfRecordFormat? = null,

  /** sizex response param */
  @SerialName("sizex")
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfSizeInTracks: Int? = null,

  /** spaceu response param */
  @SerialName("spaceu")
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfSpaceUnits: ZosmfSpaceUnits? = null,

  /** used response param */
  @SerialName("used")
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfUsedTracksOrBlocks: String? = null,

  /** vol response param */
  @SerialName("vol")
  @AvailableSince(ZVersion.ZOS_2_1) override val volumeSerial: String? = null,

  /** vols response param */
  @SerialName("vols")
  @AvailableSince(ZVersion.ZOS_2_3) val volumeSerials: String? = null,
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
