/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.jes.definitions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=zjri-json-document-specifications-zos-jobs-rest-interface-requests#JSONDocumentSpecifications__JobStepDataDocumentContents__title__1">JSON document specifications for z/OS jobs REST interface requests: Job step data document</a>
 * @property isActive active response param
 * @property smfId smfid response param
 * @property stepNumber step-number response param
 * @property selectedTime selected-time response param
 * @property owner owner response param
 * @property programName program-name response param
 * @property stepName step-name response param
 * @property ussPathName path-name response param
 * @property substepNumber substep-number response param
 * @property endTime end-time response param
 * @property procedureStepName proc-step-name response param
 * @property completion completion response param
 * @property abendReasonCode abend-reason-code response param
 */
@Serializable
class ZosmfJobStepData(
  @SerialName("active")
  @property:AvailableSince(ZVersion.ZOS_2_2) val isActive: Boolean,

  @SerialName("smfid")
  @property:AvailableSince(ZVersion.ZOS_2_2) val smfId: String? = null,

  @SerialName("step-number")
  @property:AvailableSince(ZVersion.ZOS_2_2) val stepNumber: Int,

  @SerialName("selected-time")
  @property:AvailableSince(ZVersion.ZOS_2_2) val selectedTime: String? = null,

  @SerialName("owner")
  @property:AvailableSince(ZVersion.ZOS_2_2) val owner: String? = null,

  @SerialName("program-name")
  @property:AvailableSince(ZVersion.ZOS_2_2) val programName: String? = null,

  @SerialName("step-name")
  @property:AvailableSince(ZVersion.ZOS_2_2) val stepName: String? = null,

  @SerialName("path-name")
  @property:AvailableSince(ZVersion.ZOS_2_2) val ussPathName: String? = null,

  @SerialName("substep-number")
  @property:AvailableSince(ZVersion.ZOS_2_2) val substepNumber: Int? = null,

  @SerialName("end-time")
  @property:AvailableSince(ZVersion.ZOS_2_2) val endTime: String? = null,

  @SerialName("proc-step-name")
  @property:AvailableSince(ZVersion.ZOS_2_2) val procedureStepName: String? = null,

  @SerialName("completion")
  @property:AvailableSince(ZVersion.ZOS_2_2) val completion: String? = null,

  @SerialName("abend-reason-code")
  @property:AvailableSince(ZVersion.ZOS_2_2) val abendReasonCode: Int? = null,
)
