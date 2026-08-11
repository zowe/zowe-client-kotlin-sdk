/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging

import org.zowe.kotlinsdk.core.Status
import org.zowe.kotlinsdk.core.datasets.api.messaging.WriteToDatasetResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse

/** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-write-data-zos-data-set-member#PutWriteDataSet__title__8">Write data to a z/OS data set or member: Expected response</a> */
class ZosmfWriteToDatasetResponse(override var status: Status) : ZosmfHttpResponse(), WriteToDatasetResponse
