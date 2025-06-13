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

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging

import io.ktor.http.HttpStatusCode
import org.zowe.kotlinsdk.core.datasets.api.messaging.RecallDatasetResponse
import org.zowe.kotlinsdk.providers.zowe.HttpResponse

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__10">z/OS data set and member utilities: Expected response</a> */
class ZosmfRecallDatasetResponse(
  override var status: HttpStatusCode
) : RecallDatasetResponse, HttpResponse
