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
 * Interface for various GetJobs APIs
 */
class GetJobParams (

  /**
   * Owner for which to obtain jobs for.
   */
  val owner: String? = null,

  /**
   * Prefix to filter when obtaining jobs.
   */
  val prefix: String? = null,

  /**
   * Max jobs to return in a list
   */
  val maxJobs: Int? = null,

  /**
   * job id for a job
   */
  val jobId: String? = null

)
