/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBA Group 2020
 */

package org.zowe.kotlinsdk

import retrofit2.Call
import retrofit2.http.*

interface TsoApi {

  @POST("/zosmf/tsoApp/tso")
  fun startTso(
    @Header("Authorization") authorizationToken: String,
    @Header("Content-type") contentType: ContentType = ContentType.APP_JSON,
    @Query("proc") proc: String,
    @Query("chset") chset: String,
    @Query("cpage") cpage: TsoCodePage,
    @Query("rows") rows: Int,
    @Query("cols") cols: Int,
    @Query("acct") acct: String? = null,
    @Query("ugrp") ugrp: String? = null,
    @Query("rsize") rsize: Int? = null,
    @Query("appsessid") appsessid: String? = null,
    @Query("system") system: String? = null,
  ): Call<TsoResponse>

  @PUT("/zosmf/tsoApp/tso/{servletKey}")
  fun sendMessageToTso(
    @Header("Authorization") authorizationToken: String,
    @Header("Content-type") contentType: ContentType = ContentType.APP_JSON,
    @Body body: TsoData,
    @Path("servletKey") servletKey: String,
    @Query("readReply") readReply: Boolean? = null,
  ): Call<TsoResponse>

  @GET("/zosmf/tsoApp/tso/{servletKey}")
  fun receiveMessagesFromTso(
    @Header("Authorization") authorizationToken: String,
    @Header("Content-type") contentType: ContentType = ContentType.APP_JSON,
    @Path("servletKey") servletKey: String,
  ): Call<TsoResponse>

  @DELETE("/zosmf/tsoApp/tso/{servletKey}")
  fun endTso(
    @Header("Authorization") authorizationToken: String,
    @Header("Content-type") contentType: ContentType = ContentType.APP_JSON,
    @Path("servletKey") servletKey: String,
    @Query("tsoforcecancel") tsoForceCancel: Boolean? = null,
  ): Call<TsoResponse>

}
