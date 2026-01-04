/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf

import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.AvailableUntil
import org.zowe.kotlinsdk.annotations.ZVersion

/** @see <a href="https://www.ibm.com/docs/en/zos/3.2.0?topic=zdsfri-json-document-specifications-zos-data-set-file-rest-interface-requests#RESTFILES_JSONDocumentSpecifications__ErrorReportDocumentContents_4RESTfile__title__1">Error report document</a> */
@Serializable
data class ZosmfErrorReport(
  @property:AvailableSince(ZVersion.ZOS_2_1) val category: Int,
  @property:AvailableSince(ZVersion.ZOS_2_1) val rc: Int,
  @property:AvailableSince(ZVersion.ZOS_2_1) val reason: Int,
  @property:AvailableSince(ZVersion.ZOS_2_1) val message: String,
  @property:AvailableSince(ZVersion.ZOS_2_1) val details: List<String>? = null,
  @property:AvailableSince(ZVersion.ZOS_2_1)
  @property:AvailableUntil(ZVersion.ZOS_2_4)
  val stack: String? = null
)
