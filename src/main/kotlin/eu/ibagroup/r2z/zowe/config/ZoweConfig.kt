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

import com.google.gson.annotations.SerializedName

class ZoweConfig(
  @SerializedName("\$schema")
  private val schema: String,
  val profiles: Map<String, ZoweConfigProfile>,
  val defaults: Map<String, String>
) {
  var user: String?
    get() = profiles["base"]?.secure?.get(0)
    set(el) { profiles["base"]?.secure?.set(0, el ?: "") }

  var password: String?
    get() = profiles["base"]?.secure?.get(1)
    set(el) { profiles["base"]?.secure?.set(1, el ?: "") }

  @Suppress("UNCHECKED_CAST")
  var host: String?
    get() = profiles["base"]?.properties?.get("host") as String?
    set(el) { profiles["base"]?.properties?.set("host", el) }

  @Suppress("UNCHECKED_CAST")
  var rejectUnauthorized: Boolean?
    get() = profiles["base"]?.properties?.get("rejectUnauthorized") as Boolean?
    set(el) { profiles["base"]?.properties?.set("rejectUnauthorized", el ?: true) }

  @Suppress("UNCHECKED_CAST")
  var port: Long?
    get() = profiles["zosmf"]?.properties?.get("port") as Long?
    set(el) { profiles["base"]?.properties?.set("port", el?.toDouble()) }

  @Suppress("UNCHECKED_CAST")
  var protocol: String
    get() = profiles["zosmf"]?.properties?.get("protocol") as String? ?: "http"
    set(el) { profiles["base"]?.properties?.set("protocol", el) }

  @Suppress("UNCHECKED_CAST")
  var basePath: String
    get() = profiles["zosmf"]?.properties?.get("basePath") as String? ?: "/"
    set(el) { profiles["base"]?.properties?.set("basePath", el) }

  @Suppress("UNCHECKED_CAST")
  var encoding: Long
    get() = profiles["zosmf"]?.properties?.get("encoding") as Long? ?: 1047
    set(el) { profiles["base"]?.properties?.set("encoding", el.toDouble()) }

  @Suppress("UNCHECKED_CAST")
  var responseTimeout: Long
    get() = profiles["zosmf"]?.properties?.get("responseTimeout") as Long? ?: 600
    set(el) { profiles["base"]?.properties?.set("responseTimeout", el.toDouble()) }
}

class ZoweConfigProfile(
  val type: String,
  val properties: MutableMap<String, Any?>,
  val secure: ArrayList<String>
)
