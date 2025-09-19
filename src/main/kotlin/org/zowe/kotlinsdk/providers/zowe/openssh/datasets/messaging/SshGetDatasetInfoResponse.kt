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

package org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging

import org.zowe.kotlinsdk.core.datasets.api.messaging.GetDatasetInfoResponse
import org.zowe.kotlinsdk.core.datasets.data.DatasetItem
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus

/**
 * Get data set info SSH request response
 * @property status the SSH status of the command ([SshStatus.OK] if the command is succeeded)
 * @property dataset the data set prefilled object with attributes
 */
class SshGetDatasetInfoResponse(
  override val status: SshStatus = SshStatus.OK,
  override val dataset: DatasetItem
) : GetDatasetInfoResponse, SshResponse
