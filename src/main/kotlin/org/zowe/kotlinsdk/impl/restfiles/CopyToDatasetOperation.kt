//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.restfiles

import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.ALL_MEMBERS
import org.zowe.kotlinsdk.core.restfiles.RestfilesAPI
import org.zowe.kotlinsdk.core.restfiles.ZOSCopyBody
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.Operation
import retrofit2.Call

// TODO: doc
class CopyToDatasetOperation(
  private val copyParams: ZOSCopyParams,
  private val connection: Connection,
  httpClient: OkHttpClient
) : Operation<RestfilesAPI, Void>(connection, httpClient, RestfilesAPI::class.java) {
  override fun buildCall(runnerAPI: RestfilesAPI): Call<Void> {
    return if (copyParams.toVolser != null) {
      runnerAPI.copyToDataset(
        authorizationToken = connection.basicCredentials,
        body = ZOSCopyBody.FromDataset(
          dataset = ZOSCopyBody.FromDataset.Dataset(
            datasetName = copyParams.fromDataSet,
            memberName = if (copyParams.copyAllMembers) ALL_MEMBERS else null,
            volumeSerial = copyParams.fromVolser
          ),
          replace = copyParams.replace
        ),
        toVolser = copyParams.toVolser,
        toDatasetName = copyParams.toDataSet
      )
    } else {
      runnerAPI.copyToDataset(
        authorizationToken = connection.basicCredentials,
        body = ZOSCopyBody.FromDataset(
          dataset = ZOSCopyBody.FromDataset.Dataset(
            datasetName = copyParams.fromDataSet,
            memberName = if (copyParams.copyAllMembers) ALL_MEMBERS else null
          ),
          replace = copyParams.replace
        ),
        toDatasetName = copyParams.toDataSet
      )
    }
  }
}