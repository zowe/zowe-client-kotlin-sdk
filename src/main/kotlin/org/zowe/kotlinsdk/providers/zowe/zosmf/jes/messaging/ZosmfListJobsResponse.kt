/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.jes.messaging

import org.zowe.kotlinsdk.core.Response
import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.jes.api.messaging.ListJobsResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.jes.definitions.ZosmfJobItem

// TODO: implement
// TODO: doc
class ZosmfListJobsResponse(
  override val status: Status,
  override val jobs: List<ZosmfJobItem>,
) : ZosmfHttpResponse(), ListJobsResponse {
  override suspend fun produceResponseObject(clientResponse: Any): Response {
    TODO("Not yet implemented")
  }

  override suspend fun execRequest(payload: Any): Response {
    TODO("Not yet implemented")
  }
}
