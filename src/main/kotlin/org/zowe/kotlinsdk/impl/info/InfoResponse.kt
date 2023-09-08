//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.info

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

data class InfoResponse (
  @SerializedName("zos_version")
  @Expose
  val zosVersion: String = "null",

  @SerializedName("zosmf_port")
  @Expose
  val zosmfPort: String = "null",

  @SerializedName("zosmf_version")
  @Expose
  val zosmfVersion: String = "null",

  @SerializedName("zosmf_hostname")
  @Expose
  val zosmfHostname: String = "null",

  @SerializedName("plugins")
  @JsonAdapter(ZosmfPluginsAdapter::class)
  @Expose
  val plugins: List<ZosmfPlugin> = emptyList(),

  @SerializedName("zosmf_saf_realm")
  @Expose
  val zosmfSafRealm: String = "null",

  @SerializedName("zosmf_full_version")
  @Expose
  val zosmfFullVersion: String = "null",

  @SerializedName("api_version")
  @Expose
  val apiVersion: String = "null"
) {
  // TODO: doc
  data class ZosmfPlugin(
    @SerializedName("pluginVersion")
    @Expose
    val version: String? = null,

    @SerializedName("pluginDefaultName")
    @Expose
    val defaultName: String? = null,

    @SerializedName("pluginStatus")
    @Expose
    val status: String? = null
  )

  // TODO: doc
  class ZosmfPluginsAdapter : TypeAdapter<List<ZosmfPlugin>>() {
    private val gson = Gson()
    override fun write(out: JsonWriter?, value: List<ZosmfPlugin>?) {
      gson.toJson(value, List::class.java, out)
    }

    override fun read(reader: JsonReader): List<ZosmfPlugin> {
      var result = listOf<ZosmfPlugin>()
      runCatching {
        result = gson.fromJson(reader, List::class.java)
      }.onFailure {
        reader.skipValue()
      }
      return result
    }

  }
}
