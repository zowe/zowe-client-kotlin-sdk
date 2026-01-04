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

import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.jes.api.messaging.GetJobResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.jes.definitions.ZosmfJobItem

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-obtain-status-job#izuhpinfo_api_getjobstatus__title__6">Obtain the status of a job: Expected response</a>
 * @property job the actual response of the getJob request, wrapped in this class for a consistency
 */
class ZosmfGetJobResponse(
  override val status: Status,
  override val job: ZosmfJobItem
) : ZosmfHttpResponse(), GetJobResponse
