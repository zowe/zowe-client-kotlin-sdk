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

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import retrofit2.Call
import retrofit2.http.*

interface TsoApi {

  /**
   * An API function to start a new TSO address space (session)
   * @param authorizationToken - is a base 64 encoding representation of <userid>:<password>
   * @param contentType - content type of the request
   * @param proc - a procedure name
   * @param chset - a charset which should be used
   * @param cpage - a codepage which should be used
   * @param rows - a number of rows available for a new session
   * @param cols - a number of columns available for a new session
   * @param acct - an account number
   * @param ugrp - an user group
   * @param rsize - a region size
   * @param appsessid - an application session identifier
   * @param system - a system name
   * @return a wrapped instance of TsoResponse
   */
  @AvailableSince(ZVersion.ZOS_2_1)
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

  /**
   * An API function to send a message to TSO address space
   * @param authorizationToken - is a base 64 encoding representation of <userid>:<password>
   * @param contentType - content type of the request
   * @param body - wrapped instance of TsoData class
   * @param servletKey - Unique identifier for the servlet entry
   * @param readReply -  is an optional parameter that indicates whether the service should send the message and immediately check for a response (default) or just send the message.
   * @return a wrapped instance of TsoResponse
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/tsoApp/tso/{servletKey}")
  fun sendMessageToTso(
    @Header("Authorization") authorizationToken: String,
    @Header("Content-type") contentType: ContentType = ContentType.APP_JSON,
    @Body body: TsoData,
    @Path("servletKey") servletKey: String,
    @Query("readReply") readReply: Boolean? = null,
  ): Call<TsoResponse>

  /**
   * An API function to receive messages from TSO address space
   * @param authorizationToken - is a base 64 encoding representation of <userid>:<password>
   * @param contentType - content type of the request
   * @param servletKey - Unique identifier for the servlet entry
   * @return a wrapped instance of TsoResponse
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  @GET("/zosmf/tsoApp/tso/{servletKey}")
  fun receiveMessagesFromTso(
    @Header("Authorization") authorizationToken: String,
    @Header("Content-type") contentType: ContentType = ContentType.APP_JSON,
    @Path("servletKey") servletKey: String,
  ): Call<TsoResponse>

  /**
   * An API function to close the TSO session
   * @param authorizationToken - is a base 64 encoding representation of <userid>:<password>
   * @param contentType - content type of the request
   * @param servletKey - Unique identifier for the servlet entry
   * @param tsoForceCancel - is an optional parameter that indicates whether to use the CANCEL or LOGOFF command to end the TSO/E address space
   * @return a wrapped instance of TsoResponse
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  @DELETE("/zosmf/tsoApp/tso/{servletKey}")
  fun endTso(
    @Header("Authorization") authorizationToken: String,
    @Header("Content-type") contentType: ContentType = ContentType.APP_JSON,
    @Path("servletKey") servletKey: String,
    @Query("tsoforcecancel") tsoForceCancel: Boolean? = null,
  ): Call<TsoResponse>

}
