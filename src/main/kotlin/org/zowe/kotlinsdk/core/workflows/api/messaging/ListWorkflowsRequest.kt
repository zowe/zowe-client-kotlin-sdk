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
 * Represents a basic request to list workflows
 * @property workflowName the workflow name filter
 * @property system the system filter
 * @property owner the workflow owner filter
 */
interface ListWorkflowsRequest : Request {
  val workflowName: String?
  val system: String?
  val owner: String?
}
