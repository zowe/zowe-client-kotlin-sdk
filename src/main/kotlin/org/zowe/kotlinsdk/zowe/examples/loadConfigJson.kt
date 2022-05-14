/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBA Group 2020
 */

package org.zowe.kotlinsdk.zowe.examples

import org.zowe.kotlinsdk.zowe.config.getAuthEncoding
import org.zowe.kotlinsdk.zowe.config.parseConfigJson
import org.zowe.kotlinsdk.zowe.config.withBasicPrefix

fun main() {
  val inputStream = object {}.javaClass.classLoader.getResourceAsStream("zowe.config.json")
  if (inputStream != null) {
    val zoweConfig = parseConfigJson(inputStream)
    println("url=\"${zoweConfig.protocol}://${zoweConfig.host}:${zoweConfig.port}\"; username=${zoweConfig.user}; password=${zoweConfig.password}")
    println(zoweConfig.getAuthEncoding().withBasicPrefix())
  }
}
