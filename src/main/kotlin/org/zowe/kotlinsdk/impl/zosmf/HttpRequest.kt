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

import io.ktor.http.*

/**
 * Interface to carry all the necessary info to send a z/OSMF HTTP request
 * @property method an HTTP method to make the HTTP request
 * @property path the actual URL path to make request to (not the full URL path, only the part without the domain)
 * @property headers HTTP headers to provide to the request
 * @property parameters HTTP query parameters to provide to the request
 */
interface HttpRequest {
  val method: HttpMethod
  val path: String
  val headers: Map<String, String?>
  val parameters: Map<String, String?>
}