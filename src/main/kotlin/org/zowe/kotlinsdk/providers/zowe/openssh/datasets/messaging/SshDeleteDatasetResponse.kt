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

import org.zowe.kotlinsdk.core.datasets.api.messaging.DeleteDatasetResponse
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=command-delete-return-codes">DELETE command return codes</a> */
class SshDeleteDatasetResponse(
  override val status: SshStatus = SshStatus.OK
) : SshResponse, DeleteDatasetResponse
