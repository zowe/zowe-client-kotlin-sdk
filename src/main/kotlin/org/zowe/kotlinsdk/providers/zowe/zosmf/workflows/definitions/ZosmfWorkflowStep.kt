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
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-get-properties-workflow">Get the properties of a workflow: step-info object</a>
 * @property name the name response param
 * @property title the title response param
 * @property description the description response param
 * @property stepNumber the stepNumber response param
 * @property state the [ZosmfStepState] of the step
 * @property owner the owner response param
 * @property optional the optional response param
 * @property prereqStep the prereqStep response param
 * @property autoEnable the autoEnable response param
 * @property isRestStep the isRestStep response param
 * @property isConditionStep the isConditionStep response param
 * @property hasCalledWorkflow the hasCalledWorkflow response param
 * @property calledInstanceKey the calledInstanceKey response param
 * @property calledWorkflowId the calledWorkflowID response param
 * @property calledWorkflowDefinitionFile the calledWorkflowDefinitionFile response param
 * @property submitAs the submitAs response param
 * @property returnCode the returnCode response param
 * @property skills the skills response param
 * @property weight the weight response param
 * @property userDefined the userDefined response param
 * @property variableReferences the variable-references response param
 * @property steps the nested steps of a parent step
 */
@Serializable
class ZosmfWorkflowStep(
  @SerialName("name") val name: String = "",
  @SerialName("title") val title: String? = null,
  @SerialName("description") val description: String? = null,
  @SerialName("stepNumber") val stepNumber: String? = null,
  @SerialName("state") val state: ZosmfStepState? = null,
  @SerialName("owner") val owner: String? = null,
  @SerialName("optional") val optional: Boolean? = null,
  @SerialName("prereqStep") val prereqStep: List<String> = emptyList(),
  @SerialName("autoEnable") val autoEnable: Boolean? = null,
  @SerialName("isRestStep") val isRestStep: Boolean? = null,
  @SerialName("isConditionStep") val isConditionStep: Boolean? = null,
  @SerialName("hasCalledWorkflow") val hasCalledWorkflow: Boolean? = null,
  @SerialName("calledInstanceKey") val calledInstanceKey: String? = null,
  @SerialName("calledWorkflowID") val calledWorkflowId: String? = null,
  @SerialName("calledWorkflowDefinitionFile") val calledWorkflowDefinitionFile: String? = null,
  @SerialName("submitAs") val submitAs: String? = null,
  @SerialName("returnCode") val returnCode: String? = null,
  @SerialName("skills") val skills: String? = null,
  @SerialName("weight") val weight: Int? = null,
  @SerialName("userDefined") val userDefined: Boolean? = null,
  @SerialName("variable-references") val variableReferences: List<ZosmfWorkflowVariableReference> = emptyList(),
  @SerialName("steps") val steps: List<ZosmfWorkflowStep> = emptyList()
) {
  @Serializable
  enum class ZosmfStepState {
    @SerialName("Unassigned") UNASSIGNED,
    @SerialName("Assigned") ASSIGNED,
    @SerialName("Not Ready") NOT_READY,
    @SerialName("Ready") READY,
    @SerialName("In Progress") IN_PROGRESS,
    @SerialName("Submitted") SUBMITTED,
    @SerialName("Complete") COMPLETE,
    @SerialName("Skipped") SKIPPED,
    @SerialName("Complete (Override)") COMPLETE_OVERRIDE,
    @SerialName("Failed") FAILED,
    @SerialName("Conflicts") CONFLICTS,
    @SerialName("Condition Not Satisfied") CONDITION_NOT_SATISFIED
  }
}
