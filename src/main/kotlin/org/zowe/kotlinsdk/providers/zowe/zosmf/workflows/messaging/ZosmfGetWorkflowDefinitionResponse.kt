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

import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.workflows.api.messaging.GetWorkflowDefinitionResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.definitions.ZosmfWorkflowDefinition

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-retrieve-workflow-definition">Retrieve a workflow definition: Response content</a>
 * @property definition the actual response of the getWorkflowDefinition request, wrapped in this class for consistency
 */
class ZosmfGetWorkflowDefinitionResponse(
  override val status: Status,
  override val definition: ZosmfWorkflowDefinition
) : ZosmfHttpResponse(), GetWorkflowDefinitionResponse
