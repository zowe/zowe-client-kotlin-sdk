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

/**
 * Submit job parameters
 */
class SubmitJobParams(

  /**
   * z/OS data set which should contain syntactically correct JCL
   */
  val jobDataSet: String,

  /**
   * A string for JCL symbolic substitution
   */
  val jclSymbols: String? = null

)
