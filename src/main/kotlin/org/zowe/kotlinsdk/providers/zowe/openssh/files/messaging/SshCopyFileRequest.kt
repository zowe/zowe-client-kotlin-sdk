/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging

import net.schmizz.sshj.SSHClient
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import org.zowe.kotlinsdk.core.files.api.messaging.CopyFileRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.definitions.SshFromDataset

/**
 * Copy a USS file or directory SSH request, performed by the "cp" command.
 * The source is either a USS file or directory, provided by the [from] property,
 * or a data set or a data set member, provided by the [fromDataset] property. Exactly one of them is to be provided.
 * The "cp" command overwrites the existing target silently, so the existence pre-check with the "ls" command
 * is performed, unless [overwrite] is set to true. The check is done for the target path itself, thus the copy
 * of an item into an already existing directory also requires [overwrite] to be set to true.
 * The data set content type option of the z/OSMF provider ("from-dataset" -> "type") has no representation here:
 * the "cp" command defaults are used for a data set source
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-cp-copy-file">cp - Copy a file</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-ls-list-file-directory-names-attributes">ls — List file and directory names and attributes</a>
 * @property connection the SSH connection object to perform the operation with
 * @property filePath the USS file or directory path to copy to
 * @property from the USS file or directory path to copy from
 * @property fromDataset the data set or the data set member to copy from
 * @property overwrite copy the item even if the target already exists.
 *                     Without the option an already existing target is not overwritten
 * @property recursive copy the directory together with all of its content ("cp -R")
 * @property links the way the symbolic links are followed during the copy
 * @property preserve the source file attributes to preserve for the target file
 */
class SshCopyFileRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val filePath: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val from: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val fromDataset: SshFromDataset? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val overwrite: Boolean = false,
  @property:AvailableSince(ZVersion.ZOS_2_2) val recursive: Boolean = false,
  @property:AvailableSince(ZVersion.ZOS_2_2) val links: Links = Links.NONE,
  @property:AvailableSince(ZVersion.ZOS_2_2) val preserve: Preserve = Preserve.NONE,
) : SshRequest, CopyFileRequest {
  /**
   * The way the symbolic links are followed by the "cp" command during the copy
   * @property cpOption the "cp" command option to follow the symbolic links with, if any
   */
  enum class Links(val cpOption: String?) {
    /** The symbolic links are not followed, the links themselves are copied */
    NONE(null),

    /** Only the symbolic link, provided as the source of the copy, is followed ("cp -H") */
    SRC("-H"),

    /** Both the source symbolic link and the ones, found during the tree traverse, are followed ("cp -L") */
    ALL("-L")
  }

  /**
   * The source file attributes to preserve for the target file of the "cp" command
   * @property cpOption the "cp" command option to preserve the attributes with, if any
   */
  enum class Preserve(val cpOption: String?) {
    /** No attributes are preserved */
    NONE(null),

    /** The modification and the access times are preserved ("cp -m") */
    MODTIME("-m"),

    /** The modification and the access times, the file mode, the format, the owner and the group are preserved ("cp -p") */
    ALL("-p")
  }

  private val sourceOperand = when {
    from != null && fromDataset != null ->
      throw IllegalArgumentException("Only one source to copy from is expected, but both \"from\" and \"fromDataset\" are provided")

    from != null -> "'$from'"
    fromDataset != null -> fromDataset.buildCpSourceOperand()
    else -> throw IllegalArgumentException("A source to copy from is expected in either \"from\" or \"fromDataset\"")
  }

  private val cpOptions = listOfNotNull(
    if (recursive) "-R" else null,
    links.cpOption,
    preserve.cpOption
  )

  override var sshCommand = (listOf("cp") + cpOptions + sourceOperand + "'$filePath'").joinToString(" ")

  override suspend fun produceResponseObject(clientResponse: Any): SshCopyFileResponse {
    return SshCopyFileResponse(clientResponse as SshCmdResponse)
  }

  /**
   * Checks that the target of the copy does not exist yet with the "ls" command, unless [overwrite] is requested,
   * then performs the copy itself
   * @param client the [SSHClient] to execute the commands with
   * @return the [SshCopyFileResponse] object with respective status
   */
  override suspend fun execSshRequest(client: SSHClient): SshCopyFileResponse {
    if (!overwrite) {
      val listFilesRequest = SshListFilesRequest(connection, filePath)
      val listFilesResponse = listFilesRequest.execSshRequest(client) as SshListFilesResponse
      if (listFilesResponse.status.type == StatusType.SUCCESS) {
        return produceResponseObject(
          SshCmdResponse(1, output = "THE FILE \"$filePath\" ALREADY EXISTS")
        )
      }
    }

    val clientResponse = performSshPlainRequest(client)
    return produceResponseObject(clientResponse)
  }
}
