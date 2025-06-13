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

import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfDsAndFilesCommonRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfTargetSystemRequestHeaders

/**
 * Headers for "getDatasetInfo" request.
 * Are the combination of both target-specific headers and common datasets and files headers
 */
interface ZosmfGetDatasetInfoRequestHeaders : ZosmfTargetSystemRequestHeaders, ZosmfDsAndFilesCommonRequestHeaders {
  override fun getHeadersMap(): Map<String, String?> {
    return super<ZosmfTargetSystemRequestHeaders>.getHeadersMap() +
      super<ZosmfDsAndFilesCommonRequestHeaders>.getHeadersMap()
  }
}
