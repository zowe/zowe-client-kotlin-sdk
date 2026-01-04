/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.files.api.messaging.ListFilesResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.definitions.ZosmfFileItem

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-files-directories-unix-file-path#ListUNIXfiles__title__9">List the files and directories of a UNIX file path: List the files and directories of a UNIX file path</a> */
@Serializable
class ZosmfListFilesResponse(
  /** items response param */
  @SerialName("items")
  @property:AvailableSince(ZVersion.ZOS_2_1) override val items: List<ZosmfFileItem> = emptyList(),

  /** returnedRows response param */
  @SerialName("returnedRows")
  @property:AvailableSince(ZVersion.ZOS_2_1) val returnedRows: Int = 0,

  /** moreRows response param */
  @SerialName("moreRows")
  @property:AvailableSince(ZVersion.ZOS_2_1) val moreRows: Boolean? = null,

  /** totalRows response param */
  @SerialName("totalRows")
  @property:AvailableSince(ZVersion.ZOS_2_1) val totalRows: Int? = null,

  /** JSONversion response param */
  @SerialName("JSONversion")
  @property:AvailableSince(ZVersion.ZOS_2_1) val jsonVersion: Int = 0
): ZosmfHttpResponse(), ListFilesResponse
