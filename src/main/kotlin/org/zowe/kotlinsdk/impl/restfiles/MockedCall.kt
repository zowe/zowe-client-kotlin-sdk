//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.restfiles

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: doc
class MockedCall<T>(private val asResult: T) : Call<T> {
  override fun clone(): Call<T> {
    throw NotImplementedError("'clone' should not be implemented by MockedCall class")
  }

  override fun execute(): Response<T> {
    return Response.success(asResult)
  }

  override fun isExecuted(): Boolean {
    throw NotImplementedError("'isExecuted' should not be implemented by MockedCall class")
  }

  override fun cancel() {
    throw NotImplementedError("'cancel' should not be implemented by MockedCall class")
  }

  override fun isCanceled(): Boolean {
    throw NotImplementedError("'isCanceled' should not be implemented by MockedCall class")
  }

  override fun request(): Request {
    throw NotImplementedError("'request' should not be implemented by MockedCall class")
  }

  override fun timeout(): Timeout {
    throw NotImplementedError("'timeout' should not be implemented by MockedCall class")
  }

  override fun enqueue(callback: Callback<T>) {
    throw NotImplementedError("'enqueue' should not be implemented by MockedCall class")
  }
}