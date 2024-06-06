//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.restfiles

/**
 * This interface defines the options that can be sent into the copy data set function.
 */
class ZOSCopyParams (

  /**
   * The volume to copy from.
   */
  val fromVolser: String ?= null,

  /**
   * The dataset to copy from.
   */
  val fromDataSet: String,

  /**
   * The volume to copy too
   */
  val toVolser: String ?= null,

  /**
   * The dataset to copy too
   */
  val toDataSet: String,

  /**
   * Replace option
   */
  val replace: Boolean = false,

  /**
   * Specified as true to indicate a copying of all members in partial dataset to another partial dataset request
   */
  val copyAllMembers: Boolean = false
)