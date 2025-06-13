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

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.ZosmfDatasetItem
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.ZosmfDatasetType

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-create-sequential-partitioned-data-set#CreateDataSet__title__2">Create a sequential or partitioned data set: Request Body</a> */
@Serializable
data class ZosmfCreateDatasetRequestBody(
  /** Volume serial */
  @SerialName("volser")
  @AvailableSince(ZVersion.ZOS_2_1) val volumeSerial: String? = null,

  /** Device type */
  @SerialName("unit")
  @AvailableSince(ZVersion.ZOS_2_1) val deviceType: String? = null,

  /** Dataset organization */
  @SerialName("dsorg")
  @AvailableSince(ZVersion.ZOS_2_1) val datasetOrganization: ZosmfDatasetItem.ZosmfDatasetOrganization,

  /** Unit of space allocation */
  @SerialName("alcunit")
  @AvailableSince(ZVersion.ZOS_2_1) val allocationUnit: ZosmfAllocationUnit? = null,

  /** Primary space allocation */
  @SerialName("primary")
  @AvailableSince(ZVersion.ZOS_2_1) val primaryAllocation: Int,

  /** Secondary space allocation */
  @SerialName("secondary")
  @AvailableSince(ZVersion.ZOS_2_1) val secondaryAllocation: Int,

  /** Number of directory blocks */
  @SerialName("dirblk")
  @AvailableSince(ZVersion.ZOS_2_1) val directoryBlocks: Int? = null,

  /** Average block size */
  @SerialName("avgblk")
  @AvailableSince(ZVersion.ZOS_2_1) val averageBlockLength: Int? = null,

  /** Record format */
  @SerialName("recfm")
  @AvailableSince(ZVersion.ZOS_2_1) val recordFormat: ZosmfDatasetItem.ZosmfRecordFormat,

  /** Block size */
  @SerialName("blksize")
  @AvailableSince(ZVersion.ZOS_2_1) val blockSize: Int? = null,

  /** Record length */
  @SerialName("lrecl")
  @AvailableSince(ZVersion.ZOS_2_1) val recordLength: Int? = null,

  /** Storage class */
  @SerialName("storeclass")
  @AvailableSince(ZVersion.ZOS_2_1) val storageClass: String? = null,

  /** Management class */
  @SerialName("mgntclass")
  @AvailableSince(ZVersion.ZOS_2_1) val managementClass: String? = null,

  /** Data class */
  @SerialName("dataclass")
  @AvailableSince(ZVersion.ZOS_2_1) val dataClass: String? = null,

  /** Dataset type */
  @SerialName("dsntype")
  @AvailableSince(ZVersion.ZOS_2_3) val dsnType: ZosmfDatasetType? = null,

  /** Model dataset name */
  @SerialName("like")
  @AvailableSince(ZVersion.ZOS_2_4) val datasetModel: String? = null
) {
  @Serializable
  enum class ZosmfAllocationUnit {
    @SerialName("TRK") TRK,
    @SerialName("CYL") CYL
  }
}
