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

import java.time.Instant

/**
 * A resulting status of a request or a command execution.
 * Provides a generic way to analyze a resulting status and log-compatible properties
 * @property type the resulting [StatusType] to consider a request or a command is succeeded or not
 * @property timestamp the timestamp of the status object production
 * @property metadata result status payload to form final status text based on
 * @property text the resulting status text. Usually is used by logs
 */
interface Status {
  val type: StatusType
  val timestamp: Instant
  val metadata: Map<String, Any>?
  val text: String
}
