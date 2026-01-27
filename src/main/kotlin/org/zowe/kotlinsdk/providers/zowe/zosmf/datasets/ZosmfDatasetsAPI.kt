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

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.core.datasets.api.messaging.*
import org.zowe.kotlinsdk.providers.zowe.HttpRequestRunner
import org.zowe.kotlinsdk.providers.zowe.ZoweInternalAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.*

/**
 * Implementation of Datasets API for z/OSMF REST API to work with datasets and members
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=services-zos-data-set-file-rest-interface">z/OS data set and file REST interface</a>
 */
@ZoweInternalAPI
class ZosmfDatasetsAPI(private val requestRunner: HttpRequestRunner) : DatasetsAPI {

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-zos-data-sets-system">List the z/OS data sets on a system</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-zos-data-sets-system#ListDataSets__title__9">List the z/OS data sets on a system: Example response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun listDatasets(params: ListDatasetsRequest): ZosmfListDatasetsResponse {
    return requestRunner.runRequest(params) as ZosmfListDatasetsResponse
  }

  /**
   * Get the dataset info
   * @param params [GetDatasetInfoRequest] instance to get parameters for the request from
   * @return [GetDatasetInfoResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun getDatasetInfo(params: GetDatasetInfoRequest): ZosmfGetDatasetInfoResponse {
    return requestRunner.runRequest(params) as ZosmfGetDatasetInfoResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-members-zos-data-set">List the members of a z/OS data set</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-members-zos-data-set#ListDataSetMembers__getlist_dsmembers_response__title__1">List the members of a z/OS data set: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun listDatasetMembers(params: ListDatasetMembersRequest): ZosmfListDatasetMembersResponse {
    return requestRunner.runRequest(params) as ZosmfListDatasetMembersResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-data-set-member">Retrieve the contents of a z/OS data set or member</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-data-set-member#GetReadDataSet__title__7">Retrieve the contents of a z/OS data set or member: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun retrieveDatasetContent(params: RetrieveDatasetContentRequest): ZosmfRetrieveDatasetContentResponse {
    return requestRunner.runRequest(params) as ZosmfRetrieveDatasetContentResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-write-data-zos-data-set-member">Write data to a z/OS data set or member</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-write-data-zos-data-set-member#PutWriteDataSet__title__8">Write data to a z/OS data set or member: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun writeToDataset(params: WriteToDatasetRequest): WriteToDatasetResponse {
    return requestRunner.runRequest(params) as ZosmfWriteToDatasetResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-create-sequential-partitioned-data-set">Create a sequential or partitioned data set</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-create-sequential-partitioned-data-set#CreateDataSet__title__10">Create a sequential or partitioned data set: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun createDataset(params: CreateDatasetRequest): ZosmfCreateDatasetResponse {
    return requestRunner.runRequest(params) as ZosmfCreateDatasetResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-sequential-partitioned-data-set">Delete a sequential and partitioned data set</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-partitioned-data-set-member">Delete a partitioned data set member</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-sequential-partitioned-data-set#DeleteDataSet__title__11">Delete a sequential and partitioned data set: Expected response</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-partitioned-data-set-member#DeletepartitionedDataSet__getlist_datasets__title__1">Delete a partitioned data set member: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun deleteDataset(params: DeleteDatasetRequest): ZosmfDeleteDatasetResponse {
    return requestRunner.runRequest(params) as ZosmfDeleteDatasetResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities">z/OS data set and member utilities: 'rename' request</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__10">z/OS data set and member utilities: 'rename' request: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun renameDataset(params: RenameDatasetRequest): ZosmfRenameDatasetResponse {
    return requestRunner.runRequest(params) as ZosmfRenameDatasetResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities">z/OS data set and member utilities: 'copy' request</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__10">z/OS data set and member utilities: 'copy' request: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun copyToDataset(params: CopyToDatasetRequest): ZosmfCopyToDatasetResponse {
    return requestRunner.runRequest(params) as ZosmfCopyToDatasetResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities">z/OS data set and member utilities: 'hmigrate' request</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__10">z/OS data set and member utilities: 'hmigrate' request: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun migrateDataset(params: MigrateDatasetRequest): ZosmfMigrateDatasetResponse {
    return requestRunner.runRequest(params) as ZosmfMigrateDatasetResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities">z/OS data set and member utilities: 'hrecall' request</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__10">z/OS data set and member utilities: 'hrecall' request: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun recallDataset(params: RecallDatasetRequest): ZosmfRecallDatasetResponse {
    return requestRunner.runRequest(params) as ZosmfRecallDatasetResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities">z/OS data set and member utilities: 'hdelete' request</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__10">z/OS data set and member utilities: 'hdelete' request: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun deleteDatasetBackupVersion(params: DeleteDatasetBackupVersionRequest): ZosmfDeleteDatasetBackupVersionResponse {
    return requestRunner.runRequest(params) as ZosmfDeleteDatasetBackupVersionResponse
  }

}