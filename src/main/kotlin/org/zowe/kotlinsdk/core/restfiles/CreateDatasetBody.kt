//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.restfiles

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.restfiles.AllocationUnit
import org.zowe.kotlinsdk.core.restfiles.DatasetOrganization
import org.zowe.kotlinsdk.core.restfiles.DsType
import org.zowe.kotlinsdk.core.restfiles.RecordFormat

// TODO: doc
data class CreateDatasetBody(
  @SerializedName("volser")
  @Expose
  var volumeSerial: String? = null,

  @SerializedName("unit")
  @Expose
  var deviceType: String? = null,

  @SerializedName("dsorg")
  @Expose
  var datasetOrganization: DatasetOrganization,

  @SerializedName("alcunit")
  @Expose
  var allocationUnit : AllocationUnit? = null,

  @SerializedName("primary")
  var primaryAllocation: Int,

  @SerializedName("secondary")
  @Expose
  var secondaryAllocation: Int,

  @SerializedName("dirblk")
  @Expose
  var directoryBlocks : Int? = null,

  @SerializedName("recfm")
  @Expose
  var recordFormat: RecordFormat,

  @SerializedName("blksize")
  @Expose
  var blockSize: Int? = null,

  @SerializedName("lrecl")
  @Expose
  var recordLength: Int? = null,

  @SerializedName("storclass")
  @Expose
  var storageClass: String? = null,

  @SerializedName("mgntclass")
  @Expose
  var managementClass: String? = null,

  @SerializedName("dataclass")
  @Expose
  var dataClass: String? = null,

  @SerializedName("avgblk")
  var averageBlockLength: Int? = null,

  @SerializedName("dsntype")
  @Expose
  var dsnType: DsType? = null,

  @AvailableSince(ZVersion.ZOS_2_4)
  @SerializedName("like")
  @Expose
  var datasetModel: String? = null
)