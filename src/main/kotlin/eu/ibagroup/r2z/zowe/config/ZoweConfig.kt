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
import eu.ibagroup.r2z.CodePage

class ZoweConfig(
  @SerializedName("\$schema")
  private val schema: String,
  val profiles: Map<String, ZoweConfigProfile>,
  val defaults: Map<String, String>
) {

  inner class PropertyBuilder(val propName: String) {
    var profilesToSearchProp = mutableListOf<ZoweConfigProfile?>()
    fun zosmf() {
      profilesToSearchProp.add(zosmfProfile)
    }
    fun tso() {
      profilesToSearchProp.add(tsoProfile)
    }
    fun ssh () {
      profilesToSearchProp.add(sshProfile)
    }
    fun base () {
      profilesToSearchProp.add(baseProfile)
    }
  }

  private fun PropertyBuilder.search (): Any? {
    val profiles = profilesToSearchProp.filterNotNull()
    for (profile in profiles) {
      return profile.properties[propName] ?: continue
    }
    return null
  }

  fun PropertyBuilder.set (value: Any?) {
    val profiles = profilesToSearchProp.filterNotNull()
    for (profile in profiles) {
      if (!profile.properties.containsKey(propName)) {
        continue
      }
      profile.properties[propName] = value
    }
  }

  fun searchProperty (propName: String, block: PropertyBuilder.() -> Unit): Any? {
    return PropertyBuilder(propName).apply(block).search()
  }

  fun updateProperty (propName: String, propValue: Any?, block: PropertyBuilder.() -> Unit) {
    PropertyBuilder(propName).apply(block).set(propValue)
  }

  var user: String?
    get() = baseProfile?.secure?.get(0)
    set(el) { baseProfile?.secure?.set(0, el ?: "") }

  var password: String?
    get() = baseProfile?.secure?.get(1)
    set(el) { baseProfile?.secure?.set(1, el ?: "") }

  var host: String?
    get() = searchProperty("host") { zosmf(); base() } as String?
    set(el) { updateProperty("host", el) { zosmf(); base() } }

  var rejectUnauthorized: Boolean?
    get() = searchProperty("rejectUnauthorized") { zosmf(); base() } as Boolean?
    set(el) { updateProperty("rejectUnauthorized", el ?: true) { zosmf(); base() } }

  var port: Long?
    get() = searchProperty("port") { zosmf(); base() } as Long?
    set(el) { updateProperty("port", el) { zosmf(); base() } }

  var protocol: String
    get() = searchProperty("protocol") { zosmf(); base() } as String? ?: "http"
    set(el) { updateProperty("protocol", el) { zosmf(); base() } }

  var basePath: String
    get() = searchProperty("basePath") { zosmf(); base() } as String? ?: "/"
    set(el) { updateProperty("protocol", el) { zosmf(); base() } }

  var encoding: Long
    get() = searchProperty("encoding") { zosmf(); base() } as Long? ?: 1047
    set(el) { updateProperty("encoding", el) { zosmf(); base() } }

  var responseTimeout: Long
    get() = searchProperty("responseTimeout") { zosmf(); base() } as Long? ?: 600
    set(el) { updateProperty("responseTimeout", el) { zosmf(); base() } }

  var codePage: CodePage
    get() = CodePage.valueOf("IBM_${searchProperty("codePage") { tso(); base() } }")
    set(el) { updateProperty("codePage", el.codePage.split("IBM_").last()) { tso(); base() } }

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
