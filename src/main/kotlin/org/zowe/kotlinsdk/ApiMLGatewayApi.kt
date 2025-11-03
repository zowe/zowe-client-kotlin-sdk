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
 *   Dzianis Lisiankou
 */

package org.zowe.kotlinsdk

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiMLGetawayApi {

  @POST("auth/login")
  fun login(
    @Body body: LoginRequest
  ): Call<Void>

  @GET("auth/query")
  fun validate(
    @Header("Authorization") authorizationToken: String
  ): Call<Authentication>

}