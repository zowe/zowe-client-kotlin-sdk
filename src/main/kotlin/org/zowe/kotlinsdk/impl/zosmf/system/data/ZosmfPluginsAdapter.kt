//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.system.data

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

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