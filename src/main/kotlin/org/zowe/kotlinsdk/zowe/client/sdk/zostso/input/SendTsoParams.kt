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

package org.zowe.kotlinsdk.zowe.client.sdk.zostso.input

/**
 * TSO issue command z/OSMF parameters
 */
data class SendTsoParams(

  /**
   * Servlet key of an active address space.
   */
  val servletKey: String,

  /**
   * Data to be sent to the active address space.
   */
  val data: String
)
