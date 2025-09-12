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

package org.zowe.kotlinsdk.providers.zowe.ssh.datasets.messaging

import net.schmizz.sshj.SSHClient
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.CreateDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.SshConnection
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.datasets.definitions.SshDatasetItem
import org.zowe.kotlinsdk.providers.zowe.ssh.datasets.definitions.SshDatasetType

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=subcommands-allocate-command">ALLOCATE command</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=command-allocate-operands">ALLOCATE command operands</a>
 */
class SshCreateDatasetRequest(
  override val connection: SshConnection,

  /** Data set name to allocate */
  @property:AvailableSince(ZVersion.ZOS_2_1) override val dsName: String,

  /** Volume serial to allocate the data set on (optional) */
  @property:AvailableSince(ZVersion.ZOS_2_1) val volumeSerial: String? = null,

  /** Device type (optional, 3390 by default) */
  @property:AvailableSince(ZVersion.ZOS_2_1) val deviceType: String? = null,

  /** Data set organization (optional, because it is not needed for VSAM and GDG) */
  @property:AvailableSince(ZVersion.ZOS_2_1) val datasetOrganization: SshDatasetItem.SshDatasetOrganization? = null,

  /** Allocation unit (optional, because is not needed for VSAM) */
  @property:AvailableSince(ZVersion.ZOS_2_1) val allocationUnit: SshDatasetItem.SshSpaceUnits? = null,

  /** Primary allocation */
  @property:AvailableSince(ZVersion.ZOS_2_1) val primaryAllocation: Int,

  /** Secondary allocation */
  @property:AvailableSince(ZVersion.ZOS_2_1) val secondaryAllocation: Int,

  /** Directory blocks (only for PDS) */
  @property:AvailableSince(ZVersion.ZOS_2_1) val directoryBlocks: Int? = null,

  /** Average block size */
  @property:AvailableSince(ZVersion.ZOS_2_1) val averageBlockLength: Int? = null,

  /** Record format (is not needed for VSAM) */
  @property:AvailableSince(ZVersion.ZOS_2_1) val recordFormat: SshDatasetItem.SshRecordFormat? = null,

  /** Block size (for data sets to be allocated in blocks) */
  @property:AvailableSince(ZVersion.ZOS_2_1) val blockSize: Int? = null,

  /** Record length (not needed for U data sets) */
  @property:AvailableSince(ZVersion.ZOS_2_1) val recordLength: Int? = null,

  /** Storage class */
  @property:AvailableSince(ZVersion.ZOS_2_1) val storageClass: String? = null,

  /** Management class */
  @property:AvailableSince(ZVersion.ZOS_2_1) val managementClass: String? = null,

  /** Data class */
  @property:AvailableSince(ZVersion.ZOS_2_1) val dataClass: String? = null,

  /** Data set type (could be omitted, the type then is specified automatically basing on the other parameters) */
  @property:AvailableSince(ZVersion.ZOS_2_1) val dsnType: SshDatasetType? = null,

  /** Model data set (if "allocate like" is triggered) */
  @property:AvailableSince(ZVersion.ZOS_2_1) val datasetModel: String? = null,
) : SshRequest, CreateDatasetRequest {

  override var sshCommand = StringBuilder("tsocmd \"ALLOC DA('$dsName') NEW")
    .append(if (volumeSerial.isNullOrBlank()) "" else " VOLUME($volumeSerial)")
    .append(if (deviceType.isNullOrBlank()) "" else " UNIT($deviceType)")
    .append(if (datasetOrganization == null) "" else " DSORG($datasetOrganization)")
    .append(if (allocationUnit == null || allocationUnit == SshDatasetItem.SshSpaceUnits.BLOCKS) "" else " $allocationUnit")
    .append(" SPACE($primaryAllocation, $secondaryAllocation)")
    .append(if (directoryBlocks == null) "" else " DIR($directoryBlocks)")
    .append(if (averageBlockLength == null) "" else " AVGBLOCK($averageBlockLength)")
    .append(recordFormat?.buildRecFmForAlloc() ?: "")
    .append(if (blockSize == null) "" else " BLKSIZE($blockSize)")
    .append(if (recordLength == null) "" else " LRECL($recordLength)")
    .append(if (storageClass == null) "" else " STORCLAS($storageClass)")
    .append(if (managementClass == null) "" else " MGMTCLAS($managementClass)")
    .append(if (dataClass == null) "" else " DATACLAS($dataClass)")
    .append(dsnType?.buildDsnTypeForAlloc() ?: "")
    .append(if (datasetModel == null) "" else " LIKE('$datasetModel')")
    .append("\"")
    .toString()

  /**
   * Perform a data set creation operation through SSH.
   * Will form all the necessary parameters for the "ALLOC DA('DSN') NEW" TSO command basing on the provided info
   * @param client the SSH client to execute the command with
   * @return the [SshCreateDatasetResponse] with the resulting status of the command
   */
  override fun execRequest(client: SSHClient): SshResponse {
    client
      .startSession()
      .use {
        val status = performSshRequest(client, it)
        return SshCreateDatasetResponse(status)
      }
  }

}
