/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.datasets.definitions

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.data.DatasetItem
import java.lang.Exception

/** SSH dataset item, produced by SSH response handling functions */
class SshDatasetItem(
  @property:AvailableSince(ZVersion.ZOS_2_1) override val datasetName: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val isMigrated: Boolean? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val blockSize: Int? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) private val sshDatasetOrganization: SshDatasetOrganization? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val recordLength: Int? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) private val sshRecordFormat: SshRecordFormat? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val sizeInTracks: Int? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) private val sshSpaceUnits: SshSpaceUnits? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val volumeSerial: String? = null
) : DatasetItem {
  companion object {
    /**
     * Produce an [SshDatasetItem] from the map of data set attributes header to data set attribute values
     * @param dsAttrsHeaderToValues the map to produce the item from
     * @return the new entity, translating all related parameters respectively
     */
    fun produceFromMap(dsAttrsHeaderToValues: Map<String, String>): SshDatasetItem {
      val dsName = dsAttrsHeaderToValues.getOrDefault("NAME", "ERROR404")

      val dsOrg = dsAttrsHeaderToValues
        .getOrDefault("DSORG", null)
        .let {
          when (it) {
            "PS" -> SshDatasetOrganization.PS
            "PO" -> SshDatasetOrganization.PO
            "VSAM" -> SshDatasetOrganization.VSAM
            else -> null
          }
        }

      val recfm = dsAttrsHeaderToValues
        .getOrDefault("RECFM", null)
        .let { SshRecordFormat.getSshRecordFormatFromString(it ?: "", dsOrg) }

      val volser = dsAttrsHeaderToValues
        .getOrDefault("VOLUMES", null)
        ?.let { if (it.length >= 6) it else null }

      val spaceu = dsAttrsHeaderToValues
        .getOrDefault("SPACE_UNITS", "")
        .let {
          when (it) {
            "CYL" -> SshSpaceUnits.CYLINDERS
            "TRK" -> SshSpaceUnits.TRACKS
            "BLK" -> SshSpaceUnits.BLOCKS
            else -> null
          }
        }

      return SshDatasetItem(
        dsName,
        isMigrated = false,
        blockSize = dsAttrsHeaderToValues.getOrDefault("BLKSIZE", null)?.toIntOrNull(),
        sshDatasetOrganization = dsOrg,
        recordLength = dsAttrsHeaderToValues.getOrDefault("LRECL", null)?.toIntOrNull(),
        sshRecordFormat = recfm,
        sizeInTracks = dsAttrsHeaderToValues.getOrDefault("SIZE_IN_TRACKS", null)?.toIntOrNull(),
        sshSpaceUnits = spaceu,
        volumeSerial = volser
      )
    }
  }

  // TODO: GDG
  /**
   * SSH-compatible record format.
   * The format consists of separate conflicting RECFM parts.
   * They are separated basing on the ALLOC TSO command supported RECFM operand options
   * @see <"https://www.ibm.com/docs/en/zos/3.1.0?topic=command-allocate-operands">ALLOCATE command operands: RECFM</a>
   * @property recFmLength a record length format. Supported: F - fixed, V - varying, U - unknown, VSAM - for VSAM data sets
   * @property recFmBlocking a record blocking format. Supported: B - blocked, S - spanned
   * @property recFmControlChar a record control variable. Supported: A - ASCII control character, M - machine code control characters
   * @property hasVarLengthAscii indicates variable-length ASCII records
   * @property hasTrkOverflowWrite indicates the records can be written onto overflow tracks, if required (rarely used, but is there for compatibility)
   */
  class SshRecordFormat(
    val recFmLength: SshRecordFormatLength? = null,
    val recFmBlocking: SshRecordFormatBlocking? = null,
    val recFmControlChar: SshRecordFormatControlCharacter? = null,
    val hasVarLengthAscii: Boolean = false,
    val hasTrkOverflowWrite: Boolean = false
  ) {
    // Mutually exclusive
    enum class SshRecordFormatLength { F, V, U, VSAM }

    // Mutually exclusive
    enum class SshRecordFormatBlocking { B, S }

    // Mutually exclusive
    enum class SshRecordFormatControlCharacter { A, M }

    companion object {
      /** Return a formed SSH-compatible entity, parsed from a raw SSH response string */
      fun getSshRecordFormatFromString(rawRecFm: String, dsOrg: SshDatasetOrganization? = null): SshRecordFormat {
        val recFmLength = when {
          rawRecFm.contains("F") -> SshRecordFormatLength.F
          rawRecFm.contains("V") -> SshRecordFormatLength.V
          rawRecFm.contains("U") -> SshRecordFormatLength.U
          else -> if (dsOrg == SshDatasetOrganization.VSAM) SshRecordFormatLength.VSAM else null
        }
        val recFmBlocking = when {
          rawRecFm.contains("B") -> SshRecordFormatBlocking.B
          rawRecFm.contains("S") -> SshRecordFormatBlocking.S
          else -> null
        }
        val recFmControlChar = when {
          rawRecFm.contains("A") -> SshRecordFormatControlCharacter.A
          rawRecFm.contains("M") -> SshRecordFormatControlCharacter.M
          else -> null
        }
        val hasVarLengthAscii = rawRecFm.contains("D")
        val hasTrkOverflowWrite = rawRecFm.contains("T")
        return SshRecordFormat(recFmLength, recFmBlocking, recFmControlChar, hasVarLengthAscii, hasTrkOverflowWrite)
      }
    }

    /** Prepare a record format string for ALLOC TSO command */
    fun buildRecFmForAlloc(): String {
      return when (recFmLength) {
        null -> ""
        SshRecordFormatLength.VSAM -> ""
        SshRecordFormatLength.U -> " RECFM(U)"
        else -> StringBuilder(" RECFM($recFmLength")
          .append(if (recFmBlocking != null) ",$recFmBlocking" else "")
          .append(if (recFmControlChar != null) ",$recFmControlChar" else "")
          .append(if (hasVarLengthAscii) ",D" else "")
          .append(if (hasTrkOverflowWrite) ",T" else "")
          .append(")")
          .toString()
      }
    }

    /** Transform to a generic record format entity */
    fun toRecordFormat(): DatasetItem.RecordFormat = when (this.recFmLength) {
      SshRecordFormatLength.F -> {
        if (this.recFmBlocking == SshRecordFormatBlocking.B) DatasetItem.RecordFormat.FB
        else DatasetItem.RecordFormat.F
      }
      SshRecordFormatLength.V -> {
        if (this.recFmBlocking == SshRecordFormatBlocking.B) DatasetItem.RecordFormat.VB
        else DatasetItem.RecordFormat.V
      }
      SshRecordFormatLength.U -> DatasetItem.RecordFormat.U
      else -> DatasetItem.RecordFormat.VSAM
    }
  }

  /** SSH-compatible data set organization */
  enum class SshDatasetOrganization {
    DA,
    DAU,
    PO,
    POU,
    PS,
    PSU,
    VSAM;

    /** For now PO, PS and VSAM are the only ones that could be transformed to a generic data set organization */
    fun toDatasetOrganization(): DatasetItem.DatasetOrganization = when (this) {
      PS -> DatasetItem.DatasetOrganization.PS
      PO -> DatasetItem.DatasetOrganization.PO
      VSAM -> DatasetItem.DatasetOrganization.VS
      else -> throw Exception("Unsupported dataset organization: $this")
    }
  }

  /** SSH-compatible space units */
  enum class SshSpaceUnits {
    BLOCKS,
    CYLINDERS,
    TRACKS;

    fun toSpaceUnits(): DatasetItem.SpaceUnits = when (this) {
      BLOCKS -> DatasetItem.SpaceUnits.BLOCKS
      CYLINDERS -> DatasetItem.SpaceUnits.CYLINDERS
      TRACKS -> DatasetItem.SpaceUnits.TRACKS
    }
  }

  override val datasetOrganization: DatasetItem.DatasetOrganization?
    get() = sshDatasetOrganization?.toDatasetOrganization()

  override val recordFormat: DatasetItem.RecordFormat?
    get() = sshRecordFormat?.toRecordFormat()

  override val spaceUnits: DatasetItem.SpaceUnits?
    get() = sshSpaceUnits?.toSpaceUnits()
}






