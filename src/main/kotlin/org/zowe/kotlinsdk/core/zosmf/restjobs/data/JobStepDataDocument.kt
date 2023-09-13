//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.zosmf.restjobs.data

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/2.5.0?topic=zjri-json-document-specifications-zos-jobs-rest-interface-requests#JSONDocumentSpecifications__JobStepDataDocumentContents
 */
class JobStepDataDocument(
  /** active response param */
  @AvailableSince(ZVersion.ZOS_2_2) val isActive: Boolean,

  /** smfid response param */
  @AvailableSince(ZVersion.ZOS_2_2) val smfId: String? = null,

  /** step-number response param */
  @AvailableSince(ZVersion.ZOS_2_2) val stepNumber: Int,

  /** selected-time response param */
  @AvailableSince(ZVersion.ZOS_2_2) val startedTime: String? = null,

  /** owner response param */
  @AvailableSince(ZVersion.ZOS_2_2) val owner: String? = null,

  /** program-name response param */
  @AvailableSince(ZVersion.ZOS_2_2) val programName: String,

  /** step-name response param */
  @AvailableSince(ZVersion.ZOS_2_2) val stepName: String,

  /** path-name response param */
  @AvailableSince(ZVersion.ZOS_2_2) val ussPathName: String? = null,

  /** substep-number response param */
  @AvailableSince(ZVersion.ZOS_2_2) val substepNumber: Int? = null,

  /** end-time response param */
  @AvailableSince(ZVersion.ZOS_2_2) val endTime: String? = null,

  /** proc-step-name response param */
  @AvailableSince(ZVersion.ZOS_2_2) val procedureStepName: String,

  /** completion response param */
  @AvailableSince(ZVersion.ZOS_2_2) val completion: String? = null,

  /** abend-reason-code response param */
  @AvailableSince(ZVersion.ZOS_2_2) val abendRC: String? = null,
)