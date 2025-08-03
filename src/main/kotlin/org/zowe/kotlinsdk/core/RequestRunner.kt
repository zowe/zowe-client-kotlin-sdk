/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.core

import kotlinx.coroutines.cancel
import org.zowe.kotlinsdk.providers.zowe.Cancellable
import org.zowe.kotlinsdk.core.RequestCanceller
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

/**
 * Abstraction for a request runner. Provides a request cancellation mechanism.
 * Implements a single-point way to handle an HTTP request and produce an HTTP response
 * @property protocol the protocol, supported by the request runner
 * @property requestCanceller the request cancellation mechanism, specific to the runner
 */
abstract class RequestRunner(
  val protocol: SupportedProtocol,
  private val requestCanceller: RequestCanceller? = null
) : Cancellable {
  /** The current [CoroutineContext] to cancel the request on a demand. Null if there is no running request */
  private var requestCoroutineContext: CoroutineContext? = null

  /** Run a [Request], producing an appropriate [Response] */
  abstract fun runRequest(params: Request): Response

  /**
   * Allow the current running request to be cancelled from the outside
   * @param coroutineContext the [CoroutineContext] to cancel the coroutine with the running request
   */
  protected fun allowRequestCancellation(coroutineContext: CoroutineContext) {
    requestCoroutineContext = coroutineContext
    requestCanceller?.currentRequest = this
  }

  /** Disallow the current running request to be cancelled as the request is already finished */
  protected fun disallowRequestCancellation() {
    requestCanceller?.currentRequest = null
  }

  /** Cancel the running request as a coroutine (does not cancel the actual request, but the result is ignored) */
  override fun cancel() {
    disallowRequestCancellation()
    requestCoroutineContext?.cancel(CancellationException("Request cancelled"))
  }
}
