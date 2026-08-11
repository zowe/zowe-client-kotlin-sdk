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
import org.zowe.kotlinsdk.core.datasets.api.messaging.CreateDatasetResponse
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus

/** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=command-allocate-return-codes">ALLOCATE command return codes  </a> */
class SshCreateDatasetResponse(sshCmdResponse: SshCmdResponse) : SshResponse, CreateDatasetResponse {
  override var status: Status = SshStatus(sshCmdResponse)
}
