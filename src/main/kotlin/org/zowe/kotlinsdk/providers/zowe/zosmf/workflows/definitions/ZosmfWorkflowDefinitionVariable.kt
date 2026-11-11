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
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-retrieve-workflow-definition">Retrieve a workflow definition: variable-definition object</a>
 * @property name the name response param
 * @property scope the scope response param
 * @property abstract the abstract response param
 * @property category the category response param
 * @property default the default response param
 * @property description the description response param
 * @property type the type response param
 * @property visibility the visibility response param
 * @property choice the choice response param
 * @property decimalPlaces the decimalPlaces response param
 * @property maxLength the maxLength response param
 * @property minLength the minLength response param
 * @property maxValue the maxValue response param
 * @property minValue the minValue response param
 * @property regularExpression the regularExpression response param
 * @property validationType the validationType response param
 * @property exposeToUser the exposeToUser response param
 * @property promptAtCreate the promptAtCreate response param
 * @property requiredAtCreate the requiredAtCreate response param
 * @property valueMustBeChoice the valueMustBeChoice response param
 */
@Serializable
class ZosmfWorkflowDefinitionVariable(
  @SerialName("name") val name: String = "",
  @SerialName("scope") val scope: String? = null,
  @SerialName("abstract") val abstract: String? = null,
  @SerialName("category") val category: String? = null,
  @SerialName("default") val default: String? = null,
  @SerialName("description") val description: String? = null,
  @SerialName("type") val type: String? = null,
  @SerialName("visibility") val visibility: String? = null,
  @SerialName("choice") val choice: List<String> = emptyList(),
  @SerialName("decimalPlaces") val decimalPlaces: Int? = null,
  @SerialName("maxLength") val maxLength: Int? = null,
  @SerialName("minLength") val minLength: Int? = null,
  @SerialName("maxValue") val maxValue: String? = null,
  @SerialName("minValue") val minValue: String? = null,
  @SerialName("regularExpression") val regularExpression: String? = null,
  @SerialName("validationType") val validationType: String? = null,
  @SerialName("exposeToUser") val exposeToUser: Boolean? = null,
  @SerialName("promptAtCreate") val promptAtCreate: Boolean? = null,
  @SerialName("requiredAtCreate") val requiredAtCreate: Boolean? = null,
  @SerialName("valueMustBeChoice") val valueMustBeChoice: Boolean? = null
)
