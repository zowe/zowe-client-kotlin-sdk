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

import org.zowe.kotlinsdk.RequestVersion

/**
 * ModifyJobParams APIs parameters interface for delete and cancel job operations
 */
class ModifyJobParams (

  /**
   * Job name value specified for request
   */
  val jobName: String,

  /**
   * Job id value specified for request
   */
  val jobId: String,

  /**
   * Version value specified for request
   */
  val version: RequestVersion? = null
)
