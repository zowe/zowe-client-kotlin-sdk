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

package org.zowe.kotlinsdk.providers.zowe.ssh.datasets.definitions

import org.zowe.kotlinsdk.core.datasets.data.DatasetItem

/** SSH dataset item, produced by SSH response handling functions */
class SshDatasetItem(
  override val datasetName: String,
  override val isMigrated: Boolean? = null,
  override val blockSize: Int? = null,
  override val datasetOrganization: DatasetItem.DatasetOrganization? = null,
  override val recordLength: Int? = null,
  override val recordFormat: DatasetItem.RecordFormat? = null,
  override val sizeInTracks: Int? = null,
  override val spaceUnits: DatasetItem.SpaceUnits? = null,
  override val volumeSerial: String? = null
) : DatasetItem {
  companion object {
    /**
     * Produce an [SshDatasetItem] from the map of data set attributes header to data set attribute values
     * @param dsAttrsHeaderToValues the map to produce the item from
     * @return the new entity, translating all related parameters respectively
     */
    fun produceFromMap(dsAttrsHeaderToValues: Map<String, String>): SshDatasetItem {
      val dsName = dsAttrsHeaderToValues.getOrDefault("DSNAME", "ERROR404")

      val dsOrg = dsAttrsHeaderToValues
      .getOrDefault("DSORG", null)
      .let { it ->
        when (it) {
          "PS" -> DatasetItem.DatasetOrganization.PS
          "PO" -> DatasetItem.DatasetOrganization.PO
          "VSAM" -> DatasetItem.DatasetOrganization.VS
          else -> null
        }
      }

      val recfm = dsAttrsHeaderToValues
        .getOrDefault("RECFM", null)
        .let {
          when (it) {
            "F" -> DatasetItem.RecordFormat.F
            "FB" -> DatasetItem.RecordFormat.FB
            "V" -> DatasetItem.RecordFormat.V
            "VB" -> DatasetItem.RecordFormat.VB
            "U" -> DatasetItem.RecordFormat.U
            else -> if (dsOrg == DatasetItem.DatasetOrganization.VS) DatasetItem.RecordFormat.VSAM else null
          }
        }

      val volser = dsAttrsHeaderToValues
        .getOrDefault("VOLUMES", null)
        ?.let { if (it.length >= 6) it else null }

      val spaceu = dsAttrsHeaderToValues
        .getOrDefault("SPACE_UNITS", "")
        .let {
          when (it) {
            "CYL" -> DatasetItem.SpaceUnits.CYLINDERS
            "TRK" -> DatasetItem.SpaceUnits.TRACKS
            "BLK" -> DatasetItem.SpaceUnits.BLOCKS
            else -> null
          }
        }

      return SshDatasetItem(
        dsName,
        isMigrated = false,
        blockSize = dsAttrsHeaderToValues.getOrDefault("BLKSIZE", null)?.toIntOrNull(),
        datasetOrganization = dsOrg,
        recordLength = dsAttrsHeaderToValues.getOrDefault("LRECL", null)?.toIntOrNull(),
        recordFormat = recfm,
        sizeInTracks = dsAttrsHeaderToValues.getOrDefault("SIZE_IN_TRACKS", null)?.toIntOrNull(),
        spaceUnits = spaceu,
        volumeSerial = volser
      )
    }
  }
}






