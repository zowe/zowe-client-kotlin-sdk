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
 * Type of the API wrapper. Indicates the way of working with server,
 * depending on which the appropriate request/response implementations are used
 */
enum class WrapperType {
  ZOSMF, SSH_NATIVE
}