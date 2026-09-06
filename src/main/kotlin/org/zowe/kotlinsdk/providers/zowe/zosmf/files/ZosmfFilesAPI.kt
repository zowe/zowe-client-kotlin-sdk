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
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=services-zos-data-set-file-rest-interface">z/OS data set and file REST interface</a>
 */
@ZoweInternalAPI
class ZosmfFilesAPI(private val requestRunner: HttpRequestRunner) : FilesAPI {

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-list-files-directories-unix-file-path">List the files and directories of a UNIX file path</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-list-files-directories-unix-file-path#ListUNIXfiles__title__9">List the files and directories of a UNIX file path: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun listFiles(params: ListFilesRequest): ZosmfListFilesResponse {
    return requestRunner.runRequest(params) as ZosmfListFilesResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-retrieve-contents-zos-unix-file">Retrieve the contents of a z/OS UNIX file</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-retrieve-contents-zos-unix-file#ReadUnixFile__title__8">Retrieve the contents of a z/OS UNIX file: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun retrieveFileContent(params: RetrieveFileContentRequest): ZosmfRetrieveFileContentResponse {
    return requestRunner.runRequest(params) as ZosmfRetrieveFileContentResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-write-data-zos-unix-file">Write data to a z/OS UNIX file</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-write-data-zos-unix-file#PutWriteUnixFile__title__8">Write data to a z/OS UNIX file: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun writeToFile(params: WriteToFileRequest): ZosmfWriteToFileResponse {
    return requestRunner.runRequest(params) as ZosmfWriteToFileResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-create-unix-file-directory">Create a UNIX file or directory</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-create-unix-file-directory#CreateUnixFile__getlist_datasets__title__1">Create a UNIX file or directory: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun createFile(params: CreateFileRequest): ZosmfCreateFileResponse {
    return requestRunner.runRequest(params) as ZosmfCreateFileResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-delete-unix-file-directory">Delete a UNIX file or directory</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-delete-unix-file-directory#DeleteUnixFile__getlist_datasets__title__1">Delete a UNIX file or directory: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun deleteFile(params: DeleteFileRequest): ZosmfDeleteFileResponse {
    return requestRunner.runRequest(params) as ZosmfDeleteFileResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun copyFile(params: CopyFileRequest): ZosmfCopyFileResponse {
    return requestRunner.runRequest(params) as ZosmfCopyFileResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun moveFile(params: MoveFileRequest): ZosmfMoveFileResponse {
    return requestRunner.runRequest(params) as ZosmfMoveFileResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun changeFileMode(params: ChangeFileModeRequest): ZosmfChangeFileModeResponse {
    return requestRunner.runRequest(params) as ZosmfChangeFileModeResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun changeFileOwner(params: ChangeFileOwnerRequest): ZosmfChangeFileOwnerResponse {
    return requestRunner.runRequest(params) as ZosmfChangeFileOwnerResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun changeFileTag(params: ChangeFileTagRequest): ZosmfChangeFileTagResponse {
    return requestRunner.runRequest(params) as ZosmfChangeFileTagResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun fileExtAttributesUtility(params: FileExtAttributesUtilityRequest): ZosmfFileExtAttributesUtilityResponse {
    return requestRunner.runRequest(params) as ZosmfFileExtAttributesUtilityResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun getFileACL(params: GetFileACLRequest): ZosmfGetFileACLResponse {
    return requestRunner.runRequest(params) as ZosmfGetFileACLResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun setFileACL(params: SetFileACLRequest): ZosmfSetFileACLResponse {
    return requestRunner.runRequest(params) as ZosmfSetFileACLResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun linkFile(params: LinkFileRequest): ZosmfLinkFileResponse {
    return requestRunner.runRequest(params) as ZosmfLinkFileResponse
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities">z/OS UNIX file utilities</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=interface-zos-unix-file-utilities#IZUHPINFO_API_PutUnixFileUtilities__title__7">z/OS UNIX file utilities: Expected response</a>
   */
  @AvailableSince(ZVersion.ZOS_2_1)
  override suspend fun unlinkFile(params: UnlinkFileRequest): ZosmfUnlinkFileResponse {
    return requestRunner.runRequest(params) as ZosmfUnlinkFileResponse
  }

}
