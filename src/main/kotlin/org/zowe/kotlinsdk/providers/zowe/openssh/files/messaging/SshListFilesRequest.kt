/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import org.zowe.kotlinsdk.core.files.api.messaging.ListFilesRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.SshResponse

/**
 * Request to list the USS files and directories by the provided path, using the "ls -l" command.
 * The long listing format is required, as the file type, the permissions
 * and the symbolic link target are parsed out of it
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-ls-list-file-directory-names-attributes">ls — List file and directory names and attributes</a>
 * @property connection the SSH connection to run the command with
 * @property filter the USS path to list the items of. Might be a directory path, a file path or a glob pattern
 */
class SshListFilesRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val filter: String
) : SshRequest, ListFilesRequest {
  override var sshCommand = "ls -l $filter"

  override suspend fun produceResponseObject(clientResponse: Any): SshResponse {
    return SshListFilesResponse(clientResponse as SshCmdResponse)
  }
}
