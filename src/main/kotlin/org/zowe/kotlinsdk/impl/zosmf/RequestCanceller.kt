//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf

/**
 * Request canceller class to provide a possibility to cancel a [Cancellable] request
 * @property currentRequest the current running request to cancel (will be null until the request is started)
 */
class RequestCanceller {
  var currentRequest: Cancellable? = null

  /** Cancel the current running request if it is present */
  fun cancelCurrentRequest() {
    currentRequest?.cancel()
  }
}