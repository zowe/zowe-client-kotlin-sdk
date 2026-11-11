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
import org.zowe.kotlinsdk.core.workflows.data.ArchivedWorkflowItem

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-list-archived-workflows-system">List the archived workflows for a system: workflow-info object</a>
 * @property key the workflowKey response param
 * @property name the workflowName response param
 * @property archivedInstanceUri the archivedInstanceURI response param
 */
@Serializable
class ZosmfArchivedWorkflowItem(
  @SerialName("workflowKey") override val key: String = "",
  @SerialName("workflowName") override val name: String = "",
  @SerialName("archivedInstanceURI") val archivedInstanceUri: String? = null
) : ArchivedWorkflowItem
