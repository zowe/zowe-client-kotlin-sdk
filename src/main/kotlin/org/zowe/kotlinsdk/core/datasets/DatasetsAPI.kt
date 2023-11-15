//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.datasets

import org.zowe.kotlinsdk.core.API
import org.zowe.kotlinsdk.core.datasets.data.*

// TODO: doc
interface DatasetsAPI : API {
  // TODO: doc
  fun listDatasets(params: ListDatasetsRequest): ListDatasetsResponse

  // TODO: doc
  fun getDatasetInfo(params: GetDatasetInfoRequest): GetDatasetInfoResponse

  // TODO: doc
  fun listDatasetMembers(params: ListDatasetMembersRequest): ListDatasetMembersResponse

  // TODO: doc
  fun retrieveDatasetContent(params: RetrieveDatasetContentRequest): RetrieveDatasetContentResponse
}
