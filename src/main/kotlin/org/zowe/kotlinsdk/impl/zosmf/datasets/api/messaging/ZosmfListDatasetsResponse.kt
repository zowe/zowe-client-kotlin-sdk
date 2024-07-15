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
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetsResponse
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.ZosmfDatasetItem

/**
 * The response body for [ZosmfListDatasetsRequest]
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-zos-data-sets-system">List the z/OS datasets on a system</a>
 */
class ZosmfListDatasetsResponse(
  /** items response param */
  @SerializedName("items")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) override val dsItems: List<ZosmfDatasetItem> = emptyList(),

  /** returnedRows response param */
  @SerializedName("returnedRows")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val returnedRows: Int = 0,

  /** totalRows response param */
  @SerializedName("totalRows")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val totalRows: Int? = null,

  /** JSONversion response param */
  @SerializedName("JSONversion")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val jsonVersion: Int = 0
) : ListDatasetsResponse(dsItems)
