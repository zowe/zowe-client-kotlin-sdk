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

import io.ktor.http.HttpStatusCode
import net.schmizz.sshj.SSHClient
import org.zowe.kotlinsdk.core.datasets.AttributesLevel
import org.zowe.kotlinsdk.core.datasets.api.messaging.GetDatasetInfoRequest
import org.zowe.kotlinsdk.core.datasets.data.DatasetItem
import org.zowe.kotlinsdk.providers.zowe.SshConnection
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfGetDatasetInfoRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfGetDatasetInfoResponse
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfListDatasetsResponse

// TODO: doc
class SshGetDatasetInfoRequest(
  override val dsName: String,
  override val connection: SshConnection
) : GetDatasetInfoRequest, SshRequest {
  override val sshCommand: String
    get() = throw NotImplementedError("sshCommand should not be triggered here")

  override fun execRequest(client: SSHClient): SshResponse {
    val sshListDatasetsRequest = SshListDatasetsRequest(
      connection,
      mask = dsName,
      attributesLevel = AttributesLevel.FULL
    )
    val sshListDatasetsResponse = sshListDatasetsRequest.execRequest(client)
    return if (sshListDatasetsResponse is SshListDatasetsResponse) {
      val dataset = sshListDatasetsResponse.dsItems.firstOrNull()
    } else {
      SshGetDatasetInfoResponse(

      )
    }


    return if (dataset?.datasetName?.uppercase() == zosmfParams.mask.uppercase())
      ZosmfGetDatasetInfoResponse(HttpStatusCode.OK, dataset)
    else
      ZosmfGetDatasetInfoResponse(
        HttpStatusCode(404, "Dataset for the specified mask: '${zosmfParams.mask}' is not found"),
        object : DatasetItem {
          override val datasetName = "ERROR404"
          override val isMigrated: Boolean? = null
          override val blockSize: Int? = null
          override val datasetOrganization: DatasetItem.DatasetOrganization? = null
          override val recordLength: Int? = null
          override val recordFormat: DatasetItem.RecordFormat? = null
          override val sizeInTracks: Int? = null
          override val spaceUnits: DatasetItem.SpaceUnits? = null
          override val volumeSerial: String? = null
        }
      )
  }
}
