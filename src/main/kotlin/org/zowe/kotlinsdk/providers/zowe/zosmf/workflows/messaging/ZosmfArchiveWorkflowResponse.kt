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
import org.zowe.kotlinsdk.core.workflows.api.messaging.ArchiveWorkflowResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-archive-workflow-instance">Archive a workflow instance: Response content</a>
 * @property workflowKey the workflowKey response param: the key of the archived workflow
 */
@Serializable
class ZosmfArchiveWorkflowResponse(
  @SerialName("workflowKey") override val workflowKey: String = ""
) : ZosmfHttpResponse(), ArchiveWorkflowResponse
