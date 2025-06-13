/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.jes.definitions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=zjri-json-document-specifications-zos-jobs-rest-interface-requests#JSONDocumentSpecifications__JobStepDataDocumentContents__title__1">JSON document specifications for z/OS jobs REST interface requests: Job step data document</a> */
@Serializable
class ZosmfJobStepData(
  /** active response param */
  @SerialName("active")
  @AvailableSince(ZVersion.ZOS_2_2) val isActive: Boolean,

  /** smfid response param */
  @SerialName("smfid")
  @AvailableSince(ZVersion.ZOS_2_2) val smfId: String? = null,

  /** step-number response param */
  @SerialName("step-number")
  @AvailableSince(ZVersion.ZOS_2_2) val stepNumber: Int,

  /** selected-time response param */
  @SerialName("selected-time")
  @AvailableSince(ZVersion.ZOS_2_2) val selectedTime: String? = null,

  /** owner response param */
  @SerialName("owner")
  @AvailableSince(ZVersion.ZOS_2_2) val owner: String? = null,

  /** program-name response param */
  @SerialName("program-name")
  @AvailableSince(ZVersion.ZOS_2_2) val programName: String? = null,

  /** step-name response param */
  @SerialName("step-name")
  @AvailableSince(ZVersion.ZOS_2_2) val stepName: String? = null,

  /** path-name response param */
  @SerialName("path-name")
  @AvailableSince(ZVersion.ZOS_2_2) val ussPathName: String? = null,

  /** substep-number response param */
  @SerialName("substep-number")
  @AvailableSince(ZVersion.ZOS_2_2) val substepNumber: Int? = null,

  /** end-time response param */
  @SerialName("end-time")
  @AvailableSince(ZVersion.ZOS_2_2) val endTime: String? = null,

  /** proc-step-name response param */
  @SerialName("proc-step-name")
  @AvailableSince(ZVersion.ZOS_2_2) val procedureStepName: String? = null,

  /** completion response param */
  @SerialName("completion")
  @AvailableSince(ZVersion.ZOS_2_2) val completion: String? = null,

  /** abend-reason-code response param */
  @SerialName("abend-reason-code")
  @AvailableSince(ZVersion.ZOS_2_2) val abendReasonCode: String? = null,
)
