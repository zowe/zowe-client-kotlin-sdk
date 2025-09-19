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

import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetMembersResponse
import org.zowe.kotlinsdk.core.datasets.data.MemberItem
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.definitions.SshMemberItem

/** List data set members SSH response */
class SshListDatasetMembersResponse(
  override val status: SshStatus = SshStatus.OK,
) : SshResponse, ListDatasetMembersResponse {
  override val memberItems: List<MemberItem>

  /**
   * Return the list of members (plain names only).
   * RC=4 if there are no members in the data set.
   * RC=8 if the --MEMBERS-- line is not found in the original SSH response
   */
  fun produceMembersListFromSshCmdOutput(): List<SshMemberItem> {
    return if (status.exitStatus != 0) {
      listOf()
    } else {
      if (!status.output.contains("--MEMBERS--")) {
        status.exitStatus = 8
        status.output += "THE PROCESSED ENTITY IS NOT A PDS / PDSE DATA SET\n"
        listOf()
      } else {
        val membersToReturn = status.output
          .split("--MEMBERS--")
          .getOrElse(1) { "" }
          .split("\n")
          .filter { it.length > 2 && it.substring(0, 2) == "  " }
          .map { SshMemberItem(it.trim()) }

        if (membersToReturn.isEmpty()) {
          status.exitStatus = 4
        }
        membersToReturn
      }
    }
  }

  init {
    memberItems = produceMembersListFromSshCmdOutput()
  }
}
