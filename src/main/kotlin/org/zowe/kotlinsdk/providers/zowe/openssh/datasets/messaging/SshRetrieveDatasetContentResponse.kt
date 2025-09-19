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

package org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.DataType
import org.zowe.kotlinsdk.core.datasets.api.messaging.RetrieveDatasetContentResponse
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=tc-oput-copy-mvs-data-set-member-into-zos-unix-file">OPUT - Copy an MVS data set member into a z/OS UNIX file</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=tc-oput-copy-mvs-data-set-member-into-zos-unix-file#tsooput__title__6">OPUT - Copy an MVS data set member into a z/OS UNIX file: Return codes</a>
 */
class SshRetrieveDatasetContentResponse(
  override val status: SshStatus = SshStatus.OK,
  private val requestDataType: DataType
) : SshResponse, RetrieveDatasetContentResponse {
  @property:AvailableSince(ZVersion.ZOS_2_1) override val fetchedDataType: DataType
  @property:AvailableSince(ZVersion.ZOS_2_1) override val fetchedData: Any?

  /**
   * Get a pair of data type to fetched data.
   * If there is an error returned by the SSH request, the data type is [DataType.ERROR]
   */
  fun getFetchedDataFromSshResponse(): Pair<DataType, Any?> {
    return if (status.exitStatus == 0) requestDataType to status.output else DataType.ERROR to status.output
  }

  init {
    val (resultDataType, resultData) = getFetchedDataFromSshResponse()
    fetchedDataType = resultDataType
    fetchedData = resultData
  }
}
