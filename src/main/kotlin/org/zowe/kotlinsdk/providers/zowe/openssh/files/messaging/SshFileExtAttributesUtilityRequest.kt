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
import org.zowe.kotlinsdk.core.files.api.messaging.FileExtAttributesUtilityRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest

/**
 * Modify or display the extended attributes of a USS file SSH request,
 * performed by the "extattr" command.
 * The attributes to [set] are passed to the command as the "+" operand, the ones to [reset] - as the "-" one.
 * When neither of them is provided, the command is run with no operand at all, which makes it display
 * the current attributes of the file rather than change them.
 * No existence pre-check is performed, as the "extattr" command reports the absent file by itself
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-extattr-set-reset-display-extended-attributes-files">extattr — Set, reset, and display extended attributes for files</a>
 * @property connection the SSH connection object to perform the operation with
 * @property filePath the USS file path to modify or display the extended attributes of
 * @property set the extended attributes to set, as a combination of the [SUPPORTED_ATTRIBUTES] letters
 * @property reset the extended attributes to reset, as a combination of the [SUPPORTED_ATTRIBUTES] letters
 */
class SshFileExtAttributesUtilityRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val filePath: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val set: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val reset: String? = null,
) : SshRequest, FileExtAttributesUtilityRequest {
  companion object {
    /**
     * The extended attributes the "extattr" command operates:
     * "a" - the program runs APF-authorized, "l" - the program is loaded from the shared library region,
     * "p" - the program is program-controlled, "s" - the program is enabled to run in a shared address space
     */
    const val SUPPORTED_ATTRIBUTES = "alps"
  }

  private val attributesOperands = listOfNotNull(
    set?.let { "+${validateAttributes(it, "set")}" },
    reset?.let { "-${validateAttributes(it, "reset")}" }
  )

  override var sshCommand = (listOf("extattr") + attributesOperands + "'$filePath'").joinToString(" ")

  override suspend fun produceResponseObject(clientResponse: Any): SshFileExtAttributesUtilityResponse {
    return SshFileExtAttributesUtilityResponse(clientResponse as SshCmdResponse)
  }

  /**
   * Check that the provided attributes are the ones the "extattr" command is able to operate,
   * and that the same attribute is not requested to be set and to be reset at the same time
   * @param attributes the attributes combination to check
   * @param operandName the name of the request property the attributes are provided by, to report it in an error
   * @return the checked attributes combination
   * @throws IllegalArgumentException when the attributes are not the ones to operate
   */
  private fun validateAttributes(attributes: String, operandName: String): String {
    if (attributes.isEmpty()) {
      throw IllegalArgumentException("No attribute to $operandName is provided")
    }
    val unsupportedAttributes = attributes.filterNot { SUPPORTED_ATTRIBUTES.contains(it) }
    if (unsupportedAttributes.isNotEmpty()) {
      throw IllegalArgumentException(
        "Unable to $operandName the \"$unsupportedAttributes\" attributes, " +
          "only the \"$SUPPORTED_ATTRIBUTES\" ones are supported"
      )
    }
    val contradictingAttributes = set.orEmpty().toSet() intersect reset.orEmpty().toSet()
    if (contradictingAttributes.isNotEmpty()) {
      throw IllegalArgumentException(
        "The \"${contradictingAttributes.joinToString("")}\" attributes are requested " +
          "to be set and to be reset at the same time"
      )
    }
    return attributes
  }
}
