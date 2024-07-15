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

package org.zowe.kotlinsdk.core.files.api

import org.zowe.kotlinsdk.core.API
import org.zowe.kotlinsdk.core.files.api.messaging.*

/**
 * Files API specification to provide functions to work with UNIX file system
 * */
interface FilesAPI : API {

  /**
   * List the files and directories
   * @param params [ListFilesRequest] instance to get parameters for the request from
   * @return [ListFilesResponse] instance with the succeeded request result
   * */
  fun listFiles(params: ListFilesRequest): ListFilesResponse

  /**
   * Retrieve the contents of a file
   * @param params [RetrieveFileContentRequest] instance to get parameters for the request from
   * @return [RetrieveFileContentResponse] instance with the succeeded request result
   * */
  fun retrieveFileContent(params: RetrieveFileContentRequest): RetrieveFileContentResponse

  /**
   * Write content to the file
   * @param params [WriteToFileRequest] instance to get parameters for the request from
   * */
  fun writeToUssFile(params: WriteToFileRequest)

  /**
   * Create a file or directory
   * @param params [CreateFileRequest] instance to get parameters for the request from
   * */
  fun createFile(params: CreateFileRequest)

  /**
   * Delete a file or directory
   * @param params [DeleteFileRequest] instance to get parameters for the request from
   * */
  fun deleteFile(params: DeleteFileRequest)

  /** Change file mode
   * @param params [ChangeFileModeRequest] instance to get parameters for the request from
   * */
  fun changeFileMode(params: ChangeFileModeRequest)

  /**
   * Change file owner
   * @param params [ChangeFileOwnerRequest] instance to get parameters for the request from
   * */
  fun changeFileOwner(params: ChangeFileOwnerRequest)

  /**
   * Change file tag
   * @param params [ChangeFileTagRequest] instance to get parameters for the request from
   * @return [ChangeFileTagResponse] instance with the succeeded request result
   * */
  fun changeFileTag(params: ChangeFileTagRequest): ChangeFileTagResponse

  /**
   * Copy file or directory
   * @param params [CopyFileRequest] instance to get parameters for the request from
   * */
  fun copyFile(params: CopyFileRequest)

  /**
   * Copy file or directory from dataset
   * @param params [CopyFileFromDatasetRequest] instance to get parameters for the request from
   * */
  fun copyFileFromDataset(params: CopyFileFromDatasetRequest)

  /**
   * Modify and retrieve extended attributes of a file
   * @param params [ExtendedAttributesRequest] instance to get parameters for the request from
   * @return [ExtendedAttributesResponse] instance with the succeeded request result
   * */
  fun extendedAttributes(params: ExtendedAttributesRequest): ExtendedAttributesResponse

  /**
   * Retrieve file or directory access control list
   * @param params [GetFileACLRequest] instance to get parameters for the request from
   * @return [GetFileACLResponse] instance with the succeeded request result
   * */
  fun getFileACL(params: GetFileACLRequest): GetFileACLResponse

  /**
   * Move file or directory
   * @param params [MoveFileRequest] instance to get parameters for the request from
   * */
  fun moveFile(params: MoveFileRequest)

  /**
   * Set (replace), modify, or remove the access control list (ACL) to regular files and directories
   * @param params [SetFileACLRequest] instance to get parameters for the request from
   * */
  fun setFileACL(params: SetFileACLRequest)

  /**
   * Link file or directory
   * @param params [LinkFileRequest] instance to get parameters for the request from
   * */
  fun linkFile(params: LinkFileRequest)

  /**
   * Unlink file or directory
   * @param params [UnlinkFileRequest] instance to get parameters for the request from
   * */
  fun unlinkFile(params: UnlinkFileRequest)
}
