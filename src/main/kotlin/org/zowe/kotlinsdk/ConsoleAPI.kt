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

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface ConsoleAPI {

  @AvailableSince(ZVersion.ZOS_2_1)
  @PUT("/zosmf/restconsoles/consoles/{consolename}")
  fun issueCommand(
    @Header("Authorization") authorizationToken: String,
    @Header("Content-type") contentType: ContentType = ContentType.APP_JSON,
    @Path("consolename") consoleName: String,
    @Body body: IssueRequestBody
    ): Call<IssueResponse>

}
