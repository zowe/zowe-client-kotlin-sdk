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
 * The TsoSend API response
 */
data class IssueResponse(

  /**
   * True if the command was issued and the responses were collected.
   */
  val success: Boolean = false,

  /**
   * zOSMF start TSO API response.
   */
  val startResponse: StartStopResponses? = null,

  /**
   * Indicates if started TSO contains "READY " message
   */
  val startReady: Boolean = false,

  /**
   * zOSMF stop TSO API response.
   */
  val stopResponse: StartStopResponse? = null,

  /**
   * The list of zOSMF send API responses. May issue multiple requests or
   * to ensure that all messages are collected. Each individual response is placed here.
   */
  val zosmfResponses: List<TsoResponse>? = emptyList(),

  /**
   * The command response text.
   */
  val commandResponses: String? = null

)
