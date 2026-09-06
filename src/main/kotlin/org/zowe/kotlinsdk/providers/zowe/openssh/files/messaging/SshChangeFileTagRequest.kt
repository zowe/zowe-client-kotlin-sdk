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
import org.zowe.kotlinsdk.core.files.api.messaging.ChangeFileTagRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest

/**
 * Change or display the tag of a USS file or directory SSH request, performed by the "chtag" command.
 * The [action] defines the command option to run it with: the tag is set by the type option
 * ("-b", "-t" or "-m"), removed by "-r" and displayed by "-p".
 * The [type] and the [codeSet] are only applicable to [Action.SET], as the tag content is not needed
 * to remove or to display the tag.
 * No existence pre-check is performed, as the "chtag" command reports the absent item by itself
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-chtag-change-file-tag-information">chtag — Change file tag information</a>
 * @property connection the SSH connection object to perform the operation with
 * @property filePath the USS file or directory path to change the tag of
 * @property action the operation to perform on the tag
 * @property type the tag type to set. Required for [Action.SET] and not allowed for the other actions
 * @property codeSet the coded character set to set the tag to, "IBM-1047" or "ISO8859-1", e.g.
 *                   Only allowed for [Action.SET]. If not provided, the code set of the tag is not specified
 * @property recursive change the tag of the directory and of all the items in the hierarchy below it ("chtag -R")
 * @property links the way the symbolic links are treated by the tag change
 */
class SshChangeFileTagRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val filePath: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val action: Action,
  @property:AvailableSince(ZVersion.ZOS_2_2) val type: Type? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val codeSet: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val recursive: Boolean = false,
  @property:AvailableSince(ZVersion.ZOS_2_2) val links: Links = Links.CHANGE,
) : SshRequest, ChangeFileTagRequest {
  /** The operation the "chtag" command performs on the tag of a USS item */
  enum class Action {
    /** The tag is set to the provided type and code set */
    SET,

    /** The tag is removed, the item becomes untagged ("chtag -r") */
    REMOVE,

    /** The tag information is displayed rather than changed ("chtag -p") */
    LIST
  }

  /**
   * The type of the tag to set by the "chtag" command
   * @property chtagOption the "chtag" command option to set the tag type with
   */
  enum class Type(val chtagOption: String) {
    /** The data is not text, no conversion is to be performed ("chtag -b") */
    BINARY("-b"),

    /** The data is a mix of text and binary ("chtag -m") */
    MIXED("-m"),

    /** The data is text, encoded in the provided code set ("chtag -t") */
    TEXT("-t")
  }

  /**
   * The way the symbolic links are treated by the "chtag" command
   * @property chtagOption the "chtag" command option to treat the symbolic links with, if any
   */
  enum class Links(val chtagOption: String?) {
    /** The tag of the file or directory, the symbolic link points to, is changed. The "chtag" command default */
    CHANGE(null),

    /** The tag of the symbolic link itself is changed, the link is not followed ("chtag -h") */
    SUPPRESS("-h")
  }

  private val actionOptions = when (action) {
    Action.SET -> {
      val tagType = type
        ?: throw IllegalArgumentException("The tag type is expected to be provided for the \"${Action.SET}\" action")
      listOfNotNull(tagType.chtagOption, codeSet?.let { "-c $it" })
    }

    Action.REMOVE, Action.LIST -> {
      if (type != null || codeSet != null) {
        throw IllegalArgumentException(
          "Neither the tag type nor the code set is expected to be provided for the \"$action\" action"
        )
      }
      listOf(if (action == Action.REMOVE) "-r" else "-p")
    }
  }

  private val chtagOptions = actionOptions +
    listOfNotNull(
      if (recursive) "-R" else null,
      links.chtagOption
    )

  override var sshCommand = (listOf("chtag") + chtagOptions + "'$filePath'").joinToString(" ")

  override suspend fun produceResponseObject(clientResponse: Any): SshChangeFileTagResponse {
    return SshChangeFileTagResponse(clientResponse as SshCmdResponse)
  }
}
