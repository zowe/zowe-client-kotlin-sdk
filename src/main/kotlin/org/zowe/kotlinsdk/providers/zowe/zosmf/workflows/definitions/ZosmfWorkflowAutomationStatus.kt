/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.definitions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-get-properties-workflow">Get the properties of a workflow: automation-info object</a>
 * @property startUser the startUser response param
 * @property startedTime the startedTime response param
 * @property stoppedTime the stoppedTime response param
 * @property currentStepName the currentStepName response param
 * @property currentStepNumber the currentStepNumber response param
 * @property currentStepTitle the currentStepTitle response param
 * @property messageId the messageID response param
 * @property messageText the messageText response param
 */
@Serializable
class ZosmfWorkflowAutomationStatus(
  @SerialName("startUser") val startUser: String? = null,
  @SerialName("startedTime") val startedTime: String? = null,
  @SerialName("stoppedTime") val stoppedTime: String? = null,
  @SerialName("currentStepName") val currentStepName: String? = null,
  @SerialName("currentStepNumber") val currentStepNumber: String? = null,
  @SerialName("currentStepTitle") val currentStepTitle: String? = null,
  @SerialName("messageID") val messageId: String? = null,
  @SerialName("messageText") val messageText: String? = null
)
