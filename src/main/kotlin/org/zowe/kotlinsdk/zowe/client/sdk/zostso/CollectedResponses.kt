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
 * Tso collected Responses
 */
data class CollectedResponses(

  /**
   * z/OSMF synchronous most tso command response messages.
   */
  val tsos: List<TsoResponse> = emptyList(),

  /**
   * Appended collected messages including READY prompt at the end.
   */
  val messages: String? = null
)
