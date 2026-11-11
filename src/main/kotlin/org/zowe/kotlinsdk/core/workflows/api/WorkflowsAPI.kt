/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.core.workflows.api

import org.zowe.kotlinsdk.core.API
import org.zowe.kotlinsdk.core.workflows.api.messaging.ArchiveWorkflowRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.ArchiveWorkflowResponse
import org.zowe.kotlinsdk.core.workflows.api.messaging.CancelWorkflowRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.CancelWorkflowResponse
import org.zowe.kotlinsdk.core.workflows.api.messaging.CreateWorkflowRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.CreateWorkflowResponse
import org.zowe.kotlinsdk.core.workflows.api.messaging.DeleteArchivedWorkflowRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.DeleteArchivedWorkflowResponse
import org.zowe.kotlinsdk.core.workflows.api.messaging.DeleteWorkflowRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.DeleteWorkflowResponse
import org.zowe.kotlinsdk.core.workflows.api.messaging.GetArchivedWorkflowPropertiesRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.GetArchivedWorkflowPropertiesResponse
import org.zowe.kotlinsdk.core.workflows.api.messaging.GetWorkflowDefinitionRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.GetWorkflowDefinitionResponse
import org.zowe.kotlinsdk.core.workflows.api.messaging.GetWorkflowPropertiesRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.GetWorkflowPropertiesResponse
import org.zowe.kotlinsdk.core.workflows.api.messaging.ListArchivedWorkflowsRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.ListArchivedWorkflowsResponse
import org.zowe.kotlinsdk.core.workflows.api.messaging.ListWorkflowsRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.ListWorkflowsResponse
import org.zowe.kotlinsdk.core.workflows.api.messaging.StartWorkflowRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.StartWorkflowResponse

/** Workflows API specification to provide functions to work with workflows */
interface WorkflowsAPI : API {
  /**
   * Create a workflow by the provided parameters
   * @param params [CreateWorkflowRequest] instance to get the new workflow parameters from
   * @return [CreateWorkflowResponse] instance with the request result
   */
  suspend fun createWorkflow(params: CreateWorkflowRequest): CreateWorkflowResponse

  /**
   * Get the workflow properties by the provided parameters
   * @param params [GetWorkflowPropertiesRequest] instance to get the workflow locator from
   * @return [GetWorkflowPropertiesResponse] instance with the request result
   */
  suspend fun getWorkflowProperties(params: GetWorkflowPropertiesRequest): GetWorkflowPropertiesResponse

  /**
   * List workflows by the provided parameters
   * @param params [ListWorkflowsRequest] instance to get the respective workflows list
   * @return [ListWorkflowsResponse] instance with the request result
   */
  suspend fun listWorkflows(params: ListWorkflowsRequest): ListWorkflowsResponse

  /**
   * Start a workflow by the provided parameters
   * @param params [StartWorkflowRequest] instance to get the workflow locator from
   * @return [StartWorkflowResponse] instance with the request result
   */
  suspend fun startWorkflow(params: StartWorkflowRequest): StartWorkflowResponse

  /**
   * Cancel a workflow by the provided parameters
   * @param params [CancelWorkflowRequest] instance to get the workflow locator from
   * @return [CancelWorkflowResponse] instance with the request result
   */
  suspend fun cancelWorkflow(params: CancelWorkflowRequest): CancelWorkflowResponse

  /**
   * Delete a workflow by the provided parameters
   * @param params [DeleteWorkflowRequest] instance to get the workflow locator from
   * @return [DeleteWorkflowResponse] instance with the request result
   */
  suspend fun deleteWorkflow(params: DeleteWorkflowRequest): DeleteWorkflowResponse

  /**
   * Get a workflow definition by the provided parameters
   * @param params [GetWorkflowDefinitionRequest] instance to get the workflow definition file location from
   * @return [GetWorkflowDefinitionResponse] instance with the request result
   */
  suspend fun getWorkflowDefinition(params: GetWorkflowDefinitionRequest): GetWorkflowDefinitionResponse

  /**
   * Archive a workflow by the provided parameters
   * @param params [ArchiveWorkflowRequest] instance to get the workflow locator from
   * @return [ArchiveWorkflowResponse] instance with the request result
   */
  suspend fun archiveWorkflow(params: ArchiveWorkflowRequest): ArchiveWorkflowResponse

  /**
   * List archived workflows by the provided parameters
   * @param params [ListArchivedWorkflowsRequest] instance to get the respective archived workflows list
   * @return [ListArchivedWorkflowsResponse] instance with the request result
   */
  suspend fun listArchivedWorkflows(params: ListArchivedWorkflowsRequest): ListArchivedWorkflowsResponse

  /**
   * Get the archived workflow properties by the provided parameters
   * @param params [GetArchivedWorkflowPropertiesRequest] instance to get the archived workflow locator from
   * @return [GetArchivedWorkflowPropertiesResponse] instance with the request result
   */
  suspend fun getArchivedWorkflowProperties(
    params: GetArchivedWorkflowPropertiesRequest
  ): GetArchivedWorkflowPropertiesResponse

  /**
   * Delete an archived workflow by the provided parameters
   * @param params [DeleteArchivedWorkflowRequest] instance to get the archived workflow locator from
   * @return [DeleteArchivedWorkflowResponse] instance with the request result
   */
  suspend fun deleteArchivedWorkflow(params: DeleteArchivedWorkflowRequest): DeleteArchivedWorkflowResponse
}
