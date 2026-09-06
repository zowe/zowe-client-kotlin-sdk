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

import io.ktor.http.HttpMethod
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.connectivity.HttpConnection
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.produceUssPathPart
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfUtilitiesRequestHeaders

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
 * @property filePathParam file-path-name path param
 * // TODO: Content-Type header correct processing
 *   /** charset-name param for Content-Type header  */
 * //  @property:AvailableSince(ZVersion.ZOS_2_1) override val charsetName: String? = "UTF-8",
 */
abstract class ZosmfUssUtilitiesRequest(
  override val connection: HttpConnection,
  @property:AvailableSince(ZVersion.ZOS_2_1) private val filePathParam: String,
  override val headers: ZosmfUtilitiesRequestHeaders
) : ZosmfHttpRequest {
  override val method = HttpMethod.Put
  override val path = "/zosmf/restfiles/fs/${produceUssPathPart(filePathParam)}"
  override val parameters = emptyMap<String, String>()
}
