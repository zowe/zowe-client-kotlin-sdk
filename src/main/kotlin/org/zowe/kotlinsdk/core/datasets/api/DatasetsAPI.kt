// Copyright (c) 2024 IBA Group.
//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Contributors:
//   IBA Group
//   Zowe Community

package org.zowe.kotlinsdk.core.datasets.api

import org.zowe.kotlinsdk.core.API
import org.zowe.kotlinsdk.core.datasets.api.messaging.*

/** Datasets API specification to provide functions to work with datasets  */
interface DatasetsAPI : API {

  /**
   * List datasets by the provided parameters
   * @param params [ListDatasetsRequest] instance to get parameters for the request from
   * @return [ListDatasetsResponse] instance with the succeeded request result
   */
  fun listDatasets(params: ListDatasetsRequest): ListDatasetsResponse

  /**
   * Get the dataset's info
   * @param params [GetDatasetInfoRequest] instance to get parameters for the request from
   * @return [GetDatasetInfoResponse] instance with the succeeded request result
   */
  fun getDatasetInfo(params: GetDatasetInfoRequest): GetDatasetInfoResponse

  /**
   * List the dataset's members
   * @param params [ListDatasetMembersRequest] instance to get parameters for the request from
   * @return [ListDatasetMembersResponse] instance with the succeeded request result
   */
  fun listDatasetMembers(params: ListDatasetMembersRequest): ListDatasetMembersResponse

  /**
   * Retrieve the dataset's content
   * @param params [RetrieveDatasetContentRequest] instance to get parameters for the request from
   * @return [RetrieveDatasetContentResponse] instance with the succeeded request result
   */
  fun retrieveDatasetContent(params: RetrieveDatasetContentRequest): RetrieveDatasetContentResponse

  /**
   * Writes content to the dataset
   * @param params [WriteToDatasetRequest] instance to get parameters for the request from
   */
  fun writeToDataset(params: WriteToDatasetRequest)

  /**
   * Create a sequential or partitioned dataset
   * @param params [CreateDatasetRequest] instance to get parameters for the request from
   */
  fun createDataset(params: CreateDatasetRequest)

  /**
   * Deletes a sequential and partitioned dataset
   * @param params [DeleteDatasetRequest] instance to get parameters for the request from
   */
  fun deleteDataset(params: DeleteDatasetRequest)

  /**
   * Renames dataset
   * @param params [RenameDatasetRequest] instance to get parameters for the request from
   */
  fun renameDataset(params: RenameDatasetRequest)

  /**
   * Copy dataset
   * @param params [CopyDatasetRequest] instance to get parameters for the request from
   */
  fun copyDataset(params: CopyDatasetRequest)

  /**
   * Copy member
   * @param params [CopyMemberRequest] instance to get parameters for the request from
   */
  fun copyMember(params: CopyMemberRequest)

  /**
   * Migrates dataset
   * @param params [MigrateDatasetRequest] instance to get parameters for the request from
   */
  fun migrateDataset(params: MigrateDatasetRequest)

  /**
   * Recalls a migrated dataset
   * @param params [RecallDatasetRequest] instance to get parameters for the request from
   */
  fun recallDataset(params: RecallDatasetRequest)

  /**
   * Deletes a backup version of a dataset
   * @param params [DeleteDatasetBackupVersionRequest] instance to get parameters for the request from
   */
  fun deleteDatasetBackupVersion(params: DeleteDatasetBackupVersionRequest)
}
