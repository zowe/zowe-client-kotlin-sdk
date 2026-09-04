/*
 * Copyright (c) 2020-2024 IBA Group.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   IBA Group
 *   Zowe Community
 */

package org.zowe.kotlinsdk

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import retrofit2.Call
import retrofit2.http.GET

@Deprecated(
  "Scheduled for removal since v1.0.0",
  ReplaceWith("InfoAPI", "org.zowe.kotlinsdk.core.info")
)
interface InfoAPI {

  /**
   * An API function to get an information of the system where z/OSMF is currently running
   * @return a wrapped instance of [InfoResponse]
   */
  @Deprecated(
    "Scheduled for removal since v1.0.0",
    ReplaceWith("InfoAPI.getSystemInfo", "org.zowe.kotlinsdk.core.info")
  )
  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("zosmf/info")
  fun getSystemInfo() : Call<InfoResponse>

}
