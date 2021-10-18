/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright © 2021 IBA Group, a.s.
 */

package eu.ibagroup.r2z.zowe.config

import com.google.gson.Gson
import org.yaml.snakeyaml.Yaml
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*

fun ZoweConnection.getAuthEncoding (): String {
  if (zosmfPort == null || host?.isEmpty() != false || password?.isEmpty() != false || user?.isEmpty() != false) {
    throw IllegalStateException("Connection data not setup properly")
  }
  return Base64.getEncoder().encodeToString("$user:$password".toByteArray(StandardCharsets.UTF_8))
}

fun ZoweConfig.getAuthEncoding (): String {
  if (host?.isEmpty() != false || port == null || username?.isEmpty() != false || password?.isEmpty() != false) {
    throw IllegalStateException("Connection data not setup properly")
  }
  return Base64.getEncoder().encodeToString("$username:$password".toByteArray(StandardCharsets.UTF_8))
}

fun String.toBasicAuthToken () = "Basic $this"

fun parseConfigYaml (inputStream: InputStream): ZoweConnection {
  val loaded: Map<String, Any> = Yaml().load(inputStream)
  return ZoweConnection(
    loaded["host"] as String?,
    loaded["port"] as Int?,
    loaded["password"] as String?,
    loaded["user"] as String?,
    loaded["rejectUnauthorized"] as Boolean? ?: false,
    loaded["protocol"] as String? ?: "http",
    loaded["basePath"] as String? ?: "/",
    loaded["encoding"] as Int? ?: 1047,
    loaded["responseTiomeout"] as Int? ?: 600
  )
}

fun parseConfigYaml (configString: String): ZoweConnection = parseConfigYaml(ByteArrayInputStream(configString.toByteArray()))

fun parseConfigJson(configString: String): ZoweConfig = Gson().fromJson(configString, ZoweConfig::class.java)

fun parseConfigJson (inputStream: InputStream): ZoweConfig = parseConfigJson(String(inputStream.readAllBytes()))