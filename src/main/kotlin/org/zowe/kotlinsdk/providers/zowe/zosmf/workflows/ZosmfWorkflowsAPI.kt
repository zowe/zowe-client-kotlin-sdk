/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.workflows

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.workflows.api.WorkflowsAPI
import org.zowe.kotlinsdk.core.workflows.api.messaging.ArchiveWorkflowRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.CancelWorkflowRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.CreateWorkflowRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.DeleteArchivedWorkflowRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.DeleteWorkflowRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.GetArchivedWorkflowPropertiesRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.GetWorkflowDefinitionRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.GetWorkflowPropertiesRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.ListArchivedWorkflowsRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.ListWorkflowsRequest
import org.zowe.kotlinsdk.core.workflows.api.messaging.StartWorkflowRequest
import org.zowe.kotlinsdk.providers.zowe.HttpRequestRunner
import org.zowe.kotlinsdk.providers.zowe.ZoweInternalAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfArchiveWorkflowResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfCancelWorkflowResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfCreateWorkflowResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfDeleteArchivedWorkflowResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfDeleteWorkflowResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfGetArchivedWorkflowPropertiesResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfGetWorkflowDefinitionResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfGetWorkflowPropertiesResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfListArchivedWorkflowsResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfListWorkflowsResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfStartWorkflowResponse

/**
 * Implementation of Workflows API for z/OSMF REST API to work with z/OSMF workflows
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-zosmf-workflow">z/OSMF workflow services</a>
 */
@ZoweInternalAPI
class ZosmfWorkflowsAPI(private val requestRunner: HttpRequestRunner) : WorkflowsAPI {

  /** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-create-workflow">Create a workflow</a> */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun createWorkflow(params: CreateWorkflowRequest): ZosmfCreateWorkflowResponse {
    return requestRunner.runRequest(params) as ZosmfCreateWorkflowResponse
  }

  /** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-get-properties-workflow">Get the properties of a workflow</a> */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun getWorkflowProperties(params: GetWorkflowPropertiesRequest): ZosmfGetWorkflowPropertiesResponse {
    return requestRunner.runRequest(params) as ZosmfGetWorkflowPropertiesResponse
  }

  /** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-list-workflows-system-sysplex">List the workflows for a system or sysplex</a> */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun listWorkflows(params: ListWorkflowsRequest): ZosmfListWorkflowsResponse {
    return requestRunner.runRequest(params) as ZosmfListWorkflowsResponse
  }

  /** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-start-workflow">Start a workflow</a> */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun startWorkflow(params: StartWorkflowRequest): ZosmfStartWorkflowResponse {
    return requestRunner.runRequest(params) as ZosmfStartWorkflowResponse
  }

  /** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-cancel-workflow">Cancel a workflow</a> */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun cancelWorkflow(params: CancelWorkflowRequest): ZosmfCancelWorkflowResponse {
    return requestRunner.runRequest(params) as ZosmfCancelWorkflowResponse
  }

  /** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-delete-workflow">Delete a workflow</a> */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun deleteWorkflow(params: DeleteWorkflowRequest): ZosmfDeleteWorkflowResponse {
    return requestRunner.runRequest(params) as ZosmfDeleteWorkflowResponse
  }

  /** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-retrieve-workflow-definition">Retrieve a workflow definition</a> */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun getWorkflowDefinition(params: GetWorkflowDefinitionRequest): ZosmfGetWorkflowDefinitionResponse {
    return requestRunner.runRequest(params) as ZosmfGetWorkflowDefinitionResponse
  }

  /** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-archive-workflow-instance">Archive a workflow instance</a> */
  @AvailableSince(ZVersion.ZOS_2_3)
  override suspend fun archiveWorkflow(params: ArchiveWorkflowRequest): ZosmfArchiveWorkflowResponse {
    return requestRunner.runRequest(params) as ZosmfArchiveWorkflowResponse
  }

  /** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-list-archived-workflows-system">List the archived workflows for a system</a> */
  @AvailableSince(ZVersion.ZOS_2_3)
  override suspend fun listArchivedWorkflows(params: ListArchivedWorkflowsRequest): ZosmfListArchivedWorkflowsResponse {
    return requestRunner.runRequest(params) as ZosmfListArchivedWorkflowsResponse
  }

  /** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-get-properties-archived-workflow">Get the properties of an archived workflow</a> */
  @AvailableSince(ZVersion.ZOS_2_3)
  override suspend fun getArchivedWorkflowProperties(
    params: GetArchivedWorkflowPropertiesRequest
  ): ZosmfGetArchivedWorkflowPropertiesResponse {
    return requestRunner.runRequest(params) as ZosmfGetArchivedWorkflowPropertiesResponse
  }

  /** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-delete-archived-workflow">Delete an archived workflow</a> */
  @AvailableSince(ZVersion.ZOS_2_3)
  override suspend fun deleteArchivedWorkflow(params: DeleteArchivedWorkflowRequest): ZosmfDeleteArchivedWorkflowResponse {
    return requestRunner.runRequest(params) as ZosmfDeleteArchivedWorkflowResponse
  }

}
