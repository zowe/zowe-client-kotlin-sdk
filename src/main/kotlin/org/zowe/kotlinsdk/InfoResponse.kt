/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBA Group 2020
 */

package org.zowe.kotlinsdk

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.zowe.kotlinsdk.annotations.ZVersion

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
  @JsonAdapter(PluginsAdapter::class)
  @Expose
  val plugins: List<Plugin> = emptyList(),

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
  val zVersion: ZVersion = when(zosVersion) {
    "04.25.00" -> ZVersion.ZOS_2_2
    "04.26.00" -> ZVersion.ZOS_2_3
    "04.27.00" -> ZVersion.ZOS_2_4
    "04.28.00" -> ZVersion.ZOS_2_5
    else -> ZVersion.ZOS_2_1
  }
}

class PluginsAdapter : TypeAdapter<List<Plugin>>() {
  private val gson = Gson()
  override fun write(out: JsonWriter?, value: List<Plugin>?) {
    gson.toJson(value, List::class.java, out)
  }

  override fun read(reader: JsonReader): List<Plugin> {
    var result = listOf<Plugin>()
    runCatching {
      result = gson.fromJson(reader, List::class.java)
    }.onFailure {
      reader.skipValue()
    }
    return result
  }

}
