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
  /**
   * Produce an appropriate [Response] asynchronously by the created request.
   * Should be implemented in a concrete request class
   */
  suspend fun produceResponse(): Response {
    throw Exception("You must define the functionality of producing a correct response yourself")
  }
}
