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

package org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging

import io.ktor.http.HttpStatusCode
import org.zowe.kotlinsdk.core.files.api.messaging.SetFileACLResponse
import org.zowe.kotlinsdk.providers.zowe.HttpResponse

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a> */
class ZosmfSetFileACLResponse(
  override var status: HttpStatusCode,
) : SetFileACLResponse, HttpResponse
