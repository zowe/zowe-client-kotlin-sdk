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

package org.zowe.kotlinsdk.zowe.client.sdk.zosconsole

import org.zowe.kotlinsdk.IssueResponse

/**
 * The Console API response.
 */
data class ConsoleResponse(

  /**
   * True if the command was issued and the responses were collected.
   */
  val success: Boolean? = false,

  /**
   * The list of zOSMF console API responses. May issue multiple requests (because of user request) or
   * to ensure that all messages are collected. Each individual response is placed here.
   */
  val zosmfResponse: IssueResponse? = null,

  /**
   * If an error occurs, returns the [ImperativeError], which contains case error.
   */
  val failureResponse: String? = null,

  /**
   * The command response text.
   */
  var commandResponse: String? = null,

  /**
   * The final command response key - used to "follow-up" and check for additional response messages for the command.
   */
  val lastResponseKey: String? = null,

  /**
   * If the solicited keyword is specified, indicates that the keyword was detected.
   */
  val keywordDetected: Boolean? = false,

  /**
   * The "follow-up" command response URL - you can paste this in the browser to do a "GET" using the command
   * response key provided in the URI route.
   */
  val cmdResponseUrl: String? = null

)
