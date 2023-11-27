//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf

import io.ktor.client.*
import org.zowe.kotlinsdk.core.datasets.DatasetsAPI
import org.zowe.kotlinsdk.impl.zosmf.datasets.ZosmfDatasetsAPI

/**
 * The default z/OSMF API provider. Provides all the necessary functions to interact with mainframe through z/OSMF REST API
 * @param client [HttpClient] to run requests with
 * @param connection [Connection] instance to run requests with info from
 * @param requestCanceller [RequestCanceller] instance to cancel active requests from the outside
 */
class ZosmfDefaultAPIProvider(
  client: HttpClient,
  connection: Connection,
  requestCanceller: RequestCanceller
) : APIProvider(
  DatasetsAPI::class.java to ZosmfDatasetsAPI(client, connection, requestCanceller)
)
