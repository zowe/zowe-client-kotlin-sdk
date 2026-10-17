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
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-retrieve-workflow-definition">Retrieve a workflow definition: variable-specification object</a>
 * @property name the name response param
 * @property scope the scope response param
 * @property required the required response param
 */
@Serializable
class ZosmfWorkflowVariableSpecification(
  @SerialName("name") val name: String = "",
  @SerialName("scope") val scope: String? = null,
  @SerialName("required") val required: Boolean? = null
)
