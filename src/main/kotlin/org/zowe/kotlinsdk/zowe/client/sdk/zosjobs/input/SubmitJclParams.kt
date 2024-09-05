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

package org.zowe.kotlinsdk.zowe.client.sdk.zosjobs.input

import org.zowe.kotlinsdk.Intrdr_Recfm

/**
 * Submit jcl parameters
 */
class SubmitJclParams(

  /**
   * JCL to submit which should contain syntactically correct JCL
   */
  val jcl: String,

  /**
   * Specify internal reader RECFM and corresponding http(s) headers will be appended to the request accordingly
   * "F" (fixed) or "V" (variable)
   */
  val internalReaderRecfm: Intrdr_Recfm,

  /**
   * Specify internal reader LRECL and corresponding http(s) headers will be appended to the request accordingly
   * "F" (fixed) or "V" (variable)
   */
  val internalReaderLrecl: String,

  /**
   * A string for JCL symbolic substitution
   */
  val jclSymbols: String? = null
)
