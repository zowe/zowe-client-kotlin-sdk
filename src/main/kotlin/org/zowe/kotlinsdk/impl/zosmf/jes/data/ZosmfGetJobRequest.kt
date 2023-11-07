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

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.jes.data.GetJobRequest

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-obtain-status-job
 */
class ZosmfGetJobRequest(
  /** X-IBM-Target-System custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystem: String? = null,

  /** X-IBM-Target-System-User custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemUser: String? = null,

  /** X-IBM-Target-System-Password custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemPassword: String? = null,

  /** jobname query param */
  @AvailableSince(ZVersion.ZOS_2_1) val jobName: String? = null,

  /** jobid query param */
  @AvailableSince(ZVersion.ZOS_2_1) val jobId: String? = null,

  /** correlator query param */
  @AvailableSince(ZVersion.ZOS_2_1) val jobCorrelator: String? = null,

  /** step-data query param */
  @AvailableSince(ZVersion.ZOS_2_2) val stepData: UseStepData = UseStepData.NO,

  /** user-correlator query param */
  @AvailableSince(ZVersion.ZOS_2_4) val userCorrelator: String? = null,

  /** exec-data query param */
  @AvailableSince(ZVersion.ZOS_2_4) val execData: ExecData? = null
) : GetJobRequest()
