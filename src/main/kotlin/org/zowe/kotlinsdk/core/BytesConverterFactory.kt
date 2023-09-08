/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBA Group 2020
 */

package org.zowe.kotlinsdk.core

import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

// TODO: doc
class BytesConverterFactory : Converter.Factory() {

  // TODO: doc
  companion object Factory {
    fun create(): BytesConverterFactory = BytesConverterFactory()
  }

  // TODO: doc
  override fun responseBodyConverter(
    type: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): Converter<ResponseBody, *>? {
    if (getRawType(type) !== ByteArray::class.java) return null

    return Converter { it.byteStream().readBytes() }
  }

  // TODO: doc
  override fun requestBodyConverter(
    type: Type,
    parameterAnnotations: Array<out Annotation>,
    methodAnnotations: Array<out Annotation>,
    retrofit: Retrofit
  ): Converter<*, RequestBody>? {
    if (getRawType(type) !== ByteArray::class.java) return null

    return Converter<ByteArray, RequestBody> { RequestBody.create(MediaType.get("application/octet-stream"), it) }
  }
}
