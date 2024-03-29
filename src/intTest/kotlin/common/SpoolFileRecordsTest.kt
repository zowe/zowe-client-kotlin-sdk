/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBA Group 2020
 */

package common

import org.zowe.kotlinsdk.BinaryMode
import org.zowe.kotlinsdk.JESApi
import org.zowe.kotlinsdk.RecordRange
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import retrofit2.Call

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpoolFileRecordsTest : BaseTest() {
  val jesApi = buildApi<JESApi>(BASE_URL, getUnsafeOkHttpClient())

  val JOB_CORRELATOR = "J0005569S0W1....D9975741.......:"
  val JOB_ID = "JOB05569"
  val JOB_NAME = "NOTHINGJ"

  @Test
  fun getRecordsInBinaryModeTest() {

    val call = jesApi.getSpoolFileRecords(BASIC_AUTH_TOKEN, JOB_CORRELATOR,2, BinaryMode.BINARY)
    executeCallAndCheckResult(call)
  }

  @Test
  fun getRecordsInTextModeTest() {
    val call = jesApi.getSpoolFileRecords(BASIC_AUTH_TOKEN, JOB_CORRELATOR, 2, BinaryMode.TEXT)
    executeCallAndCheckResult(call)
  }

  @Test
  fun getRecordsInRecordModeTest() {
    val call = jesApi.getSpoolFileRecords(BASIC_AUTH_TOKEN, JOB_NAME, JOB_ID, 2, BinaryMode.RECORD)
    executeCallAndCheckResult(call)
  }

  @Test
  fun getJCLRecordsInRangeTest() {
    val call = jesApi.getJCLRecords(BASIC_AUTH_TOKEN, JOB_CORRELATOR, BinaryMode.TEXT, RecordRange.withOffset(0, 5))
    executeCallAndCheckResult(call)
  }


  fun executeCallAndCheckResult(call: Call<ByteArray>) {
    val response = call.execute()
    if (response.isSuccessful) {
      val arr = response.body() as ByteArray
      println(arr.toString(Charsets.UTF_8))

    } else {
      Assertions.assertTrue(false)
    }

  }

}
