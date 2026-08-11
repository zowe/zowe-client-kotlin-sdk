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
import org.zowe.kotlinsdk.core.datasets.api.messaging.RenameDatasetResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.ZosmfHttpResponse

/** @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__10">z/OS data set and member utilities: Expected response</a> */
class ZosmfRenameDatasetResponse(override var status: Status) : ZosmfHttpResponse(), RenameDatasetResponse
