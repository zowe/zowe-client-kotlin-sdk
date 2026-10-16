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
import org.zowe.kotlinsdk.core.workflows.data.ArchivedWorkflowItem

/**
 * Represents a basic response of the get archived workflow properties request
 * @property archivedWorkflow the [ArchivedWorkflowItem] returned by a client
 */
interface GetArchivedWorkflowPropertiesResponse : Response {
  val archivedWorkflow: ArchivedWorkflowItem
}
