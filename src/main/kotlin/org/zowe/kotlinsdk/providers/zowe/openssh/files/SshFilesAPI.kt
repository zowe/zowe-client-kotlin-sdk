/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.files

import org.zowe.kotlinsdk.core.files.api.FilesAPI
import org.zowe.kotlinsdk.core.files.api.messaging.*
import org.zowe.kotlinsdk.providers.zowe.SshRequestRunner
import org.zowe.kotlinsdk.providers.zowe.ZoweInternalAPI

@ZoweInternalAPI
class SshFilesAPI(private val requestRunner: SshRequestRunner) : FilesAPI {
  override suspend fun listFiles(params: ListFilesRequest): ListFilesResponse {
    TODO("Not yet implemented")
  }

  override suspend fun retrieveFileContent(params: RetrieveFileContentRequest): RetrieveFileContentResponse {
    TODO("Not yet implemented")
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
