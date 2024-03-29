// Copyright © 2020 IBA Group, a.s. All rights reserved. Use of this source code is governed by Eclipse Public License – v 2.0 that can be found at: https://www.eclipse.org/legal/epl-2.0/

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
