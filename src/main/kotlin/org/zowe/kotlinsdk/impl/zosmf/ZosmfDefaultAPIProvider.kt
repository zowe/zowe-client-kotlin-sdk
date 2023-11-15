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


// TODO: doc
class ZosmfDefaultAPIProvider(
  client: HttpClient,
  connection: Connection
) : APIProvider(
  DatasetsAPI::class.java to ZosmfDatasetsAPI(client, connection)
)
