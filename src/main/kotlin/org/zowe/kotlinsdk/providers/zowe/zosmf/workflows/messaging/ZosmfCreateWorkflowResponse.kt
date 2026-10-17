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
import org.zowe.kotlinsdk.core.workflows.api.messaging.CreateWorkflowResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-create-workflow">Create a workflow: Response content</a>
 * @property workflowKey the workflowKey response param
 * @property workflowDescription the workflowDescription response param
 * @property workflowId the workflowID response param
 * @property workflowVersion the workflowVersion response param
 * @property vendor the vendor response param
 */
@Serializable
class ZosmfCreateWorkflowResponse(
  @SerialName("workflowKey") override val workflowKey: String = "",
  @SerialName("workflowDescription") val workflowDescription: String? = null,
  @SerialName("workflowID") val workflowId: String? = null,
  @SerialName("workflowVersion") val workflowVersion: String? = null,
  @SerialName("vendor") val vendor: String? = null
) : ZosmfHttpResponse(), CreateWorkflowResponse
