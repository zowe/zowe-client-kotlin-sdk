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

package org.zowe.kotlinsdk.providers.zowe.ssh.datasets.messaging

import net.schmizz.sshj.SSHClient
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.AttributesLevel
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetsRequest
import org.zowe.kotlinsdk.providers.zowe.SshConnection
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import java.io.ByteArrayOutputStream

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=subcommands-listds-command">LISTDS command</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=command-listds-operands">LISTDS command operands</a>
 */
class SshListDatasetsRequest(
  override val connection: SshConnection,

  /** data_set operand */
  @AvailableSince(ZVersion.ZOS_2_1) override val mask: String,

  /** Level of attributes to be returned ([AttributesLevel.FULL] by default) */
  @AvailableSince(ZVersion.ZOS_2_1) override val attributesLevel: AttributesLevel = AttributesLevel.FULL,

  // TODO: find the reasonable processing of these parameters
//  /** STATUS operand */
//  @AvailableSince(ZVersion.ZOS_2_1) val shouldReturnStatus: Boolean = false,
//
//  /** HISTORY operand */
//  @AvailableSince(ZVersion.ZOS_2_1) val shouldReturnHistory: Boolean = false,

  /** CATALOG operand */
  @AvailableSince(ZVersion.ZOS_2_1) val catalogName: String? = null,
) : SshRequest, ListDatasetsRequest {

  /**
   * Get the HLQ prefix from the mask that is provided with asterisk.
   * Examples:
   *   TEST.TEST1.* -> TEST.TEST1
   *   TEST.TEST1* -> TEST
   *   TEST.TEST1.*.TEST3 -> TEST.TEST1
   *   TEST.T*ST1 -> TEST
   *   TEST.TEST1.TE.*.** -> TEST.TEST1
   * @param mask the mask to extract the prefix from
   * @return extracted prefix
   */
  private fun extractPrefixBeforeAsterisk(mask: String): String {
    val asteriskIndex = mask.indexOf('*')
    if (asteriskIndex == -1) return mask

    val prefix = mask.substring(0, asteriskIndex)
    val lastDot = prefix.lastIndexOf('.')

    return if (lastDot != -1) prefix.substring(0, lastDot) else prefix
  }

  // TODO: find the reasonable processing of these parameters
//  private val status = if (shouldReturnStatus) "STATUS" else ""
  private val status = ""
//  private val history = if (shouldReturnHistory) "HISTORY" else ""
  private val history = ""
  private val label = if (attributesLevel == AttributesLevel.FULL) "LABEL" else ""
  private val catalog = if (catalogName != null) "CATALOG($catalogName)" else ""
  private val wildcard = if (mask.contains("*")) "LEVEL" else ""
  private val modifiedMask = if (wildcard.isNotEmpty()) extractPrefixBeforeAsterisk(mask) else mask

  override val sshCommand = "tsocmd LISTDS \"'$modifiedMask'\" $status $history $label $catalog $wildcard"

  /**
   * Execute the LISTDS SSH TSOCMD request
   * @param client the SSH client to execute the request with
   * @return SSH handled response with the list of [org.zowe.kotlinsdk.providers.zowe.ssh.datasets.definitions.SshDatasetItem]'s
   */
  override fun execRequest(client: SSHClient): SshResponse {
    client.connect(connection.host, connection.port)
    try {
      client.auth(connection.username, connection.authMethods)

      val session = client.startSession()
      session.use {
        val cmd = it.exec(sshCommand)

        val output = ByteArrayOutputStream()
        cmd.inputStream.copyTo(output)

        cmd.join()
        return SshListDatasetsResponse(output.toString(), mask, modifiedMask, attributesLevel)
      }
    } finally {
      client.disconnect()
    }
  }

}
