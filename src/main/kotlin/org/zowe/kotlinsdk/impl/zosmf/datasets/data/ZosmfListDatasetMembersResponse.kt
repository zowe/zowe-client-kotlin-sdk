//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.datasets.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.data.ListDatasetMembersResponse
import org.zowe.kotlinsdk.core.datasets.data.ListDatasetsResponse

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/2.5.0?topic=zdsfri-json-document-specifications-zos-data-set-file-rest-interface-requests#RESTFILES_JSONDocumentSpecifications__pdsattributesdocument__title__1
 */
class ZosmfListDatasetMembersResponse(
  /** items response param */
  @SerializedName("items")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) private val zosmfItems: List<ZosmfMemberItem> = emptyList(),

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
) : ListDatasetMembersResponse(items = zosmfItems)