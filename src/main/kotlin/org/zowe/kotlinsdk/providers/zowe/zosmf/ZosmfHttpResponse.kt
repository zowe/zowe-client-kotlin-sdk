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

import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.providers.zowe.HttpResponse

/**
 * Abstract class for z/OSMF REST API response classes.
 * Is designed to apply a respective status on an HTTP response objects
 */
abstract class ZosmfHttpResponse : HttpResponse {
  private var _status: ZosmfStatus = ZosmfStatus()
  override val status: Status
    get() { return _status }

  fun applyStatus(newStatusObj: ZosmfStatus): ZosmfHttpResponse {
    _status = newStatusObj
    return this
  }
}
