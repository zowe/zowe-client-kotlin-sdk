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
 * z/OSMF synchronous console command response messages. See the z/OSMF REST API publication for complete details.
 */
data class IssueResponse(

  /**
   * Follow-up response URL.
   */
  @SerializedName("cmd-response-url")
  @Expose
  val cmdResponseUrl: String? = null,

  /**
   * Command response text.
   */
  @SerializedName("cmd-response")
  @Expose
  val cmdResponse: String? = null,

  /**
   * The follow-up response URI.
   */
  @SerializedName("cmd-response-uri")
  @Expose
  val cmdResponseUri: String? = null,

  /**
   * The command response key used for follow-up requests.
   */
  @SerializedName("cmd-response-key")
  @Expose
  val cmdResponseKey: String? = null,

  /**
   * True if the solicited keyword requested is present.
   */
  @SerializedName("sol-key-detected")
  @Expose
  val solKeyDetected: String? = null

)
