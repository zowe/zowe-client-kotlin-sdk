//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.zosmf.restjobs

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.zosmf.restjobs.data.GetJobStatusRequest
import org.zowe.kotlinsdk.core.zosmf.restjobs.data.JobDocument

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/2.5.0?topic=services-zos-jobs-rest-interface
 */
interface Restjobs {
  /** https://www.ibm.com/docs/en/zos/2.5.0?topic=interface-obtain-status-job */
  @AvailableSince(ZVersion.ZOS_2_1)
  fun getJobStatus(params: GetJobStatusRequest): JobDocument
}