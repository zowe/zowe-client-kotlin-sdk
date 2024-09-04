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

import org.zowe.kotlinsdk.Job

data class MonitorJobWaitForParams(
    val jobId: String? = null,
    val jobName: String? = null,
    val jobStatus: Job.Status? = null,
    var watchDelay: Long? = null,
    var attempts: Int? = null,
    var lineLimit: Int? = null
)
