/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import org.zowe.kotlinsdk.core.datasets.api.messaging.RenameDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.SshResponse

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=subcommands-rename-command">RENAME command</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=command-rename-operands">RENAME command operands</a>
 * @property dsName data set or data set + member to rename
 * @property newDsName a new data set name to rename to
 * @property newMemName a new member name to rename the member to (if provided)
 */
class SshRenameDatasetRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val dsName: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val newDsName: String = "",
  @property:AvailableSince(ZVersion.ZOS_2_2) val newMemName: String = ""
) : SshRequest, RenameDatasetRequest {
  private val renameTo =
    if (newMemName.isNotEmpty()) "($newMemName)"
    else "'${newDsName.ifEmpty { throw Exception("Either a new data set or member name must be provided") }}'"

  override var sshCommand: String = "tsocmd \"RENAME '$dsName' $renameTo\""

  override suspend fun produceResponseObject(clientResponse: Any): SshResponse {
    return SshRenameDatasetResponse(clientResponse as SshCmdResponse)
  }
}
