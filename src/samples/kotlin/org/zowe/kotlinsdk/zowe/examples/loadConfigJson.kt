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
import org.zowe.kotlinsdk.zowe.config.parseConfigJson
import org.zowe.kotlinsdk.zowe.config.withBasicPrefix

/**
 * Shows how to read a "zowe.config.json" file and how to build the authorization header out of it.
 *
 * Note that neither the password nor the authorization header value is printed: both of them are the z/OS
 * credentials, the header value being nothing more than a Base64 encoding of them. Do not print or log them,
 * neither here nor in the code that follows this example.
 */
fun main() {
  val inputStream = object {}.javaClass.classLoader.getResourceAsStream("zowe.config.json")
  if (inputStream != null) {
    val zoweConfig = parseConfigJson(inputStream)
    println("url=\"${zoweConfig.protocol}://${zoweConfig.host}:${zoweConfig.port}\"; username=${zoweConfig.user}; password=***")
    if (zoweConfig.user.isNullOrEmpty() || zoweConfig.password.isNullOrEmpty()) {
      // The bundled example configuration lists "user" and "password" as secure properties, which means that
      // their values are kept in the credential storage of the operating system rather than in the file itself
      println("No credentials in the configuration: read the secure properties first, see ZoweConfig.extractSecureProperties")
      return
    }
    val authorizationHeader = zoweConfig.getAuthEncoding().withBasicPrefix()
    println("Authorization header is built: Basic *** (${authorizationHeader.length} characters)")
  }
}
