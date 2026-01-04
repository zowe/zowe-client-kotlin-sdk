/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging

import net.schmizz.sshj.SSHClient
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.datasets.AttributesLevel
import org.zowe.kotlinsdk.core.datasets.api.messaging.GetDatasetInfoRequest
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import org.zowe.kotlinsdk.core.datasets.data.DatasetItem
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.definitions.SshDatasetItem

/**
 * Get dataset info SSH request.
 * Basically makes listDatasets request with full attributes and returns the first entity
 * @property connection the SSH connection object
 * @property dsName data set name to find the data set by
 */
class SshGetDatasetInfoRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) override val dsName: String
) : GetDatasetInfoRequest, SshRequest {
  override var sshCommand: String = ""

  /**
   * The data set, found with listDatasets SSH command. If not changed - is considered as an empty "_INERROR" data set
   */
  private var foundDataset: DatasetItem = SshDatasetItem("_INERROR")

  override suspend fun produceResponseObject(clientResponse: Any): SshResponse {
    return SshGetDatasetInfoResponse(clientResponse as SshCmdResponse, foundDataset)
  }

  /**
   * Executes the listDatasets SSH request, produces the correct response basing on the previously returned response
   * @param client the SSH connected client to execute the request
   * @return the [SshGetDatasetInfoResponse] object
   */
  override suspend fun execSshRequest(client: SSHClient): SshResponse {
    val sshListDatasetsRequest = SshListDatasetsRequest(
      connection,
      mask = dsName,
      attributesLevel = AttributesLevel.FULL
    )
    val sshListDatasetsResponse = sshListDatasetsRequest.execSshRequest(client) as SshListDatasetsResponse
    val status = sshListDatasetsResponse.status
    return if (status.type == StatusType.SUCCESS) {
      val dataset = sshListDatasetsResponse.dsItems.firstOrNull()
      if (dataset != null && dataset.datasetName.equals(dsName, ignoreCase = true)) {
        foundDataset = dataset
        produceResponseObject(SshCmdResponse())
      } else {
        produceResponseObject(SshCmdResponse(8, output = "DATASET '$dsName' IS NOT FOUND"))
      }
    } else {
      produceResponseObject(
        SshCmdResponse(
          1,
          output = "IMPOSSIBLE TO GET DATASET INFO FOR '$dsName' AS LIST DATASETS REQUEST IS FAILED: ${status.text}"
        )
      )
    }
  }
}
