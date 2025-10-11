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

import org.zowe.kotlinsdk.providers.zowe.Cancellable
import kotlin.coroutines.CoroutineContext

// TODO: doc
/**
 * Request canceller class to provide a possibility to cancel a [org.zowe.kotlinsdk.providers.zowe.Cancellable] request
 * @property currentRequest the current running request to cancel (will be null until the request is started)
 */
class RequestCanceller {
  var coroutineContext: CoroutineContext? = null
  var currentRequest: Cancellable? = null

  /** Cancel the current running request if it is present */
  fun cancelCurrentRequest() {
    currentRequest?.cancel()
  }
}