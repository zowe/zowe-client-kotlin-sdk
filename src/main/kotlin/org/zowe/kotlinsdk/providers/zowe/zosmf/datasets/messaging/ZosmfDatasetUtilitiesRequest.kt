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

import io.ktor.http.HttpMethod
import org.zowe.kotlinsdk.providers.zowe.HttpRequest
import org.zowe.kotlinsdk.providers.zowe.HttpRequestHeaders

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities">z/OS data set and member utilities</a> */
abstract class ZosmfDatasetUtilitiesRequest : HttpRequest, HttpRequestHeaders {
  override val method = HttpMethod.Put

  protected abstract val fullDsPath: String

  override val path = "/zosmf/restfiles/ds/$fullDsPath"

  override val headers = getHeadersMap()

  override val parameters = emptyMap<String, String>()
}