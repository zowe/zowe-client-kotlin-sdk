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
 * https://www.ibm.com/docs/en/zos/2.5.0?topic=interface-obtain-status-job
 */
class GetJobStatusRequest(
  /** X-IBM-Target-System header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystem: String? = null,

  /** X-IBM-Target-System-User header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemUser: String? = null,

  /** X-IBM-Target-System-Password header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemPassword: String? = null,

  /** user-correlator query param */
  @AvailableSince(ZVersion.ZOS_2_4) val userCorrelator: String? = null,

  /** exec-data query param */
  @AvailableSince(ZVersion.ZOS_2_4) val execData: ExecData? = null
)