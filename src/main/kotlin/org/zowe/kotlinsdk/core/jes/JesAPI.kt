//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.jes

import org.zowe.kotlinsdk.core.API
import org.zowe.kotlinsdk.core.jes.data.GetJobRequest
import org.zowe.kotlinsdk.core.jes.data.GetJobResponse

// TODO: doc
interface JesAPI : API {
  fun getJob(params: GetJobRequest): GetJobResponse
}