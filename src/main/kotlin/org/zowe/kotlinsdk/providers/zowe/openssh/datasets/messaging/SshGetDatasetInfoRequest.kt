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

import net.schmizz.sshj.SSHClient
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.AttributesLevel
import org.zowe.kotlinsdk.core.datasets.api.messaging.GetDatasetInfoRequest
import org.zowe.kotlinsdk.providers.zowe.SshConnection
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.definitions.SshDatasetItem

/**
 * Get dataset info SSH request.
 * Basically makes listDatasets request with full attributes and returns the first entity
 * @property connection the SSH connection object
 */
class SshGetDatasetInfoRequest(
  override val connection: SshConnection,

  /** Data set name to find the data set by */
  @property:AvailableSince(ZVersion.ZOS_2_1) override val dsName: String
) : GetDatasetInfoRequest, SshRequest {
  override var sshCommand: String = ""

  /**
   * Executes the listDatasets SSH request, produces the correct response basing on the previously returned response
   * @param client the SSH connected client to execute the request
   * @return the [SshGetDatasetInfoResponse] object
   */
  override fun execRequest(client: SSHClient): SshResponse {
    val sshListDatasetsRequest = SshListDatasetsRequest(
      connection,
      mask = dsName,
      attributesLevel = AttributesLevel.FULL
    )
    val sshListDatasetsResponse = sshListDatasetsRequest.execRequest(client)
    return if (sshListDatasetsResponse is SshListDatasetsResponse) {
      val status = sshListDatasetsResponse.status
      val dataset = sshListDatasetsResponse.dsItems.firstOrNull()
      if (dataset?.datasetName?.uppercase() == dsName.uppercase())
        SshGetDatasetInfoResponse(status, dataset)
      else
        SshGetDatasetInfoResponse(
          SshStatus(exitStatus = 8, output = "DATASET '$dsName' IS NOT FOUND"),
          SshDatasetItem("ERROR404")
        )
    } else {
      SshGetDatasetInfoResponse(
        SshStatus(1, output = "RESPONSE OBJECT IS NOT CORRECT"),
        SshDatasetItem("ERROR404")
      )
    }
  }
}
