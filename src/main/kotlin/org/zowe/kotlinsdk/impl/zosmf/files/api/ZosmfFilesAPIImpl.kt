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

package org.zowe.kotlinsdk.impl.zosmf.files.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.files.api.FilesAPI
import org.zowe.kotlinsdk.core.files.api.messaging.*
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.RequestCanceller
import org.zowe.kotlinsdk.impl.zosmf.RequestRunner
import org.zowe.kotlinsdk.impl.zosmf.common.XIBMDataType
import org.zowe.kotlinsdk.impl.zosmf.files.api.messaging.*

/**
 * IBA Group's z/OSMF [FilesAPI] implementation to provide functions to work with UNIX file system
 * @param client KTor initialized [HttpClient] to make requests with
 * @param connection [Connection] instance to make requests using the connection data
 * @param requestCanceller [RequestCanceller] instance to cancel requests from the outside
 */
internal class ZosmfFilesAPIImpl(
  private val client: HttpClient,
  private val connection: Connection,
  private val requestCanceller: RequestCanceller
) : FilesAPI {

  /**
   * List the files and directories of a UNIX file path
   * @param params [ListFilesRequest] instance to get parameters for the request from
   * @return [ListFilesResponse] instance with the succeeded request result
   * */
  @AvailableSince(ZVersion.ZOS_2_1)
  override fun listFiles(params: ListFilesRequest): ListFilesResponse {
    return runBlocking {
      RequestRunner(client, connection, requestCanceller)
        .runRequest(params as ZosmfListFilesRequest)
        .body<ZosmfListFilesResponse>()
    }
  }

  /**
   * Retrieve the contents of a z/OS UNIX file
   * @param params [RetrieveFileContentRequest] instance to get parameters for the request from
   * @return [RetrieveFileContentResponse] instance with the succeeded request result
   * */
  @AvailableSince(ZVersion.ZOS_2_1)
  override fun retrieveFileContent(params: RetrieveFileContentRequest): RetrieveFileContentResponse {
    val zosmfParams = params as ZosmfRetrieveFileContentRequest
    val isBinary = zosmfParams.xIBMDataType?.type == XIBMDataType.Type.BINARY
    val (fetchedText, fetchedBytes) = runBlocking {
      val response = RequestRunner(client, connection, requestCanceller).runRequest(zosmfParams)
      if (isBinary) {
        val lengthLong = response.contentLength() ?: throw Exception("The file is empty")
        val length = lengthLong.toInt()
        if (length.toLong() != lengthLong) throw Exception("The file is too large to read")
        val byteArray = ByteArray(length)
        var offset = 0

        do {
          val currentRead = response.bodyAsChannel().readAvailable(byteArray, offset, byteArray.size)
          offset += currentRead
        } while (currentRead > 0 && offset != length)
        null to byteArray
      } else {
        response.bodyAsText() to null
      }
    }
    return ZosmfRetrieveFileContentResponse(fetchedText, fetchedBytes)
  }

  /**
   * Write data to a z/OS UNIX file
   * @param params [WriteToFileRequest] instance to get parameters for the request from
   * */
  override fun writeToUssFile(params: WriteToFileRequest) {
    return runBlocking {
      RequestRunner(client, connection, requestCanceller)
        .runRequest(params as ZosmfWriteToFileRequest)
    }
  }

  /**
   * Create a UNIX file or directory
   * @param params [CreateFileRequest] instance to get parameters for the request from
   * */
  override fun createFile(params: CreateFileRequest) {
    return runBlocking {
      RequestRunner(client, connection, requestCanceller)
        .runRequest(params as ZosmfCreateFileRequest)
    }
  }

  /**
   * Delete a UNIX file or directory
   * @param params [DeleteFileRequest] instance to get parameters for the request from
   * */
  override fun deleteFile(params: DeleteFileRequest) {
    return runBlocking {
      RequestRunner(client, connection, requestCanceller)
        .runRequest(params as ZosmfDeleteFileRequest)
    }
  }

  /**
   * chmod function for a UNIX file or directory
   * @param params [ChangeFileModeRequest] instance to get parameters for the request from
   * */
  override fun changeFileMode(params: ChangeFileModeRequest) {
    return runBlocking {
      RequestRunner(client, connection, requestCanceller)
        .runRequest(params as ZosmfChangeFileModeRequest)
    }
  }

  /**
   * chown function for a UNIX file or directory
   * @param params [ChangeFileOwnerRequest] instance to get parameters for the request from
   * */
  override fun changeFileOwner(params: ChangeFileOwnerRequest) {
    return runBlocking {
      RequestRunner(client, connection, requestCanceller)
        .runRequest(params as ZosmfChangeFileOwnerRequest)
    }
  }

  /**
   * chtag function for a UNIX file or directory
   * @param params [ChangeFileTagRequest] instance to get parameters for the request from
   * @return [ChangeFileTagResponse] instance with the succeeded request result
   * */
  override fun changeFileTag(params: ChangeFileTagRequest): ChangeFileTagResponse {
    return runBlocking {
      RequestRunner(client, connection, requestCanceller)
        .runRequest(params as ZosmfChangeFileTagRequest)
        .body<ZosmfChangeFileTagResponse>()
    }
  }

  /**
   * cp function for a UNIX file or directory
   * @param params [CopyFileRequest] instance to get parameters for the request from
   * */
  override fun copyFile(params: CopyFileRequest) {
    return runBlocking {
      RequestRunner(client, connection, requestCanceller)
        .runRequest(params as ZosmfCopyFileRequest)
    }
  }

  /**
   * cp from dataset function for a UNIX file or directory
   * @param params [CopyFileFromDatasetRequest] instance to get parameters for the request from
   * */
  override fun copyFileFromDataset(params: CopyFileFromDatasetRequest) {
    return runBlocking {
      RequestRunner(client, connection, requestCanceller)
        .runRequest(params as ZosmfCopyFileFromDatasetRequest)
    }
  }

  /**
   * extattr function for a UNIX file
   * @param params [ExtendedAttributesRequest] instance to get parameters for the request from
   * @return [ExtendedAttributesResponse] instance with the succeeded request result
   * */
  override fun extendedAttributes(params: ExtendedAttributesRequest): ExtendedAttributesResponse {
    return runBlocking {
      RequestRunner(client, connection, requestCanceller)
        .runRequest(params as ZosmfExtendedAttributesRequest)
        .body<ZosmfExtendedAttributesResponse>()
    }
  }

  /**
   * getfacl function for a UNIX file or directory
   * @param params [GetFileACLRequest] instance to get parameters for the request from
   * @return [GetFileACLResponse] instance with the succeeded request result
   * */
  override fun getFileACL(params: GetFileACLRequest): GetFileACLResponse {
    return runBlocking {
      RequestRunner(client, connection, requestCanceller)
        .runRequest(params as ZosmfGetFileACLRequest)
        .body<ZosmfGetFileACLResponse>()
    }
  }

  /**
   * move function for a UNIX file or directory
   * @param params [MoveFileRequest] instance to get parameters for the request from
   * */
  override fun moveFile(params: MoveFileRequest) {
    return runBlocking {
      RequestRunner(client, connection, requestCanceller)
        .runRequest(params as ZosmfMoveFileRequest)
    }
  }

  /**
   * setfacl function for a UNIX file or directory
   * @param params [SetFileACLRequest] instance to get parameters for the request from
   * */
  override fun setFileACL(params: SetFileACLRequest) {
    return runBlocking {
      RequestRunner(client, connection, requestCanceller)
        .runRequest(params as ZosmfSetFileACLRequest)
    }
  }

  /**
   * link function for a UNIX file or directory
   * @param params [LinkFileRequest] instance to get parameters for the request from
   * */
  override fun linkFile(params: LinkFileRequest) {
    return runBlocking {
      RequestRunner(client, connection, requestCanceller)
        .runRequest(params as ZosmfLinkFileRequest)
    }
  }

  /**
   * unlink function for a UNIX file or directory
   * @param params [UnlinkFileRequest] instance to get parameters for the request from
   * */
  override fun unlinkFile(params: UnlinkFileRequest) {
    return runBlocking {
      RequestRunner(client, connection, requestCanceller)
        .runRequest(params as ZosmfUnlinkFileRequest)
    }
  }
}
