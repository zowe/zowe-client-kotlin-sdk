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

package org.zowe.kotlinsdk.zowe.examples

import org.zowe.kotlinsdk.zowe.config.*

/**
 * Shows how to read a "zowe.config.json" file and serialize it back to JSON.
 *
 * The example is run against the credential free "zowe.config.json" of this source set. Mind that the produced
 * JSON of a real configuration contains the credentials of the profiles as they are stored in the file, so it
 * belongs to a file with the appropriate permissions and never to a console or a log.
 */
fun main() {
  val inputStream = object {}.javaClass.classLoader.getResourceAsStream("zowe.config.json")
  if (inputStream != null) {
    val zoweConfig = parseConfigJson(inputStream)
    println(zoweConfig.toJson())
  }
}
