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

import net.schmizz.sshj.SSHClient
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import org.zowe.kotlinsdk.core.datasets.AttributesLevel
import org.zowe.kotlinsdk.core.datasets.api.messaging.CopyToDatasetRequest
import org.zowe.kotlinsdk.core.datasets.data.FromEntity
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.definitions.SshFromDataset
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.definitions.SshFromFile

/**
 * Uses a combination of "tsocmd LISTDS" and "cp" commands. "LISTDS" is used to check whether the data set
 * to copy to exists, "cp" - to perform the copy itself.
 * The "cp" command always overwrites the target, so the copy is always performed the way the z/OSMF provider
 * performs it with "replace" set to true. The "enq", the "volser" and the "alias" options of the z/OSMF provider
 * have no "cp" command counterparts and thus are not supported here
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-cp-copy-file">cp - Copy a file</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=subcommands-listds-command">LISTDS command</a>
 * @property connection the SSH connection object to perform the operation with
 * @property fromEntity the entity to copy from. [SshFromFile] to copy from a USS file,
 *                      [SshFromDataset] to copy from a data set or a data set member
 * @property toDsName the data set name to copy to
 * @property toMemberName the data set member name to copy to (if not provided, copy to a data set will happen)
 */
class SshCopyToDatasetRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val fromEntity: FromEntity,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val toDsName: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val toMemberName: String? = null,
) : SshRequest, CopyToDatasetRequest {
  private val formedDsName = if (!toMemberName.isNullOrEmpty()) "$toDsName($toMemberName)" else toDsName

  private val cpOptionsAndSource = when (fromEntity) {
    // No conversion is performed when copying between the data sets, so no option is needed for them
    is SshFromDataset -> fromEntity.buildCpSourceOperand()
    is SshFromFile -> "${fromEntity.type.cpOption} ${fromEntity.buildCpSourceOperand()}"
    else -> throw Exception("Unknown entity type to copy from: $fromEntity (${fromEntity::class.java})")
  }

  override var sshCommand = "cp $cpOptionsAndSource \"//'$formedDsName'\""

  override suspend fun produceResponseObject(clientResponse: Any): SshCopyToDatasetResponse {
    return SshCopyToDatasetResponse(clientResponse as SshCmdResponse)
  }

  /**
   * Executes "LISTDS" to check if the data set to copy to exists first,
   * then executes "cp" command to copy the content from the provided entity.
   * The check is always performed against the data set name itself, without the member part:
   * a member is created by the copy if it does not exist yet, but the data set to hold it must already be there
   * @param client the [SSHClient] to execute the commands with
   * @return [SshCopyToDatasetResponse] object when the commands are executed
   */
  override suspend fun execSshRequest(client: SSHClient): SshCopyToDatasetResponse {
    val listDatasetsRequest = SshListDatasetsRequest(connection, toDsName, attributesLevel = AttributesLevel.NAME)
    val listDatasetsResponse = listDatasetsRequest.execSshRequest(client) as SshListDatasetsResponse
    if (listDatasetsResponse.status.type != StatusType.SUCCESS) {
      return produceResponseObject(
        SshCmdResponse(
          1,
          output = "ERROR OCCURRED DURING SEARCH FOR THE DATA SET OR MEMBER TO COPY THE CONTENT TO." +
            "\nDETAILS OF THE \"LIST\" OPERATION:\n${listDatasetsResponse.status.text}"
        )
      )
    }

    val clientResponse = performSshPlainRequest(client)
    return produceResponseObject(clientResponse)
  }
}
