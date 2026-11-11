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
import org.zowe.kotlinsdk.core.workflows.api.messaging.GetArchivedWorkflowPropertiesResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.definitions.ZosmfArchivedWorkflowProperties

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-get-properties-archived-workflow">Get the properties of an archived workflow: Response content</a>
 * @property archivedWorkflow the actual response of the getArchivedWorkflowProperties request,
 * wrapped in this class for consistency
 */
class ZosmfGetArchivedWorkflowPropertiesResponse(
  override val status: Status,
  override val archivedWorkflow: ZosmfArchivedWorkflowProperties
) : ZosmfHttpResponse(), GetArchivedWorkflowPropertiesResponse
