/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBA Group 2020
 */

import org.zowe.kotlinsdk.BaseTest
import org.zowe.kotlinsdk.HoldJobRequestBody
import org.zowe.kotlinsdk.JESApi
import org.zowe.kotlinsdk.ReleaseJobRequestBody
import org.zowe.kotlinsdk.HoldJobRequest
import org.zowe.kotlinsdk.ReleaseJobRequest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class HoldFor20sThenReleaseJobTest : BaseTest() {
    val JOB_ID = "JOB06152"
    val JOB_NAME = "NOTHINGJ"

    val JOB_CORRELATOR = "J0001561S0W1....D940967F.......:"

    // 0 - request was successful
    val SUCCESSFUL_REQUEST_RESULT = 0

    @Test
    fun holdFor10sThenReleaseJobTest() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getUnsafeOkHttpClient())
            .build()

        val request = retrofit.create(JESApi::class.java)
        val firstCall: Call<HoldJobRequest> = request.holdJobRequest(BASIC_AUTH_TOKEN,
            JOB_NAME, JOB_ID, HoldJobRequestBody()
        )

        enqueueHoldCallAndCheckResult(firstCall)

        Thread.sleep(10000)

        val secondCall: Call<ReleaseJobRequest> = request.releaseJobRequest(BASIC_AUTH_TOKEN,
            JOB_NAME, JOB_ID, ReleaseJobRequestBody()
        )

        enqueueReleaseCallAndCheckResult(secondCall)
    }

    fun enqueueHoldCallAndCheckResult(call: Call<HoldJobRequest>) {
        val response = call.execute()
        if (response.isSuccessful) {
            val jobStatus: HoldJobRequest = response.body() as HoldJobRequest
            println(jobStatus.status)
            Assertions.assertEquals(SUCCESSFUL_REQUEST_RESULT, jobStatus.status)
            Assertions.assertNotNull(jobStatus.owner)
            Assertions.assertEquals(jobStatus.owner?.lowercase(Locale.getDefault()), "hlh")
        } else {
            println(response.errorBody())
            Assertions.assertTrue(false)
        }
    }

    fun enqueueReleaseCallAndCheckResult(call: Call<ReleaseJobRequest>) {
        val response = call.execute()
        if (response.isSuccessful) {
            val jobStatus: ReleaseJobRequest = response.body() as ReleaseJobRequest
            println(jobStatus.status)
            Assertions.assertEquals(SUCCESSFUL_REQUEST_RESULT, jobStatus.status)
            Assertions.assertNotNull(jobStatus.owner)
            Assertions.assertEquals(jobStatus.owner?.lowercase(Locale.getDefault()), "hlh")
        } else {
            println(response.errorBody())
            Assertions.assertTrue(false)
        }
    }
}
