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
import org.zowe.kotlinsdk.core.RequestRunner
import org.zowe.kotlinsdk.core.files.api.FilesAPI
import org.zowe.kotlinsdk.core.files.api.messaging.ChangeFileModeRequest
import org.zowe.kotlinsdk.core.files.api.messaging.ChangeFileModeResponse
import org.zowe.kotlinsdk.core.files.api.messaging.ChangeFileOwnerRequest
import org.zowe.kotlinsdk.core.files.api.messaging.ChangeFileOwnerResponse
import org.zowe.kotlinsdk.core.files.api.messaging.ChangeFileTagRequest
import org.zowe.kotlinsdk.core.files.api.messaging.ChangeFileTagResponse
import org.zowe.kotlinsdk.core.files.api.messaging.CopyFileRequest
import org.zowe.kotlinsdk.core.files.api.messaging.CopyFileResponse
import org.zowe.kotlinsdk.core.files.api.messaging.CreateFileRequest
import org.zowe.kotlinsdk.core.files.api.messaging.CreateFileResponse
import org.zowe.kotlinsdk.core.files.api.messaging.DeleteFileRequest
import org.zowe.kotlinsdk.core.files.api.messaging.DeleteFileResponse
import org.zowe.kotlinsdk.core.files.api.messaging.GetFileACLRequest
import org.zowe.kotlinsdk.core.files.api.messaging.GetFileACLResponse
import org.zowe.kotlinsdk.core.files.api.messaging.FileExtAttributesUtilityRequest
import org.zowe.kotlinsdk.core.files.api.messaging.FileExtAttributesUtilityResponse
import org.zowe.kotlinsdk.core.files.api.messaging.LinkFileRequest
import org.zowe.kotlinsdk.core.files.api.messaging.LinkFileResponse
import org.zowe.kotlinsdk.core.files.api.messaging.ListFilesRequest
import org.zowe.kotlinsdk.core.files.api.messaging.ListFilesResponse
import org.zowe.kotlinsdk.core.files.api.messaging.MoveFileRequest
import org.zowe.kotlinsdk.core.files.api.messaging.MoveFileResponse
import org.zowe.kotlinsdk.core.files.api.messaging.RetrieveFileContentRequest
import org.zowe.kotlinsdk.core.files.api.messaging.RetrieveFileContentResponse
import org.zowe.kotlinsdk.core.files.api.messaging.SetFileACLRequest
import org.zowe.kotlinsdk.core.files.api.messaging.SetFileACLResponse
import org.zowe.kotlinsdk.core.files.api.messaging.UnlinkFileRequest
import org.zowe.kotlinsdk.core.files.api.messaging.UnlinkFileResponse
import org.zowe.kotlinsdk.core.files.api.messaging.WriteToFileRequest
import org.zowe.kotlinsdk.core.files.api.messaging.WriteToFileResponse
import org.zowe.kotlinsdk.providers.zowe.ZoweInternalAPI

/**
 * Implementation of Files API for z/OSMF REST API to work with USS files and folders
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=services-zos-data-set-file-rest-interface">z/OS data set and file REST interface</a>
 */
@ZoweInternalAPI
class ZosmfFilesAPI(private val requestRunner: RequestRunner) : FilesAPI {

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-files-directories-unix-file-path">List the files and directories of a UNIX file path</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-files-directories-unix-file-path#ListUNIXfiles__title__9">List the files and directories of a UNIX file path: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun listFiles(params: ListFilesRequest): ListFilesResponse {
    return requestRunner.runRequest(params) as ListFilesResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-unix-file">Retrieve the contents of a z/OS UNIX file</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-unix-file#ReadUnixFile__title__8">Retrieve the contents of a z/OS UNIX file: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun retrieveFileContent(params: RetrieveFileContentRequest): RetrieveFileContentResponse {
    return requestRunner.runRequest(params) as RetrieveFileContentResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-write-data-zos-unix-file">Write data to a z/OS UNIX file</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-write-data-zos-unix-file#PutWriteUnixFile__title__8">Write data to a z/OS UNIX file: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun writeToFile(params: WriteToFileRequest): WriteToFileResponse {
    return requestRunner.runRequest(params) as WriteToFileResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-create-unix-file-directory">Create a UNIX file or directory</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-create-unix-file-directory#CreateUnixFile__getlist_datasets__title__1">Create a UNIX file or directory: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun createFile(params: CreateFileRequest): CreateFileResponse {
    return requestRunner.runRequest(params) as CreateFileResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-unix-file-directory">Delete a UNIX file or directory</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-delete-unix-file-directory#DeleteUnixFile__getlist_datasets__title__1">Delete a UNIX file or directory: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun deleteFile(params: DeleteFileRequest): DeleteFileResponse {
    return requestRunner.runRequest(params) as DeleteFileResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun copyFile(params: CopyFileRequest): CopyFileResponse {
    return requestRunner.runRequest(params) as CopyFileResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun moveFile(params: MoveFileRequest): MoveFileResponse {
    return requestRunner.runRequest(params) as MoveFileResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun changeFileMode(params: ChangeFileModeRequest): ChangeFileModeResponse {
    return requestRunner.runRequest(params) as ChangeFileModeResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun changeFileOwner(params: ChangeFileOwnerRequest): ChangeFileOwnerResponse {
    return requestRunner.runRequest(params) as ChangeFileOwnerResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun changeFileTag(params: ChangeFileTagRequest): ChangeFileTagResponse {
    return requestRunner.runRequest(params) as ChangeFileTagResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun fileExtAttributesUtility(params: FileExtAttributesUtilityRequest): FileExtAttributesUtilityResponse {
    return requestRunner.runRequest(params) as FileExtAttributesUtilityResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun getFileACL(params: GetFileACLRequest): GetFileACLResponse {
    return requestRunner.runRequest(params) as GetFileACLResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun setFileACL(params: SetFileACLRequest): SetFileACLResponse {
    return requestRunner.runRequest(params) as SetFileACLResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun linkFile(params: LinkFileRequest): LinkFileResponse {
    return requestRunner.runRequest(params) as LinkFileResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun unlinkFile(params: UnlinkFileRequest): UnlinkFileResponse {
    return requestRunner.runRequest(params) as UnlinkFileResponse
  }

}
