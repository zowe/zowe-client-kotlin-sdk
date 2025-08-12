/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.providers.zowe.ssh.datasets.messaging

import org.zowe.kotlinsdk.core.datasets.api.messaging.GetDatasetInfoResponse
import org.zowe.kotlinsdk.core.datasets.data.DatasetItem
import org.zowe.kotlinsdk.providers.zowe.SshResponse

// TODO: doc
class SshGetDatasetInfoResponse(override val dataset: DatasetItem) : GetDatasetInfoResponse, SshResponse
