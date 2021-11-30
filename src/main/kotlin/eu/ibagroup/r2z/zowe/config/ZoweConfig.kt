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
    get() = baseProfile?.secure?.get(0)
    set(el) { baseProfile?.secure?.set(0, el ?: "") }

  var password: String?
    get() = baseProfile?.secure?.get(1)
    set(el) { baseProfile?.secure?.set(1, el ?: "") }

  var host: String?
    get() = baseProfile?.properties?.get("host") as String?
    set(el) { baseProfile?.properties?.set("host", el) }

  var rejectUnauthorized: Boolean?
    get() = baseProfile?.properties?.get("rejectUnauthorized") as Boolean?
    set(el) { baseProfile?.properties?.set("rejectUnauthorized", el ?: true) }

  var port: Long?
    get() = zosmfProfile?.properties?.get("port") as Long?
    set(el) { zosmfProfile?.properties?.set("port", el) }

  var protocol: String
    get() = zosmfProfile?.properties?.get("protocol") as String? ?: "http"
    set(el) { zosmfProfile?.properties?.set("protocol", el) }

  var basePath: String
    get() = zosmfProfile?.properties?.get("basePath") as String? ?: "/"
    set(el) { zosmfProfile?.properties?.set("basePath", el) }

  var encoding: Long
    get() = zosmfProfile?.properties?.get("encoding") as Long? ?: 1047
    set(el) { zosmfProfile?.properties?.set("encoding", el) }

  var responseTimeout: Long
    get() = zosmfProfile?.properties?.get("responseTimeout") as Long? ?: 600
    set(el) { zosmfProfile?.properties?.set("responseTimeout", el) }

  val zosmfProfile: ZoweConfigProfile?
    get() = profiles[defaults["zosmf"]]

  val tsoProfile: ZoweConfigProfile?
    get() = profiles[defaults["tso"]]

  val sshProfile: ZoweConfigProfile?
    get() = profiles[defaults["ssh"]]

  val baseProfile: ZoweConfigProfile?
    get() = profiles[defaults["base"]]
}

class ZoweConfigProfile(
  val type: String,
  val properties: MutableMap<String, Any?>,
  val secure: ArrayList<String>
)
