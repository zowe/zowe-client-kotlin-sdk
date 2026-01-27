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
import org.zowe.kotlinsdk.providers.zowe.zosmf.jes.messaging.ZosmfListJobsRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.jes.messaging.ZosmfListJobsResponse

class ZosmfListJobsTestSpec : ShouldSpec({
  val jesApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, JesAPI::class.java)

  afterEach {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("listJobs") {
    should("listJobs returns jobs list") {
      val jobname = "TESTJ1"

      zosmfMockResponseDispatcher.injectResolver(
        "listJobs_success",
        { it.requestLine.contains("/zosmf/restjobs/jobs?prefix=$jobname") },
        {
          MockResponse()
            .setBody(
              "[" +
                "{" +
                  "\"class\":\"T\"," +
                  "\"files-url\":\"https://tsthost:443/zosmf/restjobs/jobs/TESTJI1XCFPLX01E1BE8B01.......%3A/files\"," +
                  "\"job-correlator\":\"TESTJI1XCFPLX01E1BE8B01.......:\"," +
                  "\"jobid\":\"TESTJI1\"," +
                  "\"jobname\":\"$jobname\"," +
                  "\"owner\":\"TSTUSR\"," +
                  "\"phase\":20," +
                  "\"phase-name\":\"Job is on the hard copy queue\"," +
                  "\"retcode\":\"CC 0000\"," +
                  "\"status\":\"OUTPUT\"," +
                  "\"subsystem\":\"JES2\"," +
                  "\"type\":\"JOB\"," +
                  "\"url\":\"https://tsthost:443/zosmf/restjobs/jobs/TESTJI1XCFPLX01E1BE8B01.......%3A\"" +
                "}," +
                "{" +
                  "\"class\":\"T\"," +
                  "\"files-url\":\"https://tsthost:443/zosmf/restjobs/jobs/TESTJI2XCFPLX01E1BE8B01.......%3A/files\"," +
                  "\"job-correlator\":\"TESTJI2XCFPLX01E1BE8B01.......:\"," +
                  "\"jobid\":\"TESTJI2\"," +
                  "\"jobname\":\"$jobname\"," +
                  "\"owner\":\"TSTUSR\"," +
                  "\"phase\":20," +
                  "\"phase-name\":\"Job is on the hard copy queue\"," +
                  "\"retcode\":\"CC 0000\"," +
                  "\"status\":\"OUTPUT\"," +
                  "\"subsystem\":\"JES2\"," +
                  "\"type\":\"JOB\"," +
                  "\"url\":\"https://tsthost:443/zosmf/restjobs/jobs/TESTJI2XCFPLX01E1BE8B01.......%3A\"" +
                "}," +
                "{" +
                  "\"class\":\"B\"" +
                  "\"files-url\":\"https://tsthost:443/zosmf/restjobs/jobs/TESTJI3XCFPLX01E1BE8B01.......%3A/files\"," +
                  "\"job-correlator\":\"TESTJI3XCFPLX01E1BE8B01.......:\"," +
                  "\"jobid\":\"TESTJI3\"," +
                  "\"jobname\":\"$jobname\"," +
                  "\"owner\":\"TSTUSR\"," +
                  "\"phase\":14," +
                  "\"phase-name\":\"Job is actively executing\"," +
                  "\"retcode\":null," +
                  "\"status\":\"ACTIVE\"," +
                  "\"subsystem\":\"JES2\"," +
                  "\"type\":\"JOB\"," +
                  "\"url\":\"https://tsthost:443/zosmf/restjobs/jobs/TESTJI3XCFPLX01E1BE8B01.......%3A\"" +
                "}" +
              "]"
            )
            .addHeader("Content-Type", "application/json")
        }
      )

      val listJobsRequest = ZosmfListJobsRequest(mockHttpConnection, jobname, "", "")
      val listJobsResponse = jesApi.listJobs(listJobsRequest) as? ZosmfListJobsResponse
        ?: fail("Should be instance of ${ZosmfListJobsResponse::class.java.name}")

      assertSoftly {
        listJobsResponse.status.type shouldBe StatusType.SUCCESS
        listJobsResponse.jobs.size shouldBe 3
        listJobsResponse.jobs[0].name shouldBe jobname
        listJobsResponse.jobs[0].jobClass shouldBe "T"
        listJobsResponse.jobs[0].jobStatus shouldBe ZosmfJobItem.ZosmfJobStatus.OUTPUT
        listJobsResponse.jobs[1].name shouldBe jobname
        listJobsResponse.jobs[1].jobClass shouldBe "T"
        listJobsResponse.jobs[1].jobStatus shouldBe ZosmfJobItem.ZosmfJobStatus.OUTPUT
        listJobsResponse.jobs[2].name shouldBe jobname
        listJobsResponse.jobs[2].jobClass shouldBe "B"
        listJobsResponse.jobs[2].jobStatus shouldBe ZosmfJobItem.ZosmfJobStatus.ACTIVE
      }
    }

    should("listJobs return the a single job instance with exec data by job ID") {
      val jobid = "TESTJI2"

      zosmfMockResponseDispatcher.injectResolver(
        "listJobs_success_exec_data",
        { it.requestLine.contains("/zosmf/restjobs/jobs?jobid=$jobid&exec-data=Y") },
        {
          MockResponse()
            .setBody(
              "[" +
                "{" +
                  "\"class\":\"T\"," +
                  "\"exec-ended\": \"2025-11-05T13:52:21.920Z\", " +
                  "\"exec-member\": \"SMFT\"," +
                  "\"exec-started\": \"2025-11-05T13:52:21.310Z\"," +
                  "\"exec-submitted\": \"2025-11-05T13:52:21.290Z\"," +
                  "\"exec-system\": \"SYST\"" +
                  "\"files-url\":\"https://tsthost:443/zosmf/restjobs/jobs/${jobid}XCFPLX01E1BE8B01.......%3A/files\"," +
                  "\"job-correlator\":\"${jobid}XCFPLX01E1BE8B01.......:\"," +
                  "\"jobid\":\"$jobid\"," +
                  "\"jobname\":\"TESTJN2\"," +
                  "\"owner\":\"TSTUSR\"," +
                  "\"phase\":20," +
                  "\"phase-name\":\"Job is on the hard copy queue\"," +
                  "\"retcode\":\"CC 0000\"," +
                  "\"status\":\"OUTPUT\"," +
                  "\"subsystem\":\"JES2\"," +
                  "\"type\":\"JOB\"," +
                  "\"url\":\"https://tsthost:443/zosmf/restjobs/jobs/${jobid}XCFPLX01E1BE8B01.......%3A\"" +
                "}" +
              "]"
            )
            .addHeader("Content-Type", "application/json")
        }
      )

      val listJobsRequest = ZosmfListJobsRequest(
        mockHttpConnection,
        jobPrefix = "",
        jobId = jobid,
        jobOwner = "",
        isFetchExecData = true
      )
      val listJobsResponse = jesApi.listJobs(listJobsRequest) as? ZosmfListJobsResponse
        ?: fail("Should be instance of ${ZosmfListJobsResponse::class.java.name}")

      assertSoftly {
        listJobsResponse.status.type shouldBe StatusType.SUCCESS
        listJobsResponse.jobs.size shouldBe 1
        listJobsResponse.jobs[0].id shouldBe jobid
        listJobsResponse.jobs[0].jobStatus shouldBe ZosmfJobItem.ZosmfJobStatus.OUTPUT
        listJobsResponse.jobs[0].execMember shouldBe "SMFT"
        listJobsResponse.jobs[0].execSystem shouldBe "SYST"
      }
    }

    should("listJobs fail to return a list of jobs because there are no jobs for the prefix") {
      val jobname = "TESTJ3"

      zosmfMockResponseDispatcher.injectResolver(
        "listJobs_fail_404",
        { it.requestLine.contains("/zosmf/restjobs/jobs?prefix=$jobname") },
        {
          MockResponse()
            .addHeader("Content-Type", "application/json")
            .setResponseCode(404)
        }
      )

      val listJobsRequest = ZosmfListJobsRequest(mockHttpConnection, jobname, "", "")
      val listJobsResponse = jesApi.listJobs(listJobsRequest) as? ZosmfListJobsResponse
        ?: fail("Should be instance of ${ZosmfListJobsResponse::class.java.name}")

      assertSoftly {
        listJobsResponse.status.type shouldBe StatusType.ERROR
        listJobsResponse.status.text shouldContain "404"
      }
    }

    should("listJobs fail to return a list of jobs due to service error") {
      val jobname = "TESTJ4"

      zosmfMockResponseDispatcher.injectResolver(
        "listJobs_fail_500",
        { it.requestLine.contains("/zosmf/restjobs/jobs?prefix=$jobname") },
        {
          MockResponse()
            .addHeader("Content-Type", "application/json")
            .setResponseCode(500)
        }
      )

      val listJobsRequest = ZosmfListJobsRequest(mockHttpConnection, jobname, "", "")
      val listJobsResponse = jesApi.listJobs(listJobsRequest) as? ZosmfListJobsResponse
        ?: fail("Should be instance of ${ZosmfListJobsResponse::class.java.name}")

      assertSoftly {
        listJobsResponse.status.type shouldBe StatusType.ERROR
        listJobsResponse.status.text shouldContain "500"
      }
    }
  }
})