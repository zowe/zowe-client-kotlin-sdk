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

import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.files.api.messaging.CreateFileResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse

/** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-create-unix-file-directory#CreateUnixFile__getlist_datasets__title__1">Create a UNIX file or directory: Expected response</a> */
class ZosmfCreateFileResponse(override val status: Status) : ZosmfHttpResponse(), CreateFileResponse
