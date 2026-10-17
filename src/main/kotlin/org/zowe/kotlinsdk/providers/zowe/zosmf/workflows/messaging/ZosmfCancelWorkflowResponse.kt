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
import org.zowe.kotlinsdk.core.workflows.api.messaging.CancelWorkflowResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-cancel-workflow">Cancel a workflow: Response content</a>
 * @property workflowName the workflowName response param: the new name of the canceled workflow
 */
@Serializable
class ZosmfCancelWorkflowResponse(
  @SerialName("workflowName") override val workflowName: String = ""
) : ZosmfHttpResponse(), CancelWorkflowResponse
