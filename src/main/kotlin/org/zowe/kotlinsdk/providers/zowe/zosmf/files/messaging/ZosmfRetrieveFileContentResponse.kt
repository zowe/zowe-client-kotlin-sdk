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

import io.ktor.utils.io.ByteReadChannel
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.ChanneledRequest
import org.zowe.kotlinsdk.core.DataType
import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.files.api.messaging.RetrieveFileContentResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpChanneledResponse

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-unix-file">Retrieve the contents of a z/OS UNIX file</a> */
class ZosmfRetrieveFileContentResponse(
  override val status: Status,
  requestDataType: DataType,
  @property:AvailableSince(ZVersion.ZOS_2_1) override val fetchedText: String? = null,
  override val fetchChannel: ByteReadChannel? = null,
  override val contentLength: Long? = null,
  override val channelSize: Int = ChanneledRequest.DEFAULT_CHANNEL_SIZE
) : ZosmfHttpChanneledResponse(), RetrieveFileContentResponse {
  @AvailableSince(ZVersion.ZOS_2_1) override val fetchedDataType =
    if (status.type == StatusType.SUCCESS) requestDataType else DataType.ERROR
}
