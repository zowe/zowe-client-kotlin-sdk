/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.RequestRunner
import org.zowe.kotlinsdk.core.datasets.data.DatasetItem
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.core.datasets.api.messaging.*
import org.zowe.kotlinsdk.providers.zowe.ZoweInternalAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.*

/**
 * Implementation of Datasets API for z/OSMF REST API to work with datasets and members
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=services-zos-data-set-file-rest-interface">z/OS data set and file REST interface</a>
 */
@ZoweInternalAPI
class ZosmfDatasetsAPI(private val requestRunner: RequestRunner) : DatasetsAPI {

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-zos-data-sets-system">List the z/OS data sets on a system</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-zos-data-sets-system#ListDataSets__title__9">List the z/OS data sets on a system: Example response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun listDatasets(params: ListDatasetsRequest): ListDatasetsResponse {
    return requestRunner.runRequest(params) as ListDatasetsResponse
  }

  /**
   * Get the dataset info
   * @param params [GetDatasetInfoRequest] instance to get parameters for the request from
   * @return [GetDatasetInfoResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun getDatasetInfo(params: GetDatasetInfoRequest): GetDatasetInfoResponse {
    val zosmfParams = (params as ZosmfGetDatasetInfoRequest).toListDatasetsRequest()
    val listDatasetsResponse = listDatasets(zosmfParams)
    val dataset = listDatasetsResponse.dsItems.firstOrNull()
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

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-members-zos-data-set">List the members of a z/OS data set</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-members-zos-data-set#ListDataSetMembers__getlist_dsmembers_response__title__1">List the members of a z/OS data set: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun listDatasetMembers(params: ListDatasetMembersRequest): ListDatasetMembersResponse {
    return runBlocking {
      requestRunner.runRequest(params) as ListDatasetMembersResponse
    }
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-data-set-member">Retrieve the contents of a z/OS data set or member</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-data-set-member#GetReadDataSet__title__7">Retrieve the contents of a z/OS data set or member: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun retrieveDatasetContent(params: RetrieveDatasetContentRequest): RetrieveDatasetContentResponse {
    return runBlocking {
      requestRunner.runRequest(params) as RetrieveDatasetContentResponse
    }
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-write-data-zos-data-set-member">Write data to a z/OS data set or member</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-write-data-zos-data-set-member#PutWriteDataSet__title__8">Write data to a z/OS data set or member: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun writeToDataset(params: WriteToDatasetRequest): WriteToDatasetResponse {
    return runBlocking {
      requestRunner.runRequest(params) as WriteToDatasetResponse
    }
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-create-sequential-partitioned-data-set">Create a sequential or partitioned data set</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-create-sequential-partitioned-data-set#CreateDataSet__title__10">Create a sequential or partitioned data set: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun createDataset(params: CreateDatasetRequest): CreateDatasetResponse {
    return runBlocking {
      requestRunner.runRequest(params) as CreateDatasetResponse
    }
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-sequential-partitioned-data-set">Delete a sequential and partitioned data set</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-partitioned-data-set-member">Delete a partitioned data set member</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-sequential-partitioned-data-set#DeleteDataSet__title__11">Delete a sequential and partitioned data set: Expected response</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-partitioned-data-set-member#DeletepartitionedDataSet__getlist_datasets__title__1">Delete a partitioned data set member: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun deleteDataset(params: DeleteDatasetRequest): DeleteDatasetResponse {
    return runBlocking {
      requestRunner.runRequest(params) as DeleteDatasetResponse
    }
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities">z/OS data set and member utilities: 'rename' request</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__10">z/OS data set and member utilities: 'rename' request: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun renameDataset(params: RenameDatasetRequest): RenameDatasetResponse {
    return runBlocking {
      requestRunner.runRequest(params) as RenameDatasetResponse
    }
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities">z/OS data set and member utilities: 'copy' request</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__10">z/OS data set and member utilities: 'copy' request: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun copyDataset(params: CopyDatasetRequest): CopyDatasetResponse {
    return runBlocking {
      requestRunner.runRequest(params) as CopyDatasetResponse
    }
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities">z/OS data set and member utilities: 'hmigrate' request</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__10">z/OS data set and member utilities: 'hmigrate' request: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun migrateDataset(params: MigrateDatasetRequest): MigrateDatasetResponse {
    return runBlocking {
      requestRunner.runRequest(params) as MigrateDatasetResponse
    }
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities">z/OS data set and member utilities: 'hrecall' request</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__10">z/OS data set and member utilities: 'hrecall' request: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun recallDataset(params: RecallDatasetRequest): RecallDatasetResponse {
    return runBlocking {
      requestRunner.runRequest(params) as RecallDatasetResponse
    }
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities">z/OS data set and member utilities: 'hdelete' request</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__10">z/OS data set and member utilities: 'hdelete' request: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun deleteDatasetBackupVersion(params: DeleteDatasetBackupVersionRequest): DeleteDatasetBackupVersionResponse {
    return runBlocking {
      requestRunner.runRequest(params) as DeleteDatasetBackupVersionResponse
    }
  }

}