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
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetMembersResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.ZosmfMemberItem

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-list-members-zos-data-set">List the members of a z/OS data set</a>
 * @property memberItems the items response param
 * @property returnedRows the returnedRows response param
 * @property totalRows the totalRows response param
 * @property jsonVersion the JSONversion response param
 */
@Serializable
class ZosmfListDatasetMembersResponse(
  @SerialName("items")
  @property:AvailableSince(ZVersion.ZOS_2_1) override val memberItems: List<ZosmfMemberItem> = emptyList(),

  @SerialName("returnedRows")
  @property:AvailableSince(ZVersion.ZOS_2_1) val returnedRows: Int = 0,

  @SerialName("totalRows")
  @property:AvailableSince(ZVersion.ZOS_2_1) val totalRows: Int? = null,

  @SerialName("JSONversion")
  @property:AvailableSince(ZVersion.ZOS_2_1) val jsonVersion: Int = 0
) : ZosmfHttpResponse(), ListDatasetMembersResponse