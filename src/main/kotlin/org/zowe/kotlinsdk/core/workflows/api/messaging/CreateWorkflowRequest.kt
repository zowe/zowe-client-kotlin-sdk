/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.core.workflows.api.messaging

import org.zowe.kotlinsdk.core.Request

/**
 * Represents a basic request to create a workflow
 * @property workflowName the descriptive name of the workflow to create
 * @property workflowDefinitionFile the location of the workflow definition file
 * @property system the system to create the workflow on
 * @property owner the user ID of the workflow owner
 */
interface CreateWorkflowRequest : Request {
  val workflowName: String
  val workflowDefinitionFile: String
  val system: String
  val owner: String
}
