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
import org.zowe.kotlinsdk.core.workflows.data.WorkflowItem

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-list-workflows-system-sysplex">List the workflows for a system or sysplex: workflow-info object</a>
 * @property key the workflowKey response param
 * @property name the workflowName response param
 * @property owner the owner response param
 * @property description the workflowDescription response param
 * @property workflowId the workflowID response param
 * @property workflowVersion the workflowVersion response param
 * @property definitionFileMd5 the workflowDefinitionFileMD5Value response param
 * @property instanceUri the instanceURI response param
 * @property vendor the vendor response param
 * @property access the access response param
 */
@Serializable
class ZosmfWorkflowItem(
  @SerialName("workflowKey") override val key: String = "",
  @SerialName("workflowName") override val name: String = "",
  @SerialName("owner") override val owner: String = "",
  @SerialName("workflowDescription") val description: String? = null,
  @SerialName("workflowID") val workflowId: String? = null,
  @SerialName("workflowVersion") val workflowVersion: String? = null,
  @SerialName("workflowDefinitionFileMD5Value") val definitionFileMd5: String? = null,
  @SerialName("instanceURI") val instanceUri: String? = null,
  @SerialName("vendor") val vendor: String? = null,
  @SerialName("access") val access: ZosmfWorkflowAccess? = null
) : WorkflowItem
