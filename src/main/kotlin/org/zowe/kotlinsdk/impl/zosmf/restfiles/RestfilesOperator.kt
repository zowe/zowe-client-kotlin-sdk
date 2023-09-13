//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.restfiles

import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.core.zosmf.restfiles.Restfiles
import org.zowe.kotlinsdk.core.zosmf.restfiles.data.DataSetListDocument
import org.zowe.kotlinsdk.core.zosmf.restfiles.data.ListDatasetsRequest
import org.zowe.kotlinsdk.impl.zosmf.restfiles.operations.ListDatasetsOperation

// TODO: doc
class RestfilesOperator(
  private val connection: Connection,
  private val httpClient: OkHttpClient
) : Restfiles {
  // TODO: doc
  override fun listDatasets(params: ListDatasetsRequest): DataSetListDocument {
    return ListDatasetsOperation(params, connection, httpClient).runOperation().converted
  }
}