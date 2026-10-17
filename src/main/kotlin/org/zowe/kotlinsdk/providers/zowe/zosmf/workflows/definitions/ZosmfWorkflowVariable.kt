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
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-get-properties-workflow">Get the properties of a workflow: variable-info object</a>
 * @property name the name response param
 * @property scope the scope response param
 * @property type the type response param
 * @property value the value response param
 * @property visibility the visibility response param
 */
@Serializable
class ZosmfWorkflowVariable(
  @SerialName("name") val name: String = "",
  @SerialName("scope") val scope: String? = null,
  @SerialName("type") val type: String? = null,
  @SerialName("value") val value: String? = null,
  @SerialName("visibility") val visibility: String? = null
)
