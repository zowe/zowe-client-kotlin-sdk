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

package org.zowe.kotlinsdk.providers.zowe.openssh.datasets

import kotlinx.coroutines.runBlocking
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
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
import org.zowe.kotlinsdk.providers.zowe.ZoweInternalAPI

/**
 * Implementation of Datasets API for SSH to work with datasets and members
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=reference-tsoe-commands-subcommands">TSO/E commands and subcommands</a>
 */
@ZoweInternalAPI
class SshDatasetsAPI(private val requestRunner: RequestRunner) : DatasetsAPI {
  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=subcommands-listds-command">LISTDS command</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=lce-example-1-3">LISTDS command: Example 1</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override fun listDatasets(params: ListDatasetsRequest): ListDatasetsResponse {
    return runBlocking {
      requestRunner.runRequest(params) as ListDatasetsResponse
    }
  }

  /**
   * Get the data set info. Basically, the request to get the single data set with full available attributes
   * @param params [GetDatasetInfoRequest] instance to get parameters for the request from
   * @return [GetDatasetInfoResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override fun getDatasetInfo(params: GetDatasetInfoRequest): GetDatasetInfoResponse {
    return runBlocking {
      requestRunner.runRequest(params) as GetDatasetInfoResponse
    }
  }

  /** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=subcommands-listds-command">LISTDS command</a> */
  @AvailableSince(ZVersion.ZOS_2_1)
  override fun listDatasetMembers(params: ListDatasetMembersRequest): ListDatasetMembersResponse {
    return runBlocking {
      requestRunner.runRequest(params) as ListDatasetMembersResponse
    }
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=tc-oput-copy-mvs-data-set-member-into-zos-unix-file">OPUT - Copy an MVS data set member into a z/OS UNIX file</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=tc-oput-copy-mvs-data-set-member-into-zos-unix-file#tsooput__title__7">OPUT - Copy an MVS data set member into a z/OS UNIX file: Examples</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override fun retrieveDatasetContent(params: RetrieveDatasetContentRequest): RetrieveDatasetContentResponse {
    return runBlocking {
      requestRunner.runRequest(params) as RetrieveDatasetContentResponse
    }
  }

  override fun writeToDataset(params: WriteToDatasetRequest): WriteToDatasetResponse {
    // tsocmd "OGET '/dev/fd0' 'ULADZ.TEST.SSH80.PDS(TEST1)'" <<EOF
    //some test text
    //EOF
    //
    // tsocmd "OGET '/dev/fd0' 'ULADZ.TEST.SSH80.PDS(TEST1)'"
    //
    // OGET '/dev/fd0' 'DSN'
    //
    // To create data set member:
    // Check with LISTDS first if the member already exist
    // OGET '/dev/null' '$dsName'

//    /**
//   * Create a member in a data set.
//   * Will check with "LISTDS" if there is already a member with the same name in the provided data set.
//   * The member is created as the "OGET" operation, that puts '/dev/null' file's content into the member
//   * @param client the SSH client to execute the command with
//   * @return the [SshCreateDatasetResponse] with the resulting status of either the LISTDS command (if error), or OGET command
//   */
//  private fun performCreateMember(client: SSHClient): SshResponse {
//    val sshListDatasetsRequest = SshListDatasetsRequest(
//      connection,
//      mask = dsName,
//      attributesLevel = AttributesLevel.NAME
//    )
//    val sshListDatasetsResponse = sshListDatasetsRequest.execRequest(client)
//
//    if (sshListDatasetsResponse.status.exitStatus == 4) {
//      sshCommand = "tsocmd \"OGET '/dev/null' '$dsName'\""
//
//      client
//        .startSession()
//        .use {
//          val status = performSshPlainRequest(client, it)
//          return SshCreateDatasetResponse(status)
//        }
//    } else if (sshListDatasetsResponse.status.exitStatus == 0) {
//      val status = SshStatus(exitStatus = 8, error = "DUPLICATE MEMBER NAME")
//      return SshCreateDatasetResponse(status)
//    } else {
//      return SshCreateDatasetResponse(sshListDatasetsResponse.status)
//    }
//  }
    // * For members:
    // * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=subcommands-listds-command">LISTDS command</a>
    // * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=command-listds-operands">LISTDS command operands</a>
    // * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=tc-oget-copy-zos-unix-files-into-mvs-data-set">OGET - Copy z/OS UNIX files into an MVS data set</a>
    TODO("Not yet implemented")
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=subcommands-allocate-command">ALLOCATE command</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=ace-example-6-allocate-new-sequential-data-set-space-allocated-in-tracks">Example 6: Allocate a new sequential data set with space allocated in tracks</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override fun createDataset(params: CreateDatasetRequest): CreateDatasetResponse {
    return runBlocking {
      requestRunner.runRequest(params) as CreateDatasetResponse
    }
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=subcommands-delete-command">DELETE command</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=dce-example">DELETE command: Example</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override fun deleteDataset(params: DeleteDatasetRequest): DeleteDatasetResponse {
    return runBlocking {
      requestRunner.runRequest(params) as DeleteDatasetResponse
    }
  }

  override fun renameDataset(params: RenameDatasetRequest): RenameDatasetResponse {
    // RENAME 'OLDNM' 'NEWNM'
    // RENAME 'OLDNM(MEMOLD)' (MEMNEW)
    TODO("Not yet implemented")
  }

  override fun copyDataset(params: CopyDatasetRequest): CopyDatasetResponse {
    // ?
    // SMCOPY FROMDATASET('PSDS') TODATASET('ANOTHERPSDS')
    // SMCOPY FROMDATASET('PDSDS(MEM)') TODATASET('ANOTHERPPDSDS(MEM)')
    // For PDS / PDS/E - copy member by member
    // Avoid processing of RECFM=U datasets
    TODO("Not yet implemented")
  }

  override fun migrateDataset(params: MigrateDatasetRequest): MigrateDatasetResponse {
    // HMIGRATE
    TODO("Not yet implemented")
  }

  override fun recallDataset(params: RecallDatasetRequest): RecallDatasetResponse {
    // HRECALL
    TODO("Not yet implemented")
  }

  override fun deleteDatasetBackupVersion(params: DeleteDatasetBackupVersionRequest): DeleteDatasetBackupVersionResponse {
    // HDELETE
    TODO("Not yet implemented")
  }
}
