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
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.messaging.RetrieveDatasetContentResponse
import org.zowe.kotlinsdk.providers.zowe.HttpResponse

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-data-set-member">Retrieve the contents of a z/OS dataset or member</a> */
class ZosmfRetrieveDatasetContentResponse(
  override var status: HttpStatusCode,

  /** text content as the response */
  @AvailableSince(ZVersion.ZOS_2_1) val fetchedText: String? = null,

  /** bytes content as the response */
  @AvailableSince(ZVersion.ZOS_2_1) val fetchedBytes: ByteArray? = null
) : RetrieveDatasetContentResponse, HttpResponse
