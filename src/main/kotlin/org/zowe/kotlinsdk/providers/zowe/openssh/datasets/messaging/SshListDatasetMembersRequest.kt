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
import org.zowe.kotlinsdk.core.datasets.AttributesLevel
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetMembersRequest
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.SshResponse

/**
 * List data set members SSH request.
 * Is performed through TSO LISTDS with MEMBERS operand
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=subcommands-listds-command">LISTDS command</a>
 * @property dsName data set name to list members of
 */
class SshListDatasetMembersRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val dsName: String
) : SshRequest, ListDatasetMembersRequest {
  /** Hardcoded as it is not supported by SSH to return more info than a member name (at least I did not find it) */
  override val attributesLevel: AttributesLevel = AttributesLevel.NAME

  override var sshCommand = "tsocmd LISTDS \"'$dsName'\" MEMBERS"

  override suspend fun produceResponseObject(clientResponse: Any): SshResponse {
    return SshListDatasetMembersResponse(clientResponse as SshCmdResponse)
  }
}
