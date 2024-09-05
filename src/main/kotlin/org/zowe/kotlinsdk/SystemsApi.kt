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
import retrofit2.http.Header

interface SystemsApi {

    /**
     * An API function to get all available systems defined to z/OSMF
     * @param authToken is a base 64 encoding representation of *userid*:*password*
     * @return a wrapped instance of [SystemsResponse]
     */
    @AvailableSince(ZVersion.ZOS_2_1)
    @GET("zosmf/resttopology/systems")
    fun getSystems(
        @Header("Authorization") authToken: String,
    ): Call<SystemsResponse>

}
