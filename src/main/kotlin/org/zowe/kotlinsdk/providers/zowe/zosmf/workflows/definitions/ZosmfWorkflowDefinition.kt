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
import org.zowe.kotlinsdk.core.workflows.data.WorkflowDefinitionItem

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-retrieve-workflow-definition">Retrieve a workflow definition: Response content</a>
 * @property id the workflowID response param
 * @property version the workflowVersion response param
 * @property defaultName the workflowDefaultName response param
 * @property description the workflowDescription response param
 * @property vendor the vendor response param
 * @property definitionFileMd5 the workflowDefinitionFileMD5Value response param
 * @property isCallable the isCallable response param
 * @property containsParallelSteps the containsParallelSteps response param
 * @property scope the scope response param
 * @property jobsOutputDirectory the jobsOutputDirectory response param
 * @property category the category response param
 * @property productId the productID response param
 * @property productName the productName response param
 * @property productVersion the productVersion response param
 * @property globalVariableGroup the globalVariableGroup response param
 * @property isInstanceVariableWithoutPrefix the isInstanceVariableWithoutPrefix response param
 * @property steps the steps response param (returned only when steps are requested)
 * @property variables the variables response param (returned only when variables are requested)
 */
@Serializable
class ZosmfWorkflowDefinition(
  @SerialName("workflowID") override val id: String = "",
  @SerialName("workflowVersion") override val version: String = "",
  @SerialName("workflowDefaultName") override val defaultName: String = "",
  @SerialName("workflowDescription") val description: String? = null,
  @SerialName("vendor") val vendor: String? = null,
  @SerialName("workflowDefinitionFileMD5Value") val definitionFileMd5: String? = null,
  @SerialName("isCallable") val isCallable: String? = null,
  @SerialName("containsParallelSteps") val containsParallelSteps: Boolean? = null,
  @SerialName("scope") val scope: ZosmfWorkflowScope? = null,
  @SerialName("jobsOutputDirectory") val jobsOutputDirectory: String? = null,
  @SerialName("category") val category: ZosmfWorkflowCategory? = null,
  @SerialName("productID") val productId: String? = null,
  @SerialName("productName") val productName: String? = null,
  @SerialName("productVersion") val productVersion: String? = null,
  @SerialName("globalVariableGroup") val globalVariableGroup: String? = null,
  @SerialName("isInstanceVariableWithoutPrefix") val isInstanceVariableWithoutPrefix: Boolean? = null,
  @SerialName("steps") val steps: List<ZosmfWorkflowDefinitionStep> = emptyList(),
  @SerialName("variables") val variables: List<ZosmfWorkflowDefinitionVariable> = emptyList()
) : WorkflowDefinitionItem
