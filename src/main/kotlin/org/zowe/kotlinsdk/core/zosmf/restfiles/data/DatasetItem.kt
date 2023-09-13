//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.zosmf.restfiles.data

import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://ibm.com/docs/en/zos/2.5.0?topic=zdsfri-json-document-specifications-zos-data-set-file-rest-interface-requests#RESTFILES_JSONDocumentSpecifications__dataSetListWithAttributes__title__1
 */
class DatasetItem(
  /** dsname response param */
  @AvailableSince(ZVersion.ZOS_2_1) val datasetName: String = "",

  /** blksz response param */
  @AvailableSince(ZVersion.ZOS_2_1) val blockSize: Int? = null,

  /** catnm response param */
  @AvailableSince(ZVersion.ZOS_2_1) val catalogName: String? = null,

  /** cdate response param */
  @AvailableSince(ZVersion.ZOS_2_1) val creationDate: String? = null,

  /** dev response param */
  @AvailableSince(ZVersion.ZOS_2_1) val deviceType: String? = null,

  /** dsntp response param */
  @AvailableSince(ZVersion.ZOS_2_1) val usedTracksOrPagesPercent: String? = null,

  /** dsorg response param */
  @AvailableSince(ZVersion.ZOS_2_1) val datasetOrganization: DatasetOrganization? = null,

  /** edate response param */
  @AvailableSince(ZVersion.ZOS_2_1) val expirationDate: String? = null,

  /** extx response param */
  @AvailableSince(ZVersion.ZOS_2_1) val extendsUsed: Int? = null,

  /** lrecl response param */
  @AvailableSince(ZVersion.ZOS_2_1) val recordLength: Int? = null,

  /** migr response param */
  @AvailableSince(ZVersion.ZOS_2_1) val isMigrated: IsMigrated? = null,

  /** mvol response param */
  @AvailableSince(ZVersion.ZOS_2_1) val isMultipleVolumes: IsMultipleVolumes? = null,

  /** ovf response param */
  @AvailableSince(ZVersion.ZOS_2_1) val spaceOverflowIndicator: String? = null,

  /** rdate response param */
  @AvailableSince(ZVersion.ZOS_2_1) val lastReferenceDate: String? = null,

  /** recfm response param */
  @AvailableSince(ZVersion.ZOS_2_1) val recordFormat: RecordFormat? = null,

  /** sizex response param */
  @AvailableSince(ZVersion.ZOS_2_1) val sizeInTracks: Int? = null,

  /** spaceu response param */
  @AvailableSince(ZVersion.ZOS_2_1) val spaceUnits: SpaceUnits? = null,

  /** used response param */
  @AvailableSince(ZVersion.ZOS_2_1) val userTracksOrBlocks: Int? = null,

  /** vol response param */
  @AvailableSince(ZVersion.ZOS_2_1) val volumeSerial: String? = null,

  /** vols response param */
  @AvailableSince(ZVersion.ZOS_2_3) val volumeSerials: String? = null,
) {

  enum class DatasetOrganization(private val type: String) {
    @SerializedName("PO")
    PO("PO"),
    @SerializedName("PO-E")
    POE("PO-E"),
    @SerializedName("PS")
    PS("PS"),
    @SerializedName("VS")
    VS("VS");

    override fun toString(): String {
      return type
    }
  }

  interface HasBooleanValue {
    val value: Boolean
  }

  enum class IsMigrated(override val value: Boolean) : HasBooleanValue {
    @SerializedName("YES")
    YES(true),

    @SerializedName("NO")
    NO(false)
  }

  enum class IsMultipleVolumes(override val value: Boolean) : HasBooleanValue {
    @SerializedName("Y")
    Y(true),

    @SerializedName("N")
    N(false)
  }

  enum class RecordFormat(private val type: String) {
    @SerializedName("F")
    F("F"),
    @SerializedName("FB")
    FB("FB"),
    @SerializedName("V")
    V("V"),
    @SerializedName("VB")
    VB("VB"),
    @SerializedName("U")
    U("U"),
    @SerializedName("?")
    VSAM("VSAM"),
    @SerializedName("VA")
    VA("VA");

    override fun toString(): String {
      return type
    }
  }

  enum class SpaceUnits {
    @SerializedName("TRACKS")
    TRACKS,

    @SerializedName("BLOCKS")
    BLOCKS,

    @SerializedName("CYLINDERS")
    CYLINDERS,

    @SerializedName("BYTES")
    BYTES,

    @SerializedName("KILOBYTES")
    KILOBYTES,

    @SerializedName("MEGABYTES")
    MEGABYTES
  }
}