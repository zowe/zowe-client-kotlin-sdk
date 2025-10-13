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

/**
 * Abstraction for a request runner.
 * Provides a single-point way to handle a remote request and produce a remote response asynchronously
 * @property protocol the protocol, supported by the request runner
 */
abstract class RequestRunner(val protocol: SupportedProtocol) {
  /** Run an asynchronous [Request], producing an appropriate [Response] */
  abstract suspend fun runRequest(params: Request): Response
}
