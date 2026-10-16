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

import org.zowe.kotlinsdk.core.Response

/**
 * Represents a basic response of the create workflow request
 * @property workflowKey the key of the created workflow
 */
interface CreateWorkflowResponse : Response {
  val workflowKey: String
}
