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

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.files.api.FilesAPI
import org.zowe.kotlinsdk.core.files.api.messaging.*
import org.zowe.kotlinsdk.providers.zowe.SshRequestRunner
import org.zowe.kotlinsdk.providers.zowe.ZoweInternalAPI
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshChangeFileModeResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshChangeFileOwnerResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshChangeFileTagResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshCopyFileResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshCreateFileResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshFileExtAttributesUtilityResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshDeleteFileResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshGetFileACLResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshLinkFileResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshListFilesResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshMoveFileResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshRetrieveFileContentResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshSetFileACLResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshUnlinkFileResponse
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshWriteToFileResponse

/**
 * Implementation of Files API for SSH to work with USS files and directories
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=guide-zos-unix-system-services-command-reference">z/OS UNIX System Services command reference</a>
 */
@ZoweInternalAPI
class SshFilesAPI(private val requestRunner: SshRequestRunner) : FilesAPI {

  /**
   * List the USS files and directories by the provided path, running the "ls -l" command
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-ls-list-file-directory-names-attributes">ls — List file and directory names and attributes</a>
   * @param params [ListFilesRequest] instance to get parameters for the request from
   * @return [SshListFilesResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun listFiles(params: ListFilesRequest): SshListFilesResponse {
    return requestRunner.runRequest(params) as SshListFilesResponse
  }

  /**
   * Retrieve the USS file content, copying it to the standard output of the "cp" command
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-cp-copy-files">cp — Copy files</a>
   * @param params [RetrieveFileContentRequest] instance to get parameters for the request from
   * @return [SshRetrieveFileContentResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun retrieveFileContent(params: RetrieveFileContentRequest): SshRetrieveFileContentResponse {
    return requestRunner.runRequest(params) as SshRetrieveFileContentResponse
  }

  /**
   * Write the content to the USS file, passing it to the standard input of the "cp" command
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-cp-copy-files">cp — Copy files</a>
   * @param params [WriteToFileRequest] instance to get parameters for the request from
   * @return [SshWriteToFileResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun writeToFile(params: WriteToFileRequest): SshWriteToFileResponse {
    return requestRunner.runRequest(params) as SshWriteToFileResponse
  }

  /**
   * Create the USS file or directory, running the "touch" or the "mkdir" command respectively
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-mkdir-make-directory">mkdir — Make a directory</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-touch-change-file-access-modification-times">touch — Change the file access and modification times</a>
   * @param params [CreateFileRequest] instance to get parameters for the request from
   * @return [SshCreateFileResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun createFile(params: CreateFileRequest): SshCreateFileResponse {
    return requestRunner.runRequest(params) as SshCreateFileResponse
  }

  /**
   * Delete the USS file or directory, running the "rm" or the "rmdir" command respectively
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-rm-remove-directory-entries">rm — Remove a directory entry</a>
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-rmdir-remove-directory">rmdir — Remove a directory</a>
   * @param params [DeleteFileRequest] instance to get parameters for the request from
   * @return [SshDeleteFileResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun deleteFile(params: DeleteFileRequest): SshDeleteFileResponse {
    return requestRunner.runRequest(params) as SshDeleteFileResponse
  }

  /**
   * Copy the USS file or directory, or the data set or the data set member, running the "cp" command
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-cp-copy-file">cp - Copy a file</a>
   * @param params [CopyFileRequest] instance to get parameters for the request from
   * @return [SshCopyFileResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun copyFile(params: CopyFileRequest): SshCopyFileResponse {
    return requestRunner.runRequest(params) as SshCopyFileResponse
  }

  /**
   * Move the USS file or directory, running the "mv" command
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-mv-rename-move-file-directory">mv — Rename or move a file or directory</a>
   * @param params [MoveFileRequest] instance to get parameters for the request from
   * @return [SshMoveFileResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun moveFile(params: MoveFileRequest): SshMoveFileResponse {
    return requestRunner.runRequest(params) as SshMoveFileResponse
  }

  /**
   * Change the mode of the USS file or directory, running the "chmod" command
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-chmod-change-mode-file-directory">chmod — Change the mode of a file or directory</a>
   * @param params [ChangeFileModeRequest] instance to get parameters for the request from
   * @return [SshChangeFileModeResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun changeFileMode(params: ChangeFileModeRequest): SshChangeFileModeResponse {
    return requestRunner.runRequest(params) as SshChangeFileModeResponse
  }

  /**
   * Change the owner of the USS file or directory, running the "chown" command
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-chown-change-owner-group-file-directory">chown — Change the owner or group of a file or directory</a>
   * @param params [ChangeFileOwnerRequest] instance to get parameters for the request from
   * @return [SshChangeFileOwnerResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun changeFileOwner(params: ChangeFileOwnerRequest): SshChangeFileOwnerResponse {
    return requestRunner.runRequest(params) as SshChangeFileOwnerResponse
  }

  /**
   * Change or display the tag of the USS file or directory, running the "chtag" command
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-chtag-change-file-tag-information">chtag — Change file tag information</a>
   * @param params [ChangeFileTagRequest] instance to get parameters for the request from
   * @return [SshChangeFileTagResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun changeFileTag(params: ChangeFileTagRequest): SshChangeFileTagResponse {
    return requestRunner.runRequest(params) as SshChangeFileTagResponse
  }

  /**
   * Modify or display the extended attributes of the USS file, running the "extattr" command
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-extattr-set-reset-display-extended-attributes-files">extattr — Set, reset, and display extended attributes for files</a>
   * @param params [FileExtAttributesUtilityRequest] instance to get parameters for the request from
   * @return [SshFileExtAttributesUtilityResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun fileExtAttributesUtility(
    params: FileExtAttributesUtilityRequest
  ): SshFileExtAttributesUtilityResponse {
    return requestRunner.runRequest(params) as SshFileExtAttributesUtilityResponse
  }

  /**
   * Retrieve the access control list of the USS file or directory, running the "getfacl" command
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-getfacl-display-owner-group-class-acl-entries">getfacl — Display owner, group, class, and ACL entries</a>
   * @param params [GetFileACLRequest] instance to get parameters for the request from
   * @return [SshGetFileACLResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun getFileACL(params: GetFileACLRequest): SshGetFileACLResponse {
    return requestRunner.runRequest(params) as SshGetFileACLResponse
  }

  /**
   * Set, modify or remove the access control list of the USS file or directory, running the "setfacl" command
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-setfacl-set-remove-change-acl-entries">setfacl — Set, remove, and change ACL entries</a>
   * @param params [SetFileACLRequest] instance to get parameters for the request from
   * @return [SshSetFileACLResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun setFileACL(params: SetFileACLRequest): SshSetFileACLResponse {
    return requestRunner.runRequest(params) as SshSetFileACLResponse
  }

  /**
   * Create a link to the USS file or directory, running the "ln" command
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-ln-create-link-file">ln — Create a link to a file</a>
   * @param params [LinkFileRequest] instance to get parameters for the request from
   * @return [SshLinkFileResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun linkFile(params: LinkFileRequest): SshLinkFileResponse {
    return requestRunner.runRequest(params) as SshLinkFileResponse
  }

  /**
   * Remove the link to the USS file or directory, running the "unlink" command
   * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-unlink-remove-directory-entry">unlink — Remove a directory entry</a>
   * @param params [UnlinkFileRequest] instance to get parameters for the request from
   * @return [SshUnlinkFileResponse] instance with the request handling result
   */
  @AvailableSince(ZVersion.ZOS_2_2)
  override suspend fun unlinkFile(params: UnlinkFileRequest): SshUnlinkFileResponse {
    return requestRunner.runRequest(params) as SshUnlinkFileResponse
  }
}
