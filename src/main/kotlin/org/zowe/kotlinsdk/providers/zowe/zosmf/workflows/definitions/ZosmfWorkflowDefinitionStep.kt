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
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-retrieve-workflow-definition">Retrieve a workflow definition: step-definition object</a>
 * @property name the name response param
 * @property title the title response param
 * @property description the description response param
 * @property prereqStep the prereqStep response param
 * @property optional the optional response param
 * @property calledWorkflowId the calledWorkflowID response param
 * @property calledWorkflowVersion the calledWorkflowVersion response param
 * @property calledWorkflowDefinitionFile the calledWorkflowDefinitionFile response param
 * @property autoEnable the autoEnable response param
 * @property isRestStep the isRestStep response param
 * @property submitAs the submitAs response param
 * @property skills the skills response param
 * @property weight the weight response param
 * @property variableSpecifications the variable-specifications response param
 * @property steps the nested steps of a parent step
 */
@Serializable
class ZosmfWorkflowDefinitionStep(
  @SerialName("name") val name: String = "",
  @SerialName("title") val title: String? = null,
  @SerialName("description") val description: String? = null,
  @SerialName("prereqStep") val prereqStep: List<String> = emptyList(),
  @SerialName("optional") val optional: Boolean? = null,
  @SerialName("calledWorkflowID") val calledWorkflowId: String? = null,
  @SerialName("calledWorkflowVersion") val calledWorkflowVersion: String? = null,
  @SerialName("calledWorkflowDefinitionFile") val calledWorkflowDefinitionFile: String? = null,
  @SerialName("autoEnable") val autoEnable: Boolean? = null,
  @SerialName("isRestStep") val isRestStep: Boolean? = null,
  @SerialName("submitAs") val submitAs: String? = null,
  @SerialName("skills") val skills: String? = null,
  @SerialName("weight") val weight: Int? = null,
  @SerialName("variable-specifications") val variableSpecifications: List<ZosmfWorkflowVariableSpecification> = emptyList(),
  @SerialName("steps") val steps: List<ZosmfWorkflowDefinitionStep> = emptyList()
)
