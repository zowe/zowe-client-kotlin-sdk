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

/** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-get-properties-workflow">Get the properties of a workflow: statusName</a> */
@Serializable
enum class ZosmfWorkflowStatus(private val type: String) {
  @SerialName("in-progress") IN_PROGRESS("in-progress"),
  @SerialName("complete") COMPLETE("complete"),
  @SerialName("automation-in-progress") AUTOMATION_IN_PROGRESS("automation-in-progress"),
  @SerialName("canceled") CANCELED("canceled");

  override fun toString(): String {
    return this.type
  }
}
