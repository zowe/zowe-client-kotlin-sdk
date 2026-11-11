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
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-get-properties-archived-workflow">Get the properties of an archived workflow: Response content</a>
 * @property key the workflowKey response param
 * @property name the workflowName response param
 * @property owner the owner response param
 * @property description the workflowDescription response param
 * @property workflowId the workflowID response param
 * @property workflowVersion the workflowVersion response param
 * @property definitionFileMd5 the workflowDefinitionFileMD5Value response param
 * @property vendor the vendor response param
 * @property system the system response param
 * @property category the category response param
 * @property productId the productID response param
 * @property productName the productName response param
 * @property productVersion the productVersion response param
 * @property percentComplete the percentComplete response param
 * @property isCallable the isCallable response param
 * @property containsParallelSteps the containsParallelSteps response param
 * @property scope the scope response param
 * @property statusName the statusName response param
 * @property deleteCompletedJobs the deleteCompletedJobs response param
 * @property automationStatus the automationStatus response param
 * @property jobsOutputDirectory the jobsOutputDirectory response param
 * @property autoDeleteOnCompletion the autoDeleteOnCompletion response param
 * @property access the access response param
 * @property archivedTime the archivedTime response param
 * @property accountInfo the accountInfo response param
 * @property jobStatement the jobStatement response param
 * @property steps the steps response param (returned only when steps are requested)
 * @property variables the variables response param (returned only when variables are requested)
 */
@Serializable
class ZosmfArchivedWorkflowProperties(
  @SerialName("workflowKey") override val key: String = "",
  @SerialName("workflowName") override val name: String = "",
  @SerialName("owner") val owner: String? = null,
  @SerialName("workflowDescription") val description: String? = null,
  @SerialName("workflowID") val workflowId: String? = null,
  @SerialName("workflowVersion") val workflowVersion: String? = null,
  @SerialName("workflowDefinitionFileMD5Value") val definitionFileMd5: String? = null,
  @SerialName("vendor") val vendor: String? = null,
  @SerialName("system") val system: String? = null,
  @SerialName("category") val category: ZosmfWorkflowCategory? = null,
  @SerialName("productID") val productId: String? = null,
  @SerialName("productName") val productName: String? = null,
  @SerialName("productVersion") val productVersion: String? = null,
  @SerialName("percentComplete") val percentComplete: Int? = null,
  @SerialName("isCallable") val isCallable: String? = null,
  @SerialName("containsParallelSteps") val containsParallelSteps: Boolean? = null,
  @SerialName("scope") val scope: ZosmfWorkflowScope? = null,
  @SerialName("statusName") val statusName: ZosmfWorkflowStatus? = null,
  @SerialName("deleteCompletedJobs") val deleteCompletedJobs: Boolean? = null,
  @SerialName("automationStatus") val automationStatus: ZosmfWorkflowAutomationStatus? = null,
  @SerialName("jobsOutputDirectory") val jobsOutputDirectory: String? = null,
  @SerialName("autoDeleteOnCompletion") val autoDeleteOnCompletion: Boolean? = null,
  @SerialName("access") val access: ZosmfWorkflowAccess? = null,
  @SerialName("archivedTime") val archivedTime: String? = null,
  @SerialName("accountInfo") val accountInfo: String? = null,
  @SerialName("jobStatement") val jobStatement: String? = null,
  @SerialName("steps") val steps: List<ZosmfWorkflowStep> = emptyList(),
  @SerialName("variables") val variables: List<ZosmfWorkflowVariable> = emptyList()
) : ArchivedWorkflowItem
