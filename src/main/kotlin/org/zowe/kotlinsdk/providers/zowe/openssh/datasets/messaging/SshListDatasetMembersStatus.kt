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
 * Status for listDatasetMembers SSH command.
 * Looks through the SSH command output to find the "--MEMBERS--" line.
 * If there is no such line, exit status is considered as an error.
 * If there is such line, but there is no member definitions after it, exit status is considered as a warning.
 * Modifies the status output text respectively
 */
class SshListDatasetMembersStatus(sshCmdResponse: SshCmdResponse) : SshStatus(sshCmdResponse) {
  private val outputHasMembers = sshCmdResponse.output.contains("--MEMBERS--")

  val membersEmpty = sshCmdResponse.output
    .split("--MEMBERS--")
    .getOrElse(1) { "" }
    .split("\n")
    .none { it.isNotEmpty() }

  override val type: StatusType
    get() {
      return when {
        sshCmdResponse.exitStatus != 0 -> super.type
        !outputHasMembers -> StatusType.ERROR
        membersEmpty -> StatusType.WARNING
        else -> super.type
      }
    }

  override val metadata: Map<String, Any>
    get() {
      return super.metadata +
        listOfNotNull(
          when {
            sshCmdResponse.exitStatus != 0 -> null
            !outputHasMembers -> {
              "Output" to (
                super.metadata.getOrDefault("Output", "") as String +
                  "\nTHE PROCESSED ENTITY IS NOT A PDS / PDSE DATA SET"
                )
            }
            membersEmpty -> {
              "Output" to (
                super.metadata.getOrDefault("Output", "") as String +
                  "\nTHE PDS / PDSE DATA SET IS EMPTY"
                )
            }
            else -> null
          }
        ).toMap()
    }
}
