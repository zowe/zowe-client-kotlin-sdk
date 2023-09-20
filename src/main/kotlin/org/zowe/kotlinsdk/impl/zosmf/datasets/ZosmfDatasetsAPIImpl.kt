//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.datasets

import okhttp3.OkHttpClient
import org.zowe.kotlinsdk.core.datasets.DatasetsAPI
import org.zowe.kotlinsdk.core.datasets.data.ListDatasetMembersRequest
import org.zowe.kotlinsdk.core.datasets.data.ListDatasetMembersResponse
import org.zowe.kotlinsdk.core.datasets.data.ListDatasetsRequest
import org.zowe.kotlinsdk.core.datasets.data.ListDatasetsResponse
import org.zowe.kotlinsdk.impl.zosmf.Connection
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.ZosmfListDatasetMembersRequest
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.ZosmfListDatasetsRequest
import org.zowe.kotlinsdk.impl.zosmf.datasets.data.ZosmfListDatasetsResponse
import org.zowe.kotlinsdk.impl.zosmf.datasets.operations.ListDatasetMembersOperation
import org.zowe.kotlinsdk.impl.zosmf.datasets.operations.ListDatasetsOperation

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/2.5.0?topic=services-zos-data-set-file-rest-interface
 */
class ZosmfDatasetsAPIImpl(val connection: Connection, val httpClient: OkHttpClient) : DatasetsAPI {
  // TODO: doc
  override fun listDatasets(params: ListDatasetsRequest): ListDatasetsResponse {
    return ListDatasetsOperation(params as ZosmfListDatasetsRequest, connection, httpClient).runOperation()
  }

  override fun listDatasetMembers(params: ListDatasetMembersRequest): ListDatasetMembersResponse {
    return ListDatasetMembersOperation(params as ZosmfListDatasetMembersRequest, connection, httpClient).runOperation()
  }
}
