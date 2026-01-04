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

import io.ktor.http.HttpStatusCode
import org.zowe.kotlinsdk.providers.zowe.HttpStatus

/**
 * A z/OSMF REST API response status
 * @property code the [HttpStatusCode] produced by a z/OSMF REST API request
 * @property errorReport the [ZosmfErrorReport] document to parse as a metadata of the status
 */
open class ZosmfStatus(
  private var code: HttpStatusCode = HttpStatusCode.OK,
  private val errorReport: ZosmfErrorReport? = null
) : HttpStatus(code) {
  override val metadata: Map<String, Any>
    get() {
      return errorReport
        ?.let {
          listOfNotNull(
            "Category" to it.category,
            "RC" to it.rc,
            "Reason" to it.reason,
            "Message" to it.message,
            it.details?.let { details -> "Details" to details },
            it.details?.let { stack -> "Stack" to stack },
          ).toMap()
        } ?: emptyMap()
    }
}
