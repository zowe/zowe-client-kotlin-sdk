/*
 * Copyright (c) 2024 IBA Group.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   IBA Group
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.core.datasets.data

/** Represents basic dataset instance */
interface DatasetItem {
  val datasetName: String
  val isMigrated: Boolean?
  val blockSize: Int?
  val datasetOrganization: DatasetOrganization?
  val recordLength: Int?
  val recordFormat: RecordFormat?
  val sizeInTracks: Int?
  val spaceUnits: SpaceUnits?
  val volumeSerial: String?

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
    VSAM
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
