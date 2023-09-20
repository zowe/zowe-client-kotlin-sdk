//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.datasets.data

// TODO: doc
abstract class DatasetItem(
  val datasetName: String,
  val isMigrated: Boolean? = null,
  val blockSize: Int? = null,
  val datasetOrganization: DatasetOrganization? = null,
  val recordLength: Int? = null,
  val recordFormat: RecordFormat? = null,
  val sizeInTracks: Int? = null,
  val spaceUnits: SpaceUnits? = null,
  val volumeSerial: String? = null,
) {
  enum class DatasetOrganization {
    PO,
    POE,
    PS,
    VS
  }

  enum class RecordFormat {
    F,
    FB,
    V,
    VB,
    U,
    VSAM,
    VA
  }

  enum class SpaceUnits {
    TRACKS,
    BLOCKS,
    CYLINDERS,
    BYTES,
    KILOBYTES,
    MEGABYTES
  }
 }