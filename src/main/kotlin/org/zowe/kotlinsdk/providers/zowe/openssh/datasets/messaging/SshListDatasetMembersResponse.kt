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
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetMembersResponse
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.definitions.SshMemberItem

/** List data set members SSH response */
class SshListDatasetMembersResponse(cmdResponse: SshCmdResponse) : SshResponse, ListDatasetMembersResponse {
  override var status: Status = SshListDatasetMembersStatus(cmdResponse)

  /**
   * Member items to be formed basing on the response status.
   * If the status is [StatusType.ERROR], the value is an empty list
   */
  override val memberItems =
    if (status.type != StatusType.ERROR) {
      cmdResponse.output
        .split("--MEMBERS--")
        .getOrElse(1) { "" }
        .split("\n")
        .filter { it.length > 2 && it.substring(0, 2) == "  " }
        .map { SshMemberItem(it.trim()) }
    } else {
      listOf()
    }
}
