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

import io.ktor.http.HttpStatusCode
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfErrorReport
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfStatus

/**
 * A z/OSMF REST API createFile response status
 * @property code the [HttpStatusCode] produced by a z/OSMF REST API request. 201 is the successful one
 * @property errorReport the [ZosmfErrorReport] document to parse as a metadata of the status
 */
open class ZosmfCreateFileStatus(
  private var code: HttpStatusCode = HttpStatusCode.OK,
  private val errorReport: ZosmfErrorReport? = null
) : ZosmfStatus(code, errorReport) {
  override val type: StatusType
    get() {
      return when {
        code == HttpStatusCode.Created -> StatusType.SUCCESS
        code.value in (200 until 400) -> StatusType.WARNING
        else -> StatusType.ERROR
      }
    }
}
