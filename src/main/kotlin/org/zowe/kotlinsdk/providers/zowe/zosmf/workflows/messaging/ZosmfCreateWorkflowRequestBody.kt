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
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.definitions.ZosmfWorkflowAccess

/**
 * Not a data class on purpose: [targetSystemPassword] must not be rendered by "toString"
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-create-workflow">Create a workflow: Request content</a>
 * @property workflowName the descriptive name for the workflow
 * @property workflowDefinitionFile the location of the workflow definition file
 * @property system the nickname of the system on which the workflow is to be created
 * @property owner the user ID of the workflow owner
 * @property workflowDefinitionFileSystem the nickname of the system on which the workflow definition file resides
 * @property variableInputFile the optional properties file to pre-specify the variables values
 * @property variables the list of [Variable] to set for the workflow
 * @property resolveGlobalConflictByUsing which value to use when the input variable conflicts with the global one
 * @property workflowArchiveSafId the SAF ID that can access the archived workflow
 * @property comments the comments to associate with the workflow creation
 * @property assignToOwner whether the workflow steps are assigned to the workflow owner
 * @property accessType the [ZosmfWorkflowAccess] type of the workflow
 * @property accountInfo the account information to use in the JCL JOB statement
 * @property jobStatement the JOB statement JCL to use in the job
 * @property deleteCompletedJobs whether the job is deleted from the JES spool after it completes successfully
 * @property jobsOutputDirectory the UNIX directory to save job spool files to
 * @property autoDeleteOnCompletion whether the workflow is deleted when all its steps are complete or skipped
 * @property targetSystemUser the targetSystemuid param: the user ID for the remote system basic authentication
 * @property targetSystemPassword the targetSystempwd param: the password for the remote system basic authentication
 */
@Serializable
class ZosmfCreateWorkflowRequestBody(
  @SerialName("workflowName") val workflowName: String,
  @SerialName("workflowDefinitionFile") val workflowDefinitionFile: String,
  @SerialName("system") val system: String,
  @SerialName("owner") val owner: String,
  @SerialName("workflowDefinitionFileSystem") val workflowDefinitionFileSystem: String? = null,
  @SerialName("variableInputFile") val variableInputFile: String? = null,
  @SerialName("variables") val variables: List<Variable>? = null,
  @SerialName("resolveGlobalConflictByUsing") val resolveGlobalConflictByUsing: ResolveGlobalConflictByUsing? = null,
  @SerialName("workflowArchiveSAFID") val workflowArchiveSafId: String? = null,
  @SerialName("comments") val comments: String? = null,
  @SerialName("assignToOwner") val assignToOwner: Boolean? = null,
  @SerialName("accessType") val accessType: ZosmfWorkflowAccess? = null,
  @SerialName("accountInfo") val accountInfo: String? = null,
  @SerialName("jobStatement") val jobStatement: String? = null,
  @SerialName("deleteCompletedJobs") val deleteCompletedJobs: Boolean? = null,
  @SerialName("jobsOutputDirectory") val jobsOutputDirectory: String? = null,
  @SerialName("autoDeleteOnCompletion") val autoDeleteOnCompletion: Boolean? = null,
  @SerialName("targetSystemuid") val targetSystemUser: String? = null,
  @SerialName("targetSystempwd") val targetSystemPassword: String? = null
) {
  /**
   * A workflow variable to set on the workflow creation
   * @property name the variable name
   * @property value the variable value
   */
  @Serializable
  data class Variable(
    @SerialName("name") val name: String,
    @SerialName("value") val value: String
  )

  @Serializable
  enum class ResolveGlobalConflictByUsing {
    @SerialName("global") GLOBAL,
    @SerialName("input") INPUT
  }
}
