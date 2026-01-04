/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.jes

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import okhttp3.mockwebserver.MockResponse
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.jes.api.JesAPI
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.mockHttpConnection
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.zosmfMockResponseDispatcher
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.zosmf.jes.definitions.ZosmfJobItem
import org.zowe.kotlinsdk.providers.zowe.zosmf.jes.messaging.ZosmfGetJobRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.jes.messaging.ZosmfGetJobResponse

class ZosmfGetJobTestSpec : ShouldSpec({
  val jesApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, JesAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("getJob") {
    should("getJob return the job instance without step data") {
      val jobname = "TESTJ1"
      val jobid = "TESTID1"

      zosmfMockResponseDispatcher.injectResolver(
        "getJob_success",
        { it.requestLine.contains("/zosmf/restjobs/jobs/${jobname}/${jobid}") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"class\":\"T\"," +
                "\"files-url\":\"https://tsthost:443/zosmf/restjobs/jobs/${jobid}XCFPLX01E1BE8B01.......%3A/files\"," +
                "\"job-correlator\":\"${jobid}XCFPLX01E1BE8B01.......:\"," +
                "\"jobid\":\"${jobid}\"," +
                "\"jobname\":\"${jobname}\"," +
                "\"owner\":\"TSTUSR\"," +
                "\"phase\":20," +
                "\"phase-name\":\"Job is on the hard copy queue\"," +
                "\"retcode\":\"CC 0000\"," +
                "\"status\":\"OUTPUT\"," +
                "\"subsystem\":\"JES2\"," +
                "\"type\":\"JOB\"," +
                "\"url\":\"https://tsthost:443/zosmf/restjobs/jobs/${jobid}XCFPLX01E1BE8B01.......%3A\"" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
        }
      )

      val getJobRequest = ZosmfGetJobRequest(mockHttpConnection, jobname, jobid)
      val getJobResponse = jesApi.getJob(getJobRequest) as? ZosmfGetJobResponse
        ?: fail("Should be instance of ${ZosmfGetJobResponse::class.java.name}")

      assertSoftly {
        getJobResponse.status.type shouldBe StatusType.SUCCESS
        getJobResponse.job.name shouldBe jobname
        getJobResponse.job.id shouldBe jobid
        getJobResponse.job.jobStatus shouldBe ZosmfJobItem.ZosmfJobStatus.OUTPUT
      }
    }

    should("getJob return the job instance by the job correlator") {
      val jobname = "TESTJ2"
      val jobid = "TESTID2"
      val jobCorrelator = "${jobid}XCFPLX01E1BE8B01......."

      zosmfMockResponseDispatcher.injectResolver(
        "getJob_success_correlator",
        { it.requestLine.contains("/zosmf/restjobs/jobs/${jobCorrelator}") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"class\":\"T\"," +
                "\"files-url\":\"https://tsthost:443/zosmf/restjobs/jobs/${jobCorrelator}%3A/files\"," +
                "\"job-correlator\":\"${jobCorrelator}:\"," +
                "\"jobid\":\"${jobid}\"," +
                "\"jobname\":\"${jobname}\"," +
                "\"owner\":\"TSTUSR\"," +
                "\"phase\":20," +
                "\"phase-name\":\"Job is on the hard copy queue\"," +
                "\"retcode\":\"CC 0000\"," +
                "\"status\":\"OUTPUT\"," +
                "\"subsystem\":\"JES2\"," +
                "\"type\":\"JOB\"," +
                "\"url\":\"https://tsthost:443/zosmf/restjobs/jobs/${jobCorrelator}%3A\"" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
        }
      )

      val getJobRequest = ZosmfGetJobRequest(mockHttpConnection, jobCorellator = jobCorrelator)
      val getJobResponse = jesApi.getJob(getJobRequest) as? ZosmfGetJobResponse
        ?: fail("Should be instance of ${ZosmfGetJobResponse::class.java.name}")

      assertSoftly {
        getJobResponse.status.type shouldBe StatusType.SUCCESS
        getJobResponse.job.name shouldBe jobname
        getJobResponse.job.id shouldBe jobid
        getJobResponse.job.jobStatus shouldBe ZosmfJobItem.ZosmfJobStatus.OUTPUT
      }
    }

    should("getJob return the job instance with step data") {
      val jobname = "TESTJ3"
      val jobid = "TESTID3"

      zosmfMockResponseDispatcher.injectResolver(
        "getJob_success_step_data",
        { it.requestLine.contains("/zosmf/restjobs/jobs/${jobname}/${jobid}") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"class\":\"T\"," +
                "\"files-url\":\"https://tsthost:443/zosmf/restjobs/jobs/${jobid}XCFPLX01E1BE8B01.......%3A/files\"," +
                "\"job-correlator\":\"${jobid}XCFPLX01E1BE8B01.......:\"," +
                "\"jobid\":\"${jobid}\"," +
                "\"jobname\":\"${jobname}\"," +
                "\"owner\":\"TSTUSR\"," +
                "\"phase\":20," +
                "\"phase-name\":\"Job is on the hard copy queue\"," +
                "\"retcode\":\"CC 0000\"," +
                "\"status\":\"OUTPUT\"," +
                "\"step-data\": [" +
                  "{" +
                    "\"active\": False," +
                    "\"completion\": \"CC 0000\"," +
                    "\"end-time\": \"2025-11-05T13:52:21.910\"," +
                    "\"proc-step-name\": \"\", " +
                    "\"program-name\": \"IKJEFT01\"," +
                    "\"selected-time\": \"2025-11-05T13:52:21.320\"," +
                    "\"smfid\": \"SMFT\"," +
                    "\"step-name\": \"STEP1\"," +
                    "\"step-number\": 1" +
                  "}," +
                  "{" +
                    "\"active\": True," +
                    "\"proc-step-name\": \"\"," +
                    "\"program-name\": \"\"," +
                    "\"step-name\": \"\"," +
                    "\"step-number\": 0" +
                  "}" +
                "]," +
                "\"subsystem\":\"JES2\"," +
                "\"type\":\"JOB\"," +
                "\"url\":\"https://tsthost:443/zosmf/restjobs/jobs/${jobid}XCFPLX01E1BE8B01.......%3A\"" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
        }
      )

      val getJobRequest = ZosmfGetJobRequest(mockHttpConnection, jobname, jobid, isFetchStepData = true)
      val getJobResponse = jesApi.getJob(getJobRequest) as? ZosmfGetJobResponse
        ?: fail("Should be instance of ${ZosmfGetJobResponse::class.java.name}")

      assertSoftly {
        getJobResponse.status.type shouldBe StatusType.SUCCESS
        getJobResponse.job.name shouldBe jobname
        getJobResponse.job.id shouldBe jobid
        getJobResponse.job.jobStatus shouldBe ZosmfJobItem.ZosmfJobStatus.OUTPUT
        getJobResponse.job.stepData.size shouldBe 2
        getJobResponse.job.stepData[0].stepName shouldBe "STEP1"
        getJobResponse.job.stepData[1].isActive shouldBe true
      }
    }

    should("getJob return the job instance with exec data") {
      val jobname = "TESTJ4"
      val jobid = "TESTID4"

      zosmfMockResponseDispatcher.injectResolver(
        "getJob_success_exec_data",
        { it.requestLine.contains("/zosmf/restjobs/jobs/${jobname}/${jobid}") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"class\":\"T\"," +
                "\"exec-ended\": \"2025-11-05T13:52:21.920Z\", " +
                "\"exec-member\": \"SMFT\"," +
                "\"exec-started\": \"2025-11-05T13:52:21.310Z\"," +
                "\"exec-submitted\": \"2025-11-05T13:52:21.290Z\"," +
                "\"exec-system\": \"SYST\"" +
                "\"files-url\":\"https://tsthost:443/zosmf/restjobs/jobs/${jobid}XCFPLX01E1BE8B01.......%3A/files\"," +
                "\"job-correlator\":\"${jobid}XCFPLX01E1BE8B01.......:\"," +
                "\"jobid\":\"${jobid}\"," +
                "\"jobname\":\"${jobname}\"," +
                "\"owner\":\"TSTUSR\"," +
                "\"phase\":20," +
                "\"phase-name\":\"Job is on the hard copy queue\"," +
                "\"retcode\":\"CC 0000\"," +
                "\"status\":\"OUTPUT\"," +
                "\"subsystem\":\"JES2\"," +
                "\"type\":\"JOB\"," +
                "\"url\":\"https://tsthost:443/zosmf/restjobs/jobs/${jobid}XCFPLX01E1BE8B01.......%3A\"" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
        }
      )

      val getJobRequest = ZosmfGetJobRequest(mockHttpConnection, jobname, jobid, isFetchStepData = true)
      val getJobResponse = jesApi.getJob(getJobRequest) as? ZosmfGetJobResponse
        ?: fail("Should be instance of ${ZosmfGetJobResponse::class.java.name}")

      assertSoftly {
        getJobResponse.status.type shouldBe StatusType.SUCCESS
        getJobResponse.job.name shouldBe jobname
        getJobResponse.job.id shouldBe jobid
        getJobResponse.job.jobStatus shouldBe ZosmfJobItem.ZosmfJobStatus.OUTPUT
        getJobResponse.job.execMember shouldBe "SMFT"
        getJobResponse.job.execSystem shouldBe "SYST"
      }
    }

    should("getJob fail to return the job instance because it is not found") {
      val jobname = "TESTJ5"
      val jobid = "TESTID5"

      zosmfMockResponseDispatcher.injectResolver(
        "getJob_fail_404",
        { it.requestLine.contains("/zosmf/restjobs/jobs/${jobname}/${jobid}") },
        {
          MockResponse()
            .addHeader("Content-Type", "application/json")
            .setResponseCode(404)
        }
      )

      val getJobRequest = ZosmfGetJobRequest(mockHttpConnection, jobname, jobid)
      val getJobResponse = jesApi.getJob(getJobRequest) as? ZosmfGetJobResponse
        ?: fail("Should be instance of ${ZosmfGetJobResponse::class.java.name}")

      assertSoftly {
        getJobResponse.status.type shouldBe StatusType.ERROR
        getJobResponse.status.text shouldContain "404"
      }
    }

    should("getJob fail to return the job instance due to service error") {
      val jobname = "TESTJ6"
      val jobid = "TESTID6"

      zosmfMockResponseDispatcher.injectResolver(
        "getJob_fail_500",
        { it.requestLine.contains("/zosmf/restjobs/jobs/${jobname}/${jobid}") },
        {
          MockResponse()
            .addHeader("Content-Type", "application/json")
            .setResponseCode(500)
        }
      )

      val getJobRequest = ZosmfGetJobRequest(mockHttpConnection, jobname, jobid)
      val getJobResponse = jesApi.getJob(getJobRequest) as? ZosmfGetJobResponse
        ?: fail("Should be instance of ${ZosmfGetJobResponse::class.java.name}")

      assertSoftly {
        getJobResponse.status.type shouldBe StatusType.ERROR
        getJobResponse.status.text shouldContain "500"
      }
    }

    should("getJob return failed job instance finished with abend") {
      val jobname = "TESTJ7"
      val jobid = "TESTID7"

      zosmfMockResponseDispatcher.injectResolver(
        "getJob_result_abend",
        { it.requestLine.contains("/zosmf/restjobs/jobs/${jobname}/${jobid}") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"owner\":\"TSTOWR\"," +
                "\"phase\":20," +
                "\"step-data\":[" +
                  "{" +
                    "\"smfid\":\"TSTS\"," +
                    "\"completion\":\"CC 0000\"," +
                    "\"step-number\":1," +
                    "\"program-name\":\"IZUPARMS\"," +
                    "\"end-time\":\"2025-05-23T10:24:12.930\"," +
                    "\"active\":false," +
                    "\"step-name\":\"ZTEST\"," +
                    "\"proc-step-name\":\"STARTING\"," +
                    "\"selected-time\":\"2025-05-23T10:23:01.380\"" +
                  "}," +
                  "{" +
                    "\"smfid\":\"TSTS\"," +
                    "\"completion\":\"CC 0000\"," +
                    "\"step-number\":2," +
                    "\"program-name\":\"BPXBATCH\"," +
                    "\"end-time\":\"2025-05-23T10:28:02.890\"," +
                    "\"active\":false," +
                    "\"step-name\":\"CONFTST\"," +
                    "\"proc-step-name\":\"STARTING\"," +
                    "\"selected-time\":\"2025-05-23T10:24:13.490\"" +
                  "}," +
                  "{" +
                    "\"smfid\":\"TSTS\"," +
                    "\"completion\":\"ABENDU3782\"," +
                    "\"abend-reason-code\":65295," +
                    "\"step-number\":3," +
                    "\"program-name\":\"BPXBATSL\"," +
                    "\"end-time\":\"2025-05-26T05:14:32.670\"," +
                    "\"active\":false," +
                    "\"step-name\":\"TSTSTP\"," +
                    "\"proc-step-name\":\"STARTING\"," +
                    "\"selected-time\":\"2025-05-23T10:28:02.970\"" +
                  "}," +
                  "{" +
                    "\"step-number\":0," +
                    "\"program-name\":\"\"," +
                    "\"active\":true," +
                    "\"step-name\":\"\"," +
                    "\"proc-step-name\":\"\"" +
                  "}" +
                "]," +
                "\"subsystem\":\"JES2\"," +
                "\"phase-name\":\"Job is on the hard copy queue\"," +
                "\"job-correlator\":\"S0001001TSTS....E0EDF743.......:\"," +
                "\"type\":\"STC\"," +
                "\"url\":\"https:\\/\\/tsthost:443\\/zosmf\\/restjobs\\/jobs\\/S0001001TSTS....E0EDF743.......%3A\"," +
                "\"jobid\":\"$jobid\"," +
                "\"class\":\"STC\"," +
                "\"files-url\":\"https:\\/\\/tsthost:443\\/zosmf\\/restjobs\\/jobs\\/S0001001TSTS....E0EDF743.......%3A\\/files\"," +
                "\"jobname\":\"$jobname\"," +
                "\"status\":\"OUTPUT\"," +
                "\"retcode\":\"ABEND SEC6\"," +
                "\"exec-member\":\"TSTM\"," +
                "\"exec-ended\":\"2025-05-26T05:14:32.700Z\"," +
                "\"exec-system\":\"TSTS\"," +
                "\"exec-submitted\":\"2025-05-23T10:22:45.550Z\"," +
                "\"exec-started\":\"2025-05-23T10:22:47.210Z\"" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
        }
      )

      val getJobRequest = ZosmfGetJobRequest(
        mockHttpConnection,
        jobname,
        jobid,
        isFetchStepData = true,
        isFetchExecData = true
      )
      val getJobResponse = jesApi.getJob(getJobRequest) as? ZosmfGetJobResponse
        ?: fail("Should be instance of ${ZosmfGetJobResponse::class.java.name}")

      assertSoftly {
        getJobResponse.status.type shouldBe StatusType.SUCCESS
        getJobResponse.job.stepData.size shouldBe 4
        getJobResponse.job.jobStatus shouldBe ZosmfJobItem.ZosmfJobStatus.OUTPUT
      }
    }
  }
})