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

package org.zowe.kotlinsdk.providers.zowe.ssh.datasets

import kotlinx.coroutines.runBlocking
import org.zowe.kotlinsdk.core.RequestRunner
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.core.datasets.api.messaging.CopyDatasetRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.CopyDatasetResponse
import org.zowe.kotlinsdk.core.datasets.api.messaging.CreateDatasetRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.CreateDatasetResponse
import org.zowe.kotlinsdk.core.datasets.api.messaging.DeleteDatasetBackupVersionRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.DeleteDatasetBackupVersionResponse
import org.zowe.kotlinsdk.core.datasets.api.messaging.DeleteDatasetRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.DeleteDatasetResponse
import org.zowe.kotlinsdk.core.datasets.api.messaging.GetDatasetInfoRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.GetDatasetInfoResponse
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetMembersRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetMembersResponse
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetsRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetsResponse
import org.zowe.kotlinsdk.core.datasets.api.messaging.MigrateDatasetRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.MigrateDatasetResponse
import org.zowe.kotlinsdk.core.datasets.api.messaging.RecallDatasetRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.RecallDatasetResponse
import org.zowe.kotlinsdk.core.datasets.api.messaging.RenameDatasetRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.RenameDatasetResponse
import org.zowe.kotlinsdk.core.datasets.api.messaging.RetrieveDatasetContentRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.RetrieveDatasetContentResponse
import org.zowe.kotlinsdk.core.datasets.api.messaging.WriteToDatasetRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.WriteToDatasetResponse

// TODO: OptIn mechanism
// TODO: doc
class SshDatasetsAPI(private val requestRunner: RequestRunner) : DatasetsAPI {

  // TODO: doc
  override fun listDatasets(params: ListDatasetsRequest): ListDatasetsResponse {
    return runBlocking {
      requestRunner.runRequest(params) as ListDatasetsResponse
    }
  }

  override fun getDatasetInfo(params: GetDatasetInfoRequest): GetDatasetInfoResponse {
    TODO("Not yet implemented")
  }

  override fun listDatasetMembers(params: ListDatasetMembersRequest): ListDatasetMembersResponse {
    TODO("Not yet implemented")
  }

  override fun retrieveDatasetContent(params: RetrieveDatasetContentRequest): RetrieveDatasetContentResponse {
    TODO("Not yet implemented")
  }

  override fun writeToDataset(params: WriteToDatasetRequest): WriteToDatasetResponse {
    TODO("Not yet implemented")
  }

  override fun createDataset(params: CreateDatasetRequest): CreateDatasetResponse {
    TODO("Not yet implemented")
  }

  override fun deleteDataset(params: DeleteDatasetRequest): DeleteDatasetResponse {
    TODO("Not yet implemented")
  }

  override fun renameDataset(params: RenameDatasetRequest): RenameDatasetResponse {
    TODO("Not yet implemented")
  }

  override fun copyDataset(params: CopyDatasetRequest): CopyDatasetResponse {
    TODO("Not yet implemented")
  }

  override fun migrateDataset(params: MigrateDatasetRequest): MigrateDatasetResponse {
    TODO("Not yet implemented")
  }

  override fun recallDataset(params: RecallDatasetRequest): RecallDatasetResponse {
    TODO("Not yet implemented")
  }

  override fun deleteDatasetBackupVersion(params: DeleteDatasetBackupVersionRequest): DeleteDatasetBackupVersionResponse {
    TODO("Not yet implemented")
  }
}
