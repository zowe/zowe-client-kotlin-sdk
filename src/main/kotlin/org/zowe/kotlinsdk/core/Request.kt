/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.core

/** An abstraction to provide a basic request functionality idea to process by a [RequestRunner] */
interface Request {
  /** Produce a respective [Response] object from the [clientResponse] */
  suspend fun produceResponseObject(clientResponse: Any): Response

  /**
   * Execute the request and produce the appropriate [Response]
   * @param payload some object (like a client or a session) to execute the request with
   */
  suspend fun execRequest(payload: Any): Response
}
