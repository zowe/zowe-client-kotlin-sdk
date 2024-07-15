// Copyright (c) 2024 IBA Group.
//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Contributors:
//   IBA Group
//   Zowe Community

package org.zowe.kotlinsdk.impl.zosmf.datasets.api.messaging

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.AllocationUnit
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.DatasetOrganization
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.DatasetType
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.RecordFormat

/**
 * Request body for create dataset request
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-create-sequential-partitioned-data-set#CreateDataSet__Reqbod">Request body to create a sequential and partitioned dataset</a>
 * */
data class ZosmfCreateDatasetBody(

  /** Volume serial */
  @SerializedName("volser")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) var volumeSerial: String? = null,

  /** Device type */
  @SerializedName("unit")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) var deviceType: String? = null,

  /** Dataset organization */
  @SerializedName("dsorg")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) var datasetOrganization: DatasetOrganization,

  /** Unit of space allocation */
  @SerializedName("alcunit")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) var allocationUnit: AllocationUnit? = null,

  /** Primary space allocation */
  @SerializedName("primary")
  @AvailableSince(ZVersion.ZOS_2_1) var primaryAllocation: Int,

  /** Secondary space allocation */
  @SerializedName("secondary")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) var secondaryAllocation: Int,

  /** Number of directory blocks */
  @SerializedName("dirblk")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) var directoryBlocks: Int? = null,

  /** Average block size */
  @SerializedName("avgblk")
  @AvailableSince(ZVersion.ZOS_2_1) var averageBlockLength: Int? = null,

  /** Record format */
  @SerializedName("recfm")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) var recordFormat: RecordFormat,

  /** Block size */
  @SerializedName("blksize")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) var blockSize: Int? = null,

  /** Record length */
  @SerializedName("lrecl")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) var recordLength: Int? = null,

  /** Storage class */
  @SerializedName("storclass")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) var storageClass: String? = null,

  /** Management class */
  @SerializedName("mgntclass")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) var managementClass: String? = null,

  /** Data class */
  @SerializedName("dataclass")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) var dataClass: String? = null,

  /** Dataset type */
  @SerializedName("dsntype")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_3) var dsnType: DatasetType? = null,

  /** Model dataset name */
  @SerializedName("like")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_4) var datasetModel: String? = null
)
