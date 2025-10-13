/*
 * Copyright (c) 2024 IBA Group.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   IBA Group
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

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
  suspend fun listDatasets(params: ListDatasetsRequest): ListDatasetsResponse

  /**
   * Get the dataset's info
   * @param params [GetDatasetInfoRequest] instance to get parameters for the request from
   * @return [GetDatasetInfoResponse] instance with the succeeded request result
   */
  suspend fun getDatasetInfo(params: GetDatasetInfoRequest): GetDatasetInfoResponse

  /**
   * List the dataset's members
   * @param params [ListDatasetMembersRequest] instance to get parameters for the request from
   * @return [ListDatasetMembersResponse] instance with the succeeded request result
   */
  suspend fun listDatasetMembers(params: ListDatasetMembersRequest): ListDatasetMembersResponse

  /**
   * Retrieve the dataset's content
   * @param params [RetrieveDatasetContentRequest] instance to get parameters for the request from
   * @return [RetrieveDatasetContentResponse] instance with the succeeded request result
   */
  suspend fun retrieveDatasetContent(params: RetrieveDatasetContentRequest): RetrieveDatasetContentResponse

  /**
   * Writes content to the dataset
   * @param params [WriteToDatasetRequest] instance to get parameters for the request from
   * @return [WriteToDatasetResponse] instance with the request result
   */
  suspend fun writeToDataset(params: WriteToDatasetRequest): WriteToDatasetResponse

  /**
   * Create a sequential or partitioned dataset
   * @param params [CreateDatasetRequest] instance to get parameters for the request from
   * @return [CreateDatasetResponse] instance with the request result
   */
  suspend fun createDataset(params: CreateDatasetRequest): CreateDatasetResponse

  /**
   * Deletes a sequential and partitioned dataset
   * @param params [DeleteDatasetRequest] instance to get parameters for the request from
   * @return [DeleteDatasetResponse] instance with the request result
   */
  suspend fun deleteDataset(params: DeleteDatasetRequest): DeleteDatasetResponse

  /**
   * Renames dataset
   * @param params [RenameDatasetRequest] instance to get parameters for the request from
   * @return [RenameDatasetResponse] instance with the request result
   */
  suspend fun renameDataset(params: RenameDatasetRequest): RenameDatasetResponse

  /**
   * Copy dataset
   * @param params [CopyDatasetRequest] instance to get parameters for the request from
   * @return [CopyDatasetResponse] instance with the request result
   */
  suspend fun copyDataset(params: CopyDatasetRequest): CopyDatasetResponse

  /**
   * Migrates dataset
   * @param params [MigrateDatasetRequest] instance to get parameters for the request from
   * @return [MigrateDatasetResponse] instance with the request result
   */
  suspend fun migrateDataset(params: MigrateDatasetRequest): MigrateDatasetResponse

  /**
   * Recalls a migrated dataset
   * @param params [RecallDatasetRequest] instance to get parameters for the request from
   * @return [RecallDatasetResponse] instance with the request result
   */
  suspend fun recallDataset(params: RecallDatasetRequest): RecallDatasetResponse

  /**
   * Deletes a backup version of a dataset
   * @param params [DeleteDatasetBackupVersionRequest] instance to get parameters for the request from
   * @return [DeleteDatasetBackupVersionResponse] instance with the request result
   */
  suspend fun deleteDatasetBackupVersion(params: DeleteDatasetBackupVersionRequest): DeleteDatasetBackupVersionResponse
}
