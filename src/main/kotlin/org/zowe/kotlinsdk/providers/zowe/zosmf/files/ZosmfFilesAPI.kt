/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.files

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.files.api.FilesAPI
import org.zowe.kotlinsdk.core.files.api.messaging.*
import org.zowe.kotlinsdk.providers.zowe.HttpRequestRunner
import org.zowe.kotlinsdk.providers.zowe.ZoweInternalAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.messaging.*

/**
 * Implementation of Files API for z/OSMF REST API to work with USS files and folders
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=services-zos-data-set-file-rest-interface">z/OS data set and file REST interface</a>
 */
@ZoweInternalAPI
class ZosmfFilesAPI(private val requestRunner: HttpRequestRunner) : FilesAPI {

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-files-directories-unix-file-path">List the files and directories of a UNIX file path</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-files-directories-unix-file-path#ListUNIXfiles__title__9">List the files and directories of a UNIX file path: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun listFiles(params: ListFilesRequest): ZosmfListFilesResponse {
    return requestRunner.runRequest(params) as ZosmfListFilesResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.2.0?topic=interface-retrieve-contents-zos-unix-file">Retrieve the contents of a z/OS UNIX file</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.2.0?topic=interface-retrieve-contents-zos-unix-file#ReadUnixFile__title__8">Retrieve the contents of a z/OS UNIX file: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun retrieveFileContent(params: RetrieveFileContentRequest): ZosmfRetrieveFileContentResponse {
    return requestRunner.runRequest(params) as ZosmfRetrieveFileContentResponse
  }

  override suspend fun writeToFile(params: WriteToFileRequest): WriteToFileResponse {
    TODO("Not yet implemented")
  }

  override suspend fun createFile(params: CreateFileRequest): CreateFileResponse {
    TODO("Not yet implemented")
  }

  override suspend fun deleteFile(params: DeleteFileRequest): DeleteFileResponse {
    TODO("Not yet implemented")
  }

  override suspend fun copyFile(params: CopyFileRequest): CopyFileResponse {
    TODO("Not yet implemented")
  }

  override suspend fun moveFile(params: MoveFileRequest): MoveFileResponse {
    TODO("Not yet implemented")
  }

  override suspend fun changeFileMode(params: ChangeFileModeRequest): ChangeFileModeResponse {
    TODO("Not yet implemented")
  }

  override suspend fun changeFileOwner(params: ChangeFileOwnerRequest): ChangeFileOwnerResponse {
    TODO("Not yet implemented")
  }

  override suspend fun changeFileTag(params: ChangeFileTagRequest): ChangeFileTagResponse {
    TODO("Not yet implemented")
  }

  override suspend fun fileExtAttributesUtility(params: FileExtAttributesUtilityRequest): FileExtAttributesUtilityResponse {
    TODO("Not yet implemented")
  }

  override suspend fun getFileACL(params: GetFileACLRequest): GetFileACLResponse {
    TODO("Not yet implemented")
  }

  override suspend fun setFileACL(params: SetFileACLRequest): SetFileACLResponse {
    TODO("Not yet implemented")
  }

  override suspend fun linkFile(params: LinkFileRequest): LinkFileResponse {
    TODO("Not yet implemented")
  }

  override suspend fun unlinkFile(params: UnlinkFileRequest): UnlinkFileResponse {
    TODO("Not yet implemented")
  }

}
