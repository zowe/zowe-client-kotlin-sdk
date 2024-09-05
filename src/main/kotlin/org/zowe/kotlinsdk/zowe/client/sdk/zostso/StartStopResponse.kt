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

package org.zowe.kotlinsdk.zowe.client.sdk.zostso

import org.zowe.kotlinsdk.TsoResponse

/**
 * The TsoStartStop API response
 */
data class StartStopResponse(

  /**
   * Response from z/OSMF to start rest call
   */
  val tsoResponse: TsoResponse? = null,

  /**
   * Servlet key from ZosmfTsoResponse
   */
  val servletKey: String? = null,

  /**
   * If an error occurs, returns error which contains cause error.
   */
  var failureResponse: String? = null,

  /**
   * True if the command was issued and the responses were collected.
   */
  val success: Boolean? = false

)
