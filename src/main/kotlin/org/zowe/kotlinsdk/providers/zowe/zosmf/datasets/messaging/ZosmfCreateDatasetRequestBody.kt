/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.ZosmfDatasetItem
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.ZosmfDatasetType

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-create-sequential-partitioned-data-set#CreateDataSet__title__2">Create a sequential or partitioned data set: Request Body</a>
 * @property volumeSerial volume serial
 * @property deviceType device type
 * @property datasetOrganization data set organization
 * @property allocationUnit unit of space allocation
 * @property primaryAllocation primary space allocation
 * @property secondaryAllocation secondary space allocation
 * @property directoryBlocks number of directory blocks
 * @property averageBlockLength average block size
 * @property recordFormat record format
 * @property blockSize block size
 * @property recordLength record length
 * @property storageClass storage class
 * @property managementClass management class
 * @property dataClass data class
 * @property dsnType data set type
 * @property datasetModel model data set name
 */
@Serializable
data class ZosmfCreateDatasetRequestBody(
  @SerialName("volser")
  @property:AvailableSince(ZVersion.ZOS_2_1) val volumeSerial: String? = null,

  @SerialName("unit")
  @property:AvailableSince(ZVersion.ZOS_2_1) val deviceType: String? = null,

  @SerialName("dsorg")
  @property:AvailableSince(ZVersion.ZOS_2_1) val datasetOrganization: ZosmfDatasetItem.ZosmfDatasetOrganization,

  @SerialName("alcunit")
  @property:AvailableSince(ZVersion.ZOS_2_1) val allocationUnit: ZosmfAllocationUnit? = null,

  @SerialName("primary")
  @property:AvailableSince(ZVersion.ZOS_2_1) val primaryAllocation: Int,

  @SerialName("secondary")
  @property:AvailableSince(ZVersion.ZOS_2_1) val secondaryAllocation: Int,

  @SerialName("dirblk")
  @property:AvailableSince(ZVersion.ZOS_2_1) val directoryBlocks: Int? = null,

  @SerialName("avgblk")
  @property:AvailableSince(ZVersion.ZOS_2_1) val averageBlockLength: Int? = null,

  @SerialName("recfm")
  @property:AvailableSince(ZVersion.ZOS_2_1) val recordFormat: ZosmfDatasetItem.ZosmfRecordFormat,

  @SerialName("blksize")
  @property:AvailableSince(ZVersion.ZOS_2_1) val blockSize: Int? = null,

  @SerialName("lrecl")
  @property:AvailableSince(ZVersion.ZOS_2_1) val recordLength: Int? = null,

  @SerialName("storeclass")
  @property:AvailableSince(ZVersion.ZOS_2_1) val storageClass: String? = null,

  @SerialName("mgntclass")
  @property:AvailableSince(ZVersion.ZOS_2_1) val managementClass: String? = null,

  @SerialName("dataclass")
  @property:AvailableSince(ZVersion.ZOS_2_1) val dataClass: String? = null,

  @SerialName("dsntype")
  @property:AvailableSince(ZVersion.ZOS_2_3) val dsnType: ZosmfDatasetType? = null,

  @SerialName("like")
  @property:AvailableSince(ZVersion.ZOS_2_4) val datasetModel: String? = null
) {
  @Serializable
  enum class ZosmfAllocationUnit {
    @SerialName("TRK") TRK,
    @SerialName("CYL") CYL
  }
}
