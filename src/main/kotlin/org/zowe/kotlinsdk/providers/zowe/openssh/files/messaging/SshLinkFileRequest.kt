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

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import org.zowe.kotlinsdk.core.files.api.messaging.LinkFileRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest

/**
 * Create a link to a USS file or directory SSH request, performed by the "ln" command.
 * The link is created by the [filePath] path and refers to the [from] one.
 * No existence pre-check is performed, as the "ln" command refuses to replace an existing path by itself,
 * unless it is explicitly requested to do so by the [force] property
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-ln-create-link-file">ln — Create a link to a file</a>
 * @property connection the SSH connection object to perform the operation with
 * @property filePath the USS path to create the link by
 * @property from the USS file or directory path the link is to refer to
 * @property type the type of the link to create
 * @property recursive link the content of the source directory, including its subdirectories
 * @property force replace the already existing path, the link is requested to be created by
 */
class SshLinkFileRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val filePath: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val from: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val type: Type = Type.HARD,
  @property:AvailableSince(ZVersion.ZOS_2_2) val recursive: Boolean = false,
  @property:AvailableSince(ZVersion.ZOS_2_2) val force: Boolean = false,
) : SshRequest, LinkFileRequest {

  /**
   * The type of the link to create
   * @property lnOption the "ln" command option to create the link of the type
   */
  enum class Type(val lnOption: String?) {
    /**
     * The hard link, which is another directory entry for the same file.
     * The z/OSMF provider has no counterpart for it, as its link function
     * only creates the symbolic and the external links
     */
    HARD(null),

    /** The symbolic link, which is a file, holding the path it refers to */
    SYMBOLIC("-s"),

    /** The external link, which refers to a non-USS object, such as an MVS load module */
    EXTERNAL("-e")
  }

  private val lnOptions = listOfNotNull(
    type.lnOption,
    if (recursive) "-R" else null,
    if (force) "-f" else null
  )

  override var sshCommand =
    (listOf("ln") + lnOptions + "'${validateSource()}'" + "'$filePath'").joinToString(" ")

  override suspend fun produceResponseObject(clientResponse: Any): SshLinkFileResponse {
    return SshLinkFileResponse(clientResponse as SshCmdResponse)
  }

  /**
   * Check that the path the link is to refer to is provided as a meaningful value
   * @return the checked source path
   * @throws IllegalArgumentException when the source path is provided as a blank value
   */
  private fun validateSource(): String {
    if (from.isBlank()) {
      throw IllegalArgumentException("The path to create the link to is provided as blank")
    }
    return from
  }
}
