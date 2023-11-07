//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.jes.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/3.1.0?topic=zjri-json-document-specifications-zos-jobs-rest-interface-requests#JSONDocumentSpecifications__JobStepDataDocumentContents__title__1
 */
class JobStepData(
  /** active response param */
  @SerializedName("active")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_2) val isActive: Boolean,

  /** step-number response param */
  @SerializedName("step-number")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_2) val stepNumber: Int,

  /** smfid response param */
  @SerializedName("smfid")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_2) val smfId: String? = null,

  /** selected-time response param */
  @SerializedName("selected-time")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_2) val startedTime: String? = null,

  /** owner response param */
  @SerializedName("owner")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_2) val owner: String? = null,

  /** program-name response param */
  @SerializedName("program-name")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_2) val programName: String? = null,

  /** step-name response param */
  @SerializedName("step-name")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_2) val stepName: String? = null,

  /** path-name response param */
  @SerializedName("path-name")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_2) val ussPathName: String? = null,

  /** substep-number response param */
  @SerializedName("substep-number")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_2) val substepNumber: Int? = null,

  /** end-time response param */
  @SerializedName("end-time")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_2) val endTime: String? = null,

  /** proc-step-name response param */
  @SerializedName("proc-step-name")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_2) val procedureStepName: String? = null,

  /** completion response param */
  @SerializedName("completion")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_2) val completion: String? = null,

  /** abend-reason-code response param */
  @SerializedName("abend-reason-code")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_2) val abendRC: String? = null,
)
