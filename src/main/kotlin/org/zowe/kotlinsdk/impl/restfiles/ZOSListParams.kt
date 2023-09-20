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

import org.zowe.kotlinsdk.impl.zosmf.datasets.data.XIBMMigratedRecall
import org.zowe.kotlinsdk.core.restfiles.XIBMAttr


/** This interface defines the options that can be sent into the list data set function */
class ZOSListParams(
  /** X-IBM-Max-Items header. The indicator that specifies the maximum number of items to return */
  val maxItems: Int? = null,
  /**
   * The volume where the data set resides
   */
  val volume: String? = null,

  /**
   * The indicator that specifies the attribute type
   */
  val attribute: XIBMAttr.Type = XIBMAttr.Type.BASE,



  /**
   * An optional search parameter that specifies the first data set name to return to the response document
   */
  val start: String? = null,

  /**
   * An optional parameter that specifies how to handle migrated data sets
   */
  val recall: XIBMMigratedRecall = XIBMMigratedRecall.WAIT,

  /**
   * An optional pattern for restricting the response list
   */
  val pattern: String? = null,

  /**
   * Response time out value
   */
  val responseTimeout: String? = null,

  /**
   * A boolean parameter that shows total rows property of the attribute
   */
  val returnTotalRows: Boolean = true

)
