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

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.files.api.messaging.RetrieveFileContentResponse
import org.zowe.kotlinsdk.providers.zowe.HttpResponse

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-unix-file">Retrieve the contents of a z/OS UNIX file</a> */
class ZosmfRetrieveFileContentResponse(
  override var status: Status,

  /** text content as the response */
  @property:AvailableSince(ZVersion.ZOS_2_1) val fetchedText: String? = null,

  /** bytes content as the response */
  @property:AvailableSince(ZVersion.ZOS_2_1) val fetchedBytes: ByteArray? = null
) : RetrieveFileContentResponse, HttpResponse
