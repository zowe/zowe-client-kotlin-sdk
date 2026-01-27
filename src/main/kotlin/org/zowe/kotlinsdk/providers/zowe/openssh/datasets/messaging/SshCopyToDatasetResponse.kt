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

import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.datasets.api.messaging.CopyToDatasetResponse
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.2.0?topic=descriptions-cp-copy-file#cp__title__18">cp - Copy a file. Exit values</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.2.0?topic=descriptions-cp-copy-file#cp__cpmessagesection__title__1">cp - Copy a file. Messages</a>
 */
class SshCopyToDatasetResponse(sshCmdResponse: SshCmdResponse) : SshResponse, CopyToDatasetResponse {
  override var status: Status = SshStatus(sshCmdResponse)
}
