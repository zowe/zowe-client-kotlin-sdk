//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.zosmf.info

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.zosmf.info.data.InfoRequest
import org.zowe.kotlinsdk.core.zosmf.info.data.InfoResponse

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/2.5.0?topic=services-zosmf-information-retrieval-service
 */
interface Info {
  /** https://www.ibm.com/docs/en/zos/2.5.0?topic=service-retrieve-zosmf-information */
  @AvailableSince(ZVersion.ZOS_2_1)
  fun getSystemInfo(params: InfoRequest): InfoResponse
}