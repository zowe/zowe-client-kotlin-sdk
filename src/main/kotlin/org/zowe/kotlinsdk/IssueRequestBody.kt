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

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The z/OSMF console API parameters. See the z/OSMF REST API documentation for full details.
 */
class IssueRequestBody(

  /**
   * The z/OS console command to issue.
   */
  @SerializedName("cmd")
  @Expose
  val cmd: String,

  /**
   * The solicited keyword to look for.
   */
  @SerializedName("sol-key")
  @Expose
  val solKey: String? = null,

  /**
   * The system in the sysplex to route the command.
   */
  @SerializedName("system")
  @Expose
  val system: String? = null,

  /**
   * The method of issuing the command.
   */
  @SerializedName("async")
  @Expose
  val async: String? = null

)
