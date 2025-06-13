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
import org.zowe.kotlinsdk.core.files.api.messaging.WriteToFileResponse
import org.zowe.kotlinsdk.providers.zowe.HttpResponse

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-write-data-zos-unix-file#PutWriteUnixFile__title__8">Write data to a z/OS UNIX file: Expected response</a> */
class ZosmfWriteToFileResponse(
  override var status: HttpStatusCode
) : WriteToFileResponse, HttpResponse
