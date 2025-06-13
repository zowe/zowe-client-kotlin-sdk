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

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetMembersResponse
import org.zowe.kotlinsdk.providers.zowe.HttpResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.ZosmfMemberItem

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-members-zos-data-set">List the members of a z/OS data set</a> */
@Serializable
class ZosmfListDatasetMembersResponse(
  @Transient
  override var status: HttpStatusCode = HttpStatusCode.OK,

  /** items response param */
  @SerialName("items")
  @AvailableSince(ZVersion.ZOS_2_1) override val memberItems: List<ZosmfMemberItem> = emptyList(),

  /** returnedRows response param */
  @SerialName("returnedRows")
  @AvailableSince(ZVersion.ZOS_2_1) val returnedRows: Int = 0,

  /** totalRows response param */
  @SerialName("totalRows")
  @AvailableSince(ZVersion.ZOS_2_1) val totalRows: Int? = null,

  /** JSONversion response param */
  @SerialName("JSONversion")
  @AvailableSince(ZVersion.ZOS_2_1) val jsonVersion: Int = 0
) : ListDatasetMembersResponse, HttpResponse