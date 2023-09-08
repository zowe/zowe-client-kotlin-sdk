//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.info

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.impl.info.InfoResponse
import retrofit2.Call
import retrofit2.http.GET

// TODO: doc
interface InfoAPI {

  /**
   * An API function to get an information of the system where z/OSMF is currently running
   * @return a wrapped instance of [InfoResponse]
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("zosmf/info")
  fun getSystemInfo() : Call<InfoResponse>

}