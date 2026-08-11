/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.CreateDatasetRequest
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.definitions.SshDatasetItem
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.definitions.SshDatasetType

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=subcommands-allocate-command">ALLOCATE command</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=command-allocate-operands">ALLOCATE command operands</a>
 * @property dsName data set name to allocate
 * @property volumeSerial volume serial to allocate the data set on (optional)
 * @property deviceType device type (optional, 3390 by default)
 * @property datasetOrganization data set organization (optional, because it is not needed for VSAM and GDG)
 * @property allocationUnit allocation unit (optional, because is not needed for VSAM)
 * @property primaryAllocation primary allocation
 * @property secondaryAllocation secondary allocation
 * @property directoryBlocks Directory blocks (only for PDS)
 * @property averageBlockLength average block size
 * @property recordFormat record format (is not needed for VSAM)
 * @property blockSize block size (for data sets to be allocated in blocks)
 * @property recordLength record length (not needed for U data sets)
 * @property storageClass storage class
 * @property managementClass management class
 * @property dataClass data class
 * @property dsnType data set type (could be omitted, the type then is specified automatically basing on the other parameters)
 * @property datasetModel model data set (if "allocate like" is triggered)
 */
class SshCreateDatasetRequest(
  override val connection: SshConnection,

  @property:AvailableSince(ZVersion.ZOS_2_2) override val dsName: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val volumeSerial: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val deviceType: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val datasetOrganization: SshDatasetItem.SshDatasetOrganization? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val allocationUnit: SshDatasetItem.SshSpaceUnits? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val primaryAllocation: Int,
  @property:AvailableSince(ZVersion.ZOS_2_2) val secondaryAllocation: Int,
  @property:AvailableSince(ZVersion.ZOS_2_2) val directoryBlocks: Int? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val averageBlockLength: Int? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val recordFormat: SshDatasetItem.SshRecordFormat? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val blockSize: Int? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val recordLength: Int? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val storageClass: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val managementClass: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val dataClass: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val dsnType: SshDatasetType? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val datasetModel: String? = null,
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

  override suspend fun produceResponseObject(clientResponse: Any): SshResponse {
    return SshCreateDatasetResponse(clientResponse as SshCmdResponse)
  }

}
