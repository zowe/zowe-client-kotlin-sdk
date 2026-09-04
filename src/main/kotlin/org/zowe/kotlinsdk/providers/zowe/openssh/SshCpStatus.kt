/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh

import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus

/**
 * The status of an SSH command that is built around the "cp" command.
 * The "cp" command reports a record truncation with a non-zero exit status, although the copy itself is performed:
 * the records, longer than the LRECL of the data set to copy to, are cut to fit it. Such a result is considered
 * as [StatusType.WARNING] rather than as an error. Everything else is left to the basic [SshStatus] processing
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-cp-copy-file#cp__title__18">cp - Copy a file. Exit values</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-cp-copy-file#cp__cpmessagesection__title__1">cp - Copy a file. Messages</a>
 */
class SshCpStatus(
  sshCmdResponse: SshCmdResponse = SshCmdResponse(),
) : SshStatus(sshCmdResponse) {
  companion object {
    /** The message ID the truncation of the records being copied is reported with */
    private const val TRUNCATION_MESSAGE_ID = "EDC5003I"
  }

  override val type: StatusType
    get() {
      return if (sshCmdResponse.stderr.contains(TRUNCATION_MESSAGE_ID)) StatusType.WARNING else super.type
    }
}
