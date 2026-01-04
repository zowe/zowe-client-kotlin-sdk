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

import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus

/**
 * Status for listDatasets SSH command.
 * If the command response contains "LOCATE ERROR" or "NOT IN CATALOG",
 * the status is considered as [StatusType.WARNING]
 */
class SshListDatasetsStatus(
  sshCmdResponse: SshCmdResponse = SshCmdResponse(),
) : SshStatus(sshCmdResponse) {
  override val type: StatusType
    get() {
      return if (
        sshCmdResponse.output.contains("LOCATE ERROR")
        || sshCmdResponse.output.contains("NOT IN CATALOG")
      ) {
        StatusType.WARNING
      } else super.type
    }
}
