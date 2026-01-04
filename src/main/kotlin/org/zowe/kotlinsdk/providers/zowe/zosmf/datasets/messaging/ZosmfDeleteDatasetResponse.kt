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
import org.zowe.kotlinsdk.core.datasets.api.messaging.DeleteDatasetResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-sequential-partitioned-data-set#DeleteDataSet__getlist_datasets__title__1">Delete a sequential and partitioned data set: Expected response</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-partitioned-data-set-member#DeletepartitionedDataSet__getlist_datasets__title__1">Delete a partitioned data set member: Expected response</a>
 */
class ZosmfDeleteDatasetResponse(override var status: Status) : ZosmfHttpResponse(), DeleteDatasetResponse
