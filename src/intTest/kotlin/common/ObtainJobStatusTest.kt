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

import org.zowe.kotlinsdk.JESApi
import org.zowe.kotlinsdk.Job
import org.zowe.kotlinsdk.UseStepData
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import retrofit2.Call

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ObtainJobStatusTest : BaseTest() {

  var jesApi: JESApi = buildGsonApi(BASE_URL, getUnsafeOkHttpClient())

  val JOB_CORRELATOR = "J0006081S0W1....D9A8D97A.......:"
  val JOB_ID = "JOB05569"
  val JOB_NAME = "NOTHINGJ"

  @Test
  fun obtainStatusByNameAndIdTest() {
    val call = jesApi.getJob(BASIC_AUTH_TOKEN, JOB_NAME, JOB_ID, UseStepData.DISABLE)
    executeCallAndCheckResult(call)
  }

  @Test
  fun obtainStatusByCorrelator() {
    val call = jesApi.getJob(BASIC_AUTH_TOKEN, JOB_CORRELATOR)
    executeCallAndCheckResult(call)
  }


  fun executeCallAndCheckResult(call: Call<Job>) {
    val response = call.execute()
    if (response.isSuccessful) {
      val jobStatus = response.body() as Job
      jobStatus.steps.forEach { el ->
        println(el)
      }

      Assertions.assertNotNull(jobStatus.owner)
      Assertions.assertEquals(jobStatus.owner.lowercase(), "hlh")

    } else {
      println(response.errorBody())
      Assertions.assertTrue(false)
    }

  }


}
