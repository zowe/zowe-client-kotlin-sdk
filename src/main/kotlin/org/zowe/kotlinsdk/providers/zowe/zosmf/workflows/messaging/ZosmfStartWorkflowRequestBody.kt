/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Not a data class on purpose: [targetSystemPassword] must not be rendered by "toString"
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-start-workflow">Start a workflow: Request content</a>
 * @property resolveConflictByUsing how the variable conflicts are handled when the step output file is read in
 * @property stepName the name of the step at which the automation is to begin
 * @property performSubsequent whether z/OSMF is to perform the subsequent automated steps
 * @property notificationUrl the URL to notify when the automation completes
 * @property targetSystemUser the targetSystemuid param: the user ID for the remote system basic authentication
 * @property targetSystemPassword the targetSystempwd param: the password for the remote system basic authentication
 */
@Serializable
class ZosmfStartWorkflowRequestBody(
  @SerialName("resolveConflictByUsing") val resolveConflictByUsing: ResolveConflictByUsing? = null,
  @SerialName("stepName") val stepName: String? = null,
  @SerialName("performSubsequent") val performSubsequent: Boolean? = null,
  @SerialName("notificationUrl") val notificationUrl: String? = null,
  @SerialName("targetSystemuid") val targetSystemUser: String? = null,
  @SerialName("targetSystempwd") val targetSystemPassword: String? = null
) {
  @Serializable
  enum class ResolveConflictByUsing {
    @SerialName("outputFileValue") OUTPUT_FILE_VALUE,
    @SerialName("existingValue") EXISTING_VALUE,
    @SerialName("leaveConflict") LEAVE_CONFLICT
  }
}
