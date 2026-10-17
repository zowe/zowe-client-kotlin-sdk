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
import org.zowe.kotlinsdk.core.workflows.api.messaging.ListArchivedWorkflowsResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.definitions.ZosmfArchivedWorkflowItem

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-list-archived-workflows-system">List the archived workflows for a system: Response content</a>
 * @property archivedWorkflows the archivedWorkflows response param
 */
@Serializable
class ZosmfListArchivedWorkflowsResponse(
  @SerialName("archivedWorkflows") override val archivedWorkflows: List<ZosmfArchivedWorkflowItem> = emptyList()
) : ZosmfHttpResponse(), ListArchivedWorkflowsResponse
