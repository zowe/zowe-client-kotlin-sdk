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
 */

package org.zowe.kotlinsdk.zowe.examples

import org.zowe.kotlinsdk.zowe.config.getAuthEncoding
import org.zowe.kotlinsdk.zowe.config.parseConfigYaml
import org.zowe.kotlinsdk.zowe.config.withBasicPrefix

/**
 * Shows how to read a "config.yaml" file and how to build the authorization header out of it.
 *
 * Note that neither the password nor the authorization header value is printed: both of them are the z/OS
 * credentials, the header value being nothing more than a Base64 encoding of them. Do not print or log them,
 * neither here nor in the code that follows this example. The password is redacted by the connection itself,
 * see [org.zowe.kotlinsdk.zowe.config.ZoweConnection.toString].
 */
fun main() {
  val inputStream = object {}.javaClass.classLoader.getResourceAsStream("config.yaml")
  if (inputStream != null) {
    val zoweConnection = parseConfigYaml(inputStream)
    println(zoweConnection)
    val authorizationHeader = zoweConnection.getAuthEncoding().withBasicPrefix()
    println("Authorization header is built: Basic *** (${authorizationHeader.length} characters)")
  }
}
