/*
 * Copyright (c) 2020-2024 IBA Group.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   IBA Group
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk

import okhttp3.OkHttpClient

/**
 * Provides an [OkHttpClient] that accepts any TLS certificate chain and any host name.
 *
 * Using it makes the connection vulnerable to machine-in-the-middle attacks: the credentials sent with every
 * request can be harvested and the transferred data (JCL, datasets, console and TSO commands) can be tampered with.
 * Use [ZosmfOkHttpClient.getOkHttpClient] instead: it validates certificates by default and only falls back to
 * this client when [org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection.rejectUnauthorized] is explicitly `false`.
 */
@Deprecated(
  "Disables TLS certificate and host name validation. Use ZosmfOkHttpClient.getOkHttpClient(connection) instead.",
  ReplaceWith("ZosmfOkHttpClient.secureOkHttpClient", "org.zowe.kotlinsdk.ZosmfOkHttpClient")
)
object UnsafeOkHttpClient {
  val unsafeOkHttpClient: OkHttpClient
    get() = ZosmfOkHttpClient.insecureOkHttpClient
}
