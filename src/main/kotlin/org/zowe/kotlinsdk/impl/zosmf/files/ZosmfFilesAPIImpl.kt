//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.files

import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.core.files.FilesAPI
import org.zowe.kotlinsdk.core.files.data.ListFilesRequest
import org.zowe.kotlinsdk.core.files.data.ListFilesResponse
import org.zowe.kotlinsdk.impl.zosmf.CallCustomizer
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.files.data.ZosmfListFilesRequest
import org.zowe.kotlinsdk.impl.zosmf.files.operations.ListFilesOperation

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/3.1.0?topic=services-zos-data-set-file-rest-interface
 */
class ZosmfFilesAPIImpl(
  val connection: Connection,
  val httpClient: OkHttpClient,
  private val callCustomizer: CallCustomizer? = null
) : FilesAPI {
  // TODO: doc
  override fun listFiles(params: ListFilesRequest): ListFilesResponse {
    return ListFilesOperation(params as ZosmfListFilesRequest, connection, httpClient).runOperation(callCustomizer)
  }
}