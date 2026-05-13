/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.jes.messaging

import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus

/** An SSH response status, produced from a Rexx script call */
class SshRexxScriptStatus(
  sshCmdResponse: SshCmdResponse = SshCmdResponse(),
) : SshStatus(sshCmdResponse) {
  override val type: StatusType
    get() {
      return if (
        sshCmdResponse.output.contains("=== ISFEXEC ERROR")
        || (sshCmdResponse.output.contains("=== JOB BY NAME") && sshCmdResponse.output.contains("IS NOT FOUND ==="))
      ) {
        StatusType.ERROR
      } else super.type
    }
}
