/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe

import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.StatusType
import java.time.Instant

/** An HTTP status, formed from the [code] as the resulting object of an executed request */
open class HttpStatus(private var code: HttpStatusCode = HttpStatusCode.OK) : Status {
  companion object {
    val OK = HttpStatus()
  }

  /** Success - codes until 300, warning - codes between 300 and 400, error - every other code */
  override val type: StatusType
    get() {
      return when {
        code.isSuccess() -> StatusType.SUCCESS
        code.value in (300 until 400) -> StatusType.WARNING
        else -> StatusType.ERROR
      }
    }
  override val timestamp: Instant = Instant.now()
  override val metadata: Map<String, Any> = emptyMap()
  override val text: String
    get() {
      return "$code.\n${metadata.entries.joinToString(".\n") { entry -> "${entry.key}: ${entry.value}" } }"
    }
}
