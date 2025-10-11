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

package org.zowe.kotlinsdk.core.files.api

import org.zowe.kotlinsdk.core.API
import org.zowe.kotlinsdk.core.files.api.messaging.*

// TODO: doc
/** Files API specification to provide functions to work with Unix system services */
interface FilesAPI : API {
  /**
   * List the files and directories
   * @param params [ListFilesRequest] instance to get parameters for the request from
   * @return [ListFilesResponse] instance with the request handling result
   */
  suspend fun listFiles(params: ListFilesRequest): ListFilesResponse

  /**
   * Retrieve the contents of a file
   * @param params [RetrieveFileContentRequest] instance to get parameters for the request from
   * @return [RetrieveFileContentResponse] instance with the request handling result
   */
  fun retrieveFileContent(params: RetrieveFileContentRequest): RetrieveFileContentResponse

  /**
   * Write content to the file
   * @param params [WriteToFileRequest] instance to get parameters for the request from
   * @return [WriteToFileResponse] instance with the request handling result
   */
  fun writeToFile(params: WriteToFileRequest): WriteToFileResponse

  /**
   * Create a file or directory
   * @param params [CreateFileRequest] instance to get parameters for the request from
   * @return [CreateFileResponse] instance with the request handling result
   */
  fun createFile(params: CreateFileRequest): CreateFileResponse

  /**
   * Delete a file or directory
   * @param params [DeleteFileRequest] instance to get parameters for the request from
   * @return [DeleteFileResponse] instance with the request handling result
   */
  fun deleteFile(params: DeleteFileRequest): DeleteFileResponse

  /**
   * Copy file or directory
   * @param params [CopyFileRequest] instance to get parameters for the request from
   * @return [CopyFileResponse] instance with the request handling result
   */
  fun copyFile(params: CopyFileRequest): CopyFileResponse

  /**
   * Move file or directory
   * @param params [MoveFileRequest] instance to get parameters for the request from
   * @return [MoveFileResponse] instance with the request handling result
   */
  fun moveFile(params: MoveFileRequest): MoveFileResponse

  /**
   * Change file mode
   * @param params [ChangeFileModeRequest] instance to get parameters for the request from
   * @return [ChangeFileModeResponse] instance with the request handling result
   */
  fun changeFileMode(params: ChangeFileModeRequest): ChangeFileModeResponse

  /**
   * Change file owner
   * @param params [ChangeFileOwnerRequest] instance to get parameters for the request from
   * @return [ChangeFileOwnerResponse] instance with the request handling result
   */
  fun changeFileOwner(params: ChangeFileOwnerRequest): ChangeFileOwnerResponse

  /**
   * Change file tag
   * @param params [ChangeFileTagRequest] instance to get parameters for the request from
   * @return [ChangeFileTagResponse] instance with the request handling result
   */
  fun changeFileTag(params: ChangeFileTagRequest): ChangeFileTagResponse

  /**
   * Modify and retrieve extended attributes of a file
   * @param params [FileExtAttributesUtilityRequest] instance to get parameters for the request from
   * @return [FileExtAttributesUtilityResponse] instance with the request handling result
   */
  fun fileExtAttributesUtility(params: FileExtAttributesUtilityRequest): FileExtAttributesUtilityResponse

  /**
   * Retrieve file or directory access control list
   * @param params [GetFileACLRequest] instance to get parameters for the request from
   * @return [GetFileACLResponse] instance with the request handling result
   */
  fun getFileACL(params: GetFileACLRequest): GetFileACLResponse

  /**
   * Set (replace), modify, or remove the access control list (ACL) to regular files and directories
   * @param params [SetFileACLRequest] instance to get parameters for the request from
   * @return [SetFileACLResponse] instance with the request handling result
   */
  fun setFileACL(params: SetFileACLRequest): SetFileACLResponse

  /**
   * Link file or directory
   * @param params [LinkFileRequest] instance to get parameters for the request from
   * @return [LinkFileResponse] instance with the request handling result
   */
  fun linkFile(params: LinkFileRequest): LinkFileResponse

  /**
   * Unlink file or directory
   * @param params [UnlinkFileRequest] instance to get parameters for the request from
   * @return [UnlinkFileResponse] instance with the request handling result
   */
  fun unlinkFile(params: UnlinkFileRequest): UnlinkFileResponse
}
