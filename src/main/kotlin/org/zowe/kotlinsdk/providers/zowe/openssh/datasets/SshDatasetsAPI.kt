/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.datasets

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.core.datasets.api.messaging.CopyDatasetRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.CopyDatasetResponse
import org.zowe.kotlinsdk.core.datasets.api.messaging.CreateDatasetRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.DeleteDatasetBackupVersionRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.DeleteDatasetBackupVersionResponse
import org.zowe.kotlinsdk.core.datasets.api.messaging.DeleteDatasetRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.GetDatasetInfoRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.GetDatasetInfoResponse
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetMembersRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetsRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.MigrateDatasetRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.MigrateDatasetResponse
import org.zowe.kotlinsdk.core.datasets.api.messaging.RecallDatasetRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.RecallDatasetResponse
import org.zowe.kotlinsdk.core.datasets.api.messaging.RenameDatasetRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.RetrieveDatasetContentRequest
import org.zowe.kotlinsdk.core.datasets.api.messaging.WriteToDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.SshRequestRunner
import org.zowe.kotlinsdk.providers.zowe.ZoweInternalAPI
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshCreateDatasetResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshDeleteDatasetResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshGetDatasetInfoResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshListDatasetMembersResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshListDatasetsResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshRenameDatasetResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshRetrieveDatasetContentResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshWriteToDatasetResponse

/**
 * Implementation of Datasets API for SSH to work with datasets and members
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=reference-tsoe-commands-subcommands">TSO/E commands and subcommands</a>
 */
@ZoweInternalAPI
class SshDatasetsAPI(private val requestRunner: SshRequestRunner) : DatasetsAPI {
  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=subcommands-listds-command">LISTDS command</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=lce-example-1-3">LISTDS command: Example 1</a>
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun listDatasets(params: ListDatasetsRequest): SshListDatasetsResponse {
    return requestRunner.runRequest(params) as SshListDatasetsResponse
  }

  /**
   * Get the data set info. Basically, the request to get the single data set with full available attributes
   * @param params [GetDatasetInfoRequest] instance to get parameters for the request from
   * @return [GetDatasetInfoResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun getDatasetInfo(params: GetDatasetInfoRequest): SshGetDatasetInfoResponse {
    return requestRunner.runRequest(params) as SshGetDatasetInfoResponse
  }

  /** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=subcommands-listds-command">LISTDS command</a> */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun listDatasetMembers(params: ListDatasetMembersRequest): SshListDatasetMembersResponse {
    return requestRunner.runRequest(params) as SshListDatasetMembersResponse
  }

  /** @see <a href="https://www.ibm.com/docs/en/zos/3.2.0?topic=descriptions-cp-copy-file">cp - Copy a file</a> */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun retrieveDatasetContent(params: RetrieveDatasetContentRequest): SshRetrieveDatasetContentResponse {
    return requestRunner.runRequest(params) as SshRetrieveDatasetContentResponse
  }

  /**
   * Uses a combination of "tsocmd LISTDS" and "cp" commands. "LISTDS" is used to check whether the data set
   * or the data set member exists, "cp" - to write the content
   * @see <a href="https://www.ibm.com/docs/en/zos/3.2.0?topic=descriptions-cp-copy-file">cp - Copy a file</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=subcommands-listds-command">LISTDS command</a>
   */
  override suspend fun writeToDataset(params: WriteToDatasetRequest): SshWriteToDatasetResponse {
    return requestRunner.runRequest(params) as SshWriteToDatasetResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=subcommands-allocate-command">ALLOCATE command</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=ace-example-6-allocate-new-sequential-data-set-space-allocated-in-tracks">Example 6: Allocate a new sequential data set with space allocated in tracks</a>
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun createDataset(params: CreateDatasetRequest): SshCreateDatasetResponse {
    return requestRunner.runRequest(params) as SshCreateDatasetResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=subcommands-delete-command">DELETE command</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=dce-example">DELETE command: Example</a>
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun deleteDataset(params: DeleteDatasetRequest): SshDeleteDatasetResponse {
    return requestRunner.runRequest(params) as SshDeleteDatasetResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.2.0?topic=subcommands-rename-command">RENAME command</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.2.0?topic=command-rename-operands">RENAME command operands</a>
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun renameDataset(params: RenameDatasetRequest): SshRenameDatasetResponse {
    return requestRunner.runRequest(params) as SshRenameDatasetResponse
  }

  override suspend fun copyDataset(params: CopyDatasetRequest): CopyDatasetResponse {
    // <a href="https://www.ibm.com/docs/en/zos/3.2.0?topic=descriptions-cp-copy-file">cp - Copy a file</a>
    // cp
    // see SshRetrieveDatasetContentRequest
    // SMCOPY FROMDATASET('PSDS') TODATASET('ANOTHERPSDS')
    // SMCOPY FROMDATASET('PDSDS(MEM)') TODATASET('ANOTHERPPDSDS(MEM)')
    // For PDS / PDS/E - copy member by member
    // Avoid processing of RECFM=U datasets
    TODO("Not yet implemented")
  }

  override suspend fun migrateDataset(params: MigrateDatasetRequest): MigrateDatasetResponse {
    // HMIGRATE
    TODO("Not yet implemented")
  }

  override suspend fun recallDataset(params: RecallDatasetRequest): RecallDatasetResponse {
    // HRECALL
    TODO("Not yet implemented")
  }

  override suspend fun deleteDatasetBackupVersion(params: DeleteDatasetBackupVersionRequest): DeleteDatasetBackupVersionResponse {
    // HDELETE
    TODO("Not yet implemented")
  }
}
