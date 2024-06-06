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

/** Datasets API specification to provide functions to work with datasets  */
interface DatasetsAPI : API {

  /**
   * List datasets by the provided parameters
   * @param params [ListDatasetsRequest] instance to get parameters for the request from
   * @return [ListDatasetsResponse] instance with the succeeded request result
   */
  fun listDatasets(params: ListDatasetsRequest): ListDatasetsResponse

  /**
   * Get the dataset's info
   * @param params [GetDatasetInfoRequest] instance to get parameters for the request from
   * @return [GetDatasetInfoResponse] instance with the succeeded request result
   */
  fun getDatasetInfo(params: GetDatasetInfoRequest): GetDatasetInfoResponse

  /**
   * List the dataset's members
   * @param params [ListDatasetMembersRequest] instance to get parameters for the request from
   * @return [ListDatasetMembersResponse] instance with the succeeded request result
   */
  fun listDatasetMembers(params: ListDatasetMembersRequest): ListDatasetMembersResponse

  /**
   * Retrieve the dataset's content
   * @param params [RetrieveDatasetContentRequest] instance to get parameters for the request from
   * @return [RetrieveDatasetContentResponse] instance with the succeeded request result
   */
  fun retrieveDatasetContent(params: RetrieveDatasetContentRequest): RetrieveDatasetContentResponse
}
