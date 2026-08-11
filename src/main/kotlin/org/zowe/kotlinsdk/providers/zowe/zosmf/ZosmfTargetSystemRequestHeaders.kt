/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.providers.zowe.HttpRequestHeaders

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-zos-data-set-file-rest-interface#izuhpinfo_api_restfiles__title__5">z/OS data set and file REST interface: Common HTTP Request Headers</a>
 * X-IBM-Target-System + user and password custom headers specification
 */
interface ZosmfTargetSystemRequestHeaders : HttpRequestHeaders {
  /** X-IBM-Target-System default header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystem: String?

    /** X-IBM-Target-System-User custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemUser: String?

  /** X-IBM-Target-System-Password custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemPassword: String?

  override fun getHeadersMap(): Map<String, String?> {
    return mapOf(
      "X-IBM-Target-System" to targetSystem,
      "X-IBM-Target-System-User" to targetSystemUser,
      "X-IBM-Target-System-Password" to targetSystemPassword,
    )
  }
}
