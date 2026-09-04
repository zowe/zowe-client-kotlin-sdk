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

package org.zowe.kotlinsdk.zowe.client.sdk.zosfiles.input

import org.zowe.kotlinsdk.XIBMAttr

/**
 * This interface defines the options that can be sent into the list data set function
 */
@Deprecated(
  "Scheduled for removal since v1.0.0",
  ReplaceWith("ZosmfListDatasetsRequest", "org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging")
)
class ListParams(

  /**
   * The volume where the data set resides
   */
  val volume: String? = null,

  /**
   * The indicator that specifies the attribute type
   */
  val attribute: XIBMAttr.Type = XIBMAttr.Type.BASE,

  /**
   * The indicator that specifies the maximum number of items to return
   */
  val maxLength: Int? = null,

  /**
   * An optional search parameter that specifies the first data set name to return to the response document
   */
  val start: String? = null,

  /**
   * An optional parameter that specifies how to handle migrated data sets
   */
  val recall: String? = null,

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
