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
 * TSO start command z/OSMF parameters
 */
data class StartTsoParams(

  /**
   * User's z/OS permission account number
   */
  val account: String? = null,

  /**
   * Character set for address space
   */
  val characterSet: String? = null,

  /**
   * Code page for tso address space
   */
  val codePage: String? = null,

  /**
   * Number of columns
   */
  val columns: String? = null,

  /**
   * Name of the logonProcedure for address space
   */
  val logonProcedure: String? = null,

  /**
   * Region size for tso address space
   */
  val regionSize: String? = null,

  /**
   * Number of rows
   */
  val rows: String? = null
)
