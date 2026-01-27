/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.ssh.jes

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.sshMockResponseDispatcher
import org.zowe.kotlinsdk.core.jes.api.JesAPI
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.mockSshConnection
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.definitions.SshJobItem
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.messaging.SshListJobsRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.messaging.SshListJobsResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse
import kotlin.text.contains

class SshListJobsTestSpec : ShouldSpec({
  val jesApi = zoweAPIProvider.getApi(WrapperType.SSH_NATIVE, JesAPI::class.java)

  afterEach {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("listJobs") {
    should("listJobs execute successfully fetching a list of jobs by the provided prefix") {
      val jobName = "TESTJN1"

      sshMockResponseDispatcher.injectResolver(
        "ssh:listJobs_success_output",
        resolver = {
          it.contains("### list_jobs") && it.contains(jobName)
        },
        handler = { _, _ ->
          SshMockCommandResponse(
            "=== JOBS INFO OUTPUT START ===\n" +
            "=== JOB 0 OUTPUT START ===\n" +
            "=|||= Job subsystem: JES2 =|||=\n" +
            "=|||= Job name: $jobName =|||=\n" +
            "=|||= Job ID: TESTJI1 =|||=\n" +
            "=|||= Job owner: TESTJO1 =|||=\n" +
            "=|||= Job RC: ABEND S222 =|||=\n" +
            "=|||= Job type: TSU =|||=\n" +
            "=|||= Job phase (num): 20 =|||=\n" +
            "=|||= Job phase name: AWAITING OUTPUT =|||=\n" +
            "=|||= Job class:  =|||=\n" +
            "=|||= Job queue: PRINT =|||=\n" +
            "=== JOB 0 OUTPUT END ===\n" +
            "=== JOB 1 OUTPUT START ===\n" +
            "=|||= Job subsystem: JES2 =|||=\n" +
            "=|||= Job name: $jobName =|||=\n" +
            "=|||= Job ID: TESTJI2 =|||=\n" +
            "=|||= Job owner: TESTJO1 =|||=\n" +
            "=|||= Job RC: ABEND S522 =|||=\n" +
            "=|||= Job type: TSU =|||=\n" +
            "=|||= Job phase (num): 20 =|||=\n" +
            "=|||= Job phase name: AWAITING OUTPUT =|||=\n" +
            "=|||= Job class:  =|||=\n" +
            "=|||= Job queue: PRINT =|||=\n" +
            "=== JOB 1 OUTPUT END ===\n" +
            "=== JOB 2 OUTPUT START ===\n" +
            "=|||= Job subsystem: JES2 =|||=\n" +
            "=|||= Job name: $jobName =|||=\n" +
            "=|||= Job ID: TESTJI3 =|||=\n" +
            "=|||= Job owner: TESTJO2 =|||=\n" +
            "=|||= Job RC: CC 0000 =|||=\n" +
            "=|||= Job type: JOB =|||=\n" +
            "=|||= Job phase (num): 20 =|||=\n" +
            "=|||= Job phase name: AWAITING OUTPUT =|||=\n" +
            "=|||= Job class:  =|||=\n" +
            "=|||= Job queue: PRINT =|||=\n" +
            "=== JOB 2 OUTPUT END ===\n" +
            "=== JOB 3 OUTPUT START ===\n" +
            "=|||= Job subsystem: JES2 =|||=\n" +
            "=|||= Job name: $jobName =|||=\n" +
            "=|||= Job ID: TESTJI4 =|||=\n" +
            "=|||= Job owner: TESTJO3 =|||=\n" +
            "=|||= Job RC:  =|||=\n" +
            "=|||= Job type: STC =|||=\n" +
            "=|||= Job phase (num): 14 =|||=\n" +
            "=|||= Job phase name: EXECUTING =|||=\n" +
            "=|||= Job class:  =|||=\n" +
            "=|||= Job queue: EXECUTION =|||=\n" +
            "=== JOB 3 OUTPUT END ===\n" +
            "=== JOBS INFO OUTPUT END ==="
          )
        }
      )

      val listJobsRequest = SshListJobsRequest(mockSshConnection, jobName, "", "")
      val listJobsResponse = jesApi.listJobs(listJobsRequest)

      if (listJobsResponse !is SshListJobsResponse) {
        fail("Should be instance of ${SshListJobsResponse::class.java.name}")
      } else {
        assertSoftly {
          listJobsResponse.status.type shouldBe StatusType.SUCCESS
          listJobsResponse.jobs.size shouldBe 4
          listJobsResponse.jobs[0].name shouldBe jobName
          listJobsResponse.jobs[0].id shouldBe "TESTJI1"
          listJobsResponse.jobs[0].owner shouldBe "TESTJO1"
          listJobsResponse.jobs[0].type shouldBe SshJobItem.SshJobType.TSU
          listJobsResponse.jobs[1].name shouldBe jobName
          listJobsResponse.jobs[1].id shouldBe "TESTJI2"
          listJobsResponse.jobs[1].owner shouldBe "TESTJI1"
          listJobsResponse.jobs[1].returnCode shouldBe "ABEND S522"
          listJobsResponse.jobs[1].type shouldBe SshJobItem.SshJobType.TSU
          listJobsResponse.jobs[2].name shouldBe jobName
          listJobsResponse.jobs[2].id shouldBe "TESTJI3"
          listJobsResponse.jobs[2].owner shouldBe "TESTJO2"
          listJobsResponse.jobs[2].returnCode shouldBe "CC 0000"
          listJobsResponse.jobs[2].type shouldBe SshJobItem.SshJobType.JOB
          listJobsResponse.jobs[3].name shouldBe jobName
          listJobsResponse.jobs[3].id shouldBe "TESTJI4"
          listJobsResponse.jobs[3].owner shouldBe "TESTJO3"
          listJobsResponse.jobs[3].type shouldBe SshJobItem.SshJobType.STC
          listJobsResponse.jobs[3].jobStatus shouldBe SshJobItem.SshJobStatus.ACTIVE
        }
      }
    }

    should("listJobs execute successfully fetching a job info together with exec-data and step-data") {
      val jobName = "TESTJN2"

      sshMockResponseDispatcher.injectResolver(
        "ssh:listJobs_success_output_exec_and_step_data",
        resolver = {
          it.contains("### list_jobs") && it.contains(jobName)
        },
        handler = { _, _ ->
          SshMockCommandResponse(
            "=== JOBS INFO OUTPUT START ===\n" +
            "=== JOB 0 OUTPUT START ===\n" +
            "=|||= Job subsystem: JES2 =|||=\n" +
            "=|||= Job name: $jobName =|||=\n" +
            "=|||= Job ID: TESTJI1 =|||=\n" +
            "=|||= Job owner: TESTJO1 =|||=\n" +
            "=|||= Job RC: CC 0000 =|||=\n" +
            "=|||= Job type: JOB =|||=\n" +
            "=|||= Job phase (num): 20 =|||=\n" +
            "=|||= Job phase name: AWAITING OUTPUT =|||=\n" +
            "=|||= Job class:  =|||=\n" +
            "=|||= Job queue: PRINT =|||=\n" +
            "=|||= exec-system: TSTS =|||=\n" +
            "=|||= exec-member: TSTM =|||=\n" +
            "=|||= exec-started-date: 30 MAY 2025 =|||=\n" +
            "=|||= exec-started-time: 02.14.24 =|||=\n" +
            "=|||= exec-ended-date: 30 MAY 2025 =|||=\n" +
            "=|||= exec-ended-time: 02.19.19 =|||=\n" +
            "=///= STEP DATA START =///=\n" +
            "=/= JESMSGLG SUMMARY START =/=\n" +
            " 02.19.19 TSTJOB12  -                                      -----TIMINGS (MINS.)------                          -----PAGING COUNTS----\n" +
            " 02.19.19 TSTJOB12  -STEPNAME PROCSTEP    RC   EXCP   CONN       TCB       SRB  CLOCK          SERV  WORKLOAD  PAGE  SWAP   VIO SWAPS\n" +
            " 02.19.19 TSTJOB12  -STEP1                00   5371      0       .06       .00    4.9           117  BATCH       14     0     0     0\n" +
            " 02.19.19 TSTJOB12  -$jobName ENDED.  NAME-.TESTPGM             TOTAL TCB CPU TIME=      .06 TOTAL ELAPSED TIME=   4.9\n" +
            "=\\= JESMSGLG SUMMARY END =\\=\n" +
            "=/= JESYSMSG MESSAGES START =/=\n" +
            " IEF142I $jobName STEP1 - STEP WAS EXECUTED - COND CODE 0000\n" +
            " IEF373I STEP/STEP1   /START 2025150.0214\n" +
            " IEF032I STEP/STEP1   /STOP  2025150.0219\n" +
            "=\\= JESYSMSG MESSAGES END =\\=\n" +
            "=\\\\\\= STEP DATA END =\\\\\\=\n" +
            "=== JOB 0 OUTPUT END ===\n" +
            "=== JOB 1 OUTPUT START ===\n" +
            "=|||= Job subsystem: JES2 =|||=\n" +
            "=|||= Job name: $jobName =|||=\n" +
            "=|||= Job ID: TESTJI2 =|||=\n" +
            "=|||= Job owner: TESTJO1 =|||=\n" +
            "=|||= Job RC: CC 0000 =|||=\n" +
            "=|||= Job type: STC =|||=\n" +
            "=|||= Job phase (num): 20 =|||=\n" +
            "=|||= Job phase name: AWAITING OUTPUT =|||=\n" +
            "=|||= Job class:  =|||=\n" +
            "=|||= Job queue: PRINT =|||=\n" +
            "=|||= exec-system: TSTS =|||=\n" +
            "=|||= exec-member: TSTM =|||=\n" +
            "=|||= exec-started-date: 23 MAY 2025 =|||=\n" +
            "=|||= exec-started-time: 10.23.01 =|||=\n" +
            "=|||= exec-ended-date: 29 MAY 2025 =|||=\n" +
            "=|||= exec-ended-time: 05.14.50 =|||=\n" +
            "=///= STEP DATA START =///=\n" +
            "=/= JESYSMSG MESSAGES START =/=\n" +
            " IEF142I $jobName $jobName - STEP WAS EXECUTED - COND CODE 0000\n" +
            " IEF373I STEP/STEP1   /START 2025143.1023\n" +
            " IEF032I STEP/STEP1   /STOP  2025143.1024 \n" +
            " IEF142I $jobName $jobName - STEP WAS EXECUTED - COND CODE 0000\n" +
            " IEF373I STEP/STEP2   /START 2025143.1024\n" +
            " IEF032I STEP/STEP2   /STOP  2025143.1028 \n" +
            " IEF472I $jobName $jobName - COMPLETION CODE - SYSTEM=EC6 USER=0000 REASON=0000FF0F\n" +
            " IEF373I STEP/STEP3   /START 2025143.1028\n" +
            " IEF032I STEP/STEP3   /STOP  2025146.0514 \n" +
            "=\\= JESYSMSG MESSAGES END =\\=\n" +
            "=\\\\\\= STEP DATA END =\\\\\\=\n" +
            "=== JOB 1 OUTPUT END ===\n" +
            "=== JOBS INFO OUTPUT END ==="
          )
        }
      )

      val listJobsRequest = SshListJobsRequest(
        mockSshConnection,
        jobName,
        "",
        "",
        isFetchExecData = true,
        isFetchStepData = true
      )
      val listJobsResponse = jesApi.listJobs(listJobsRequest)

      if (listJobsResponse !is SshListJobsResponse) {
        fail("Should be instance of ${SshListJobsResponse::class.java.name}")
      } else {
        assertSoftly {
          listJobsResponse.status.type shouldBe StatusType.SUCCESS
          listJobsResponse.jobs.size shouldBe 2
          listJobsResponse.jobs[0].jobStatus shouldBe SshJobItem.SshJobStatus.OUTPUT
          listJobsResponse.jobs[0].name shouldBe jobName
          listJobsResponse.jobs[0].stepData.size shouldBe 1
          listJobsResponse.jobs[0].stepData[0].stepName shouldBe "STEP1"
          listJobsResponse.jobs[0].stepData[0].abendReasonCode shouldBe null
          listJobsResponse.jobs[0].stepData[0].completion shouldBe "CC 0000"

          listJobsResponse.jobs[1].jobStatus shouldBe SshJobItem.SshJobStatus.OUTPUT
          listJobsResponse.jobs[1].name shouldBe jobName
          listJobsResponse.jobs[1].stepData.size shouldBe 3
          listJobsResponse.jobs[1].stepData[0].stepName shouldBe "STEP1"
          listJobsResponse.jobs[1].stepData[0].abendReasonCode shouldBe null
          listJobsResponse.jobs[1].stepData[0].completion shouldBe "CC 0000"
          listJobsResponse.jobs[1].stepData[1].stepName shouldBe "STEP2"
          listJobsResponse.jobs[1].stepData[1].abendReasonCode shouldBe null
          listJobsResponse.jobs[1].stepData[1].completion shouldBe "CC 0000"
          listJobsResponse.jobs[1].stepData[2].stepName shouldBe "STEP3"
          listJobsResponse.jobs[1].stepData[2].abendReasonCode shouldBe 65295
          listJobsResponse.jobs[1].stepData[2].completion shouldBe "ABEND SEC6"
        }
      }
    }

    should("listJobs fail to find the job with the provided name and ID") {
      val jobName = "TESTJN3"
      val jobId = "TESTJI4"

      sshMockResponseDispatcher.injectResolver(
        "ssh:listJobs_fail_not_found",
        resolver = {
          it.contains("### list_jobs") && it.contains(jobName)
        },
        handler = { _, _ ->
          SshMockCommandResponse("=== JOB BY NAME NONEXST IS NOT FOUND ===", exitCode = 255)
        }
      )

      val listJobsRequest = SshListJobsRequest(mockSshConnection, jobName, jobId, "")
      val listJobsResponse = jesApi.listJobs(listJobsRequest)

      if (listJobsResponse !is SshListJobsResponse) {
        fail("Should be instance of ${SshListJobsResponse::class.java.name}")
      } else {
        assertSoftly {
          listJobsResponse.status.type shouldBe StatusType.ERROR
        }
      }
    }

    should("listJobs fail to execute SSH request due to ISFEXEC error") {
      val jobName = "TESTJN4"

      sshMockResponseDispatcher.injectResolver(
        "ssh:listJobs_fail_isfexec_error",
        resolver = {
          it.contains("### list_jobs") && it.contains(jobName)
        },
        handler = { _, _ ->
          SshMockCommandResponse("=== ISFEXEC ERROR, RC=8 ===", exitCode = 8)
        }
      )

      val listJobsRequest = SshListJobsRequest(mockSshConnection, jobName, "", "")
      val listJobsResponse = jesApi.listJobs(listJobsRequest)

      if (listJobsResponse !is SshListJobsResponse) {
        fail("Should be instance of ${SshListJobsResponse::class.java.name}")
      } else {
        assertSoftly {
          listJobsResponse.status.type shouldBe StatusType.ERROR
        }
      }
    }

    should("listJobs execute successfully fetching a single cancelled job info with step-data") {
      val jobName = "TESTJN5"

      sshMockResponseDispatcher.injectResolver(
        "ssh:listJobs_success_cancelled_job",
        resolver = {
          it.contains("### list_jobs") && it.contains(jobName)
        },
        handler = { _, _ ->
          SshMockCommandResponse(
            "=== JOBS INFO OUTPUT START ===\n" +
            "=== JOB 0 OUTPUT START ===\n" +
            "=|||= Job subsystem: JES2 =|||=\n" +
            "=|||= Job name: $jobName =|||=\n" +
            "=|||= Job ID: TESTJI1 =|||=\n" +
            "=|||= Job owner: IZUSVR =|||=\n" +
            "=|||= Job RC: ABEND SEC6 =|||=\n" +
            "=|||= Job type: STC =|||=\n" +
            "=|||= Job phase (num): 20 =|||=\n" +
            "=|||= Job phase name: AWAITING OUTPUT =|||=\n" +
            "=|||= Job class:  =|||=\n" +
            "=|||= Job queue: PRINT =|||=\n" +
            "=///= STEP DATA START =///=\n" +
            "=/= JESYSMSG MESSAGES START =/=\n" +
            " IEF142I $jobName $jobName - STEP WAS EXECUTED - COND CODE 0000\n" +
            " IEF373I STEP/STEP1   /START 2025143.1023\n" +
            " IEF032I STEP/STEP1   /STOP  2025143.1024 \n" +
            " IEF142I $jobName $jobName - STEP WAS EXECUTED - COND CODE 0000\n" +
            " IEF373I STEP/STEP2   /START 2025143.1024\n" +
            " IEF032I STEP/STEP2   /STOP  2025143.1028 \n" +
            " IEF472I $jobName $jobName - COMPLETION CODE - SYSTEM=EC6 USER=0000 REASON=0000FF0F\n" +
            " IEF373I STEP/STEP3   /START 2025143.1028\n" +
            " IEF032I STEP/STEP3   /STOP  2025146.0514 \n" +
            "=\\= JESYSMSG MESSAGES END =\\=\n" +
            "=\\\\\\= STEP DATA END =\\\\\\=\n" +
            "=== JOB 0 OUTPUT END ===\n" +
            "=== JOBS INFO OUTPUT END ==="
          )
        }
      )

      val listJobsRequest = SshListJobsRequest(
        mockSshConnection,
        jobName,
        "",
        "",
        isFetchStepData = true
      )
      val listJobsResponse = jesApi.listJobs(listJobsRequest)

      if (listJobsResponse !is SshListJobsResponse) {
        fail("Should be instance of ${SshListJobsResponse::class.java.name}")
      } else {
        assertSoftly {
          listJobsResponse.status.type shouldBe StatusType.SUCCESS
          listJobsResponse.jobs.size shouldBe 1
          listJobsResponse.jobs[0].jobStatus shouldBe SshJobItem.SshJobStatus.OUTPUT
          listJobsResponse.jobs[0].name shouldBe jobName
          listJobsResponse.jobs[0].stepData.size shouldBe 3
          listJobsResponse.jobs[0].stepData[0].stepName shouldBe "STEP1"
          listJobsResponse.jobs[0].stepData[0].abendReasonCode shouldBe null
          listJobsResponse.jobs[0].stepData[0].completion shouldBe "CC 0000"
          listJobsResponse.jobs[0].stepData[1].stepName shouldBe "STEP2"
          listJobsResponse.jobs[0].stepData[1].abendReasonCode shouldBe null
          listJobsResponse.jobs[0].stepData[1].completion shouldBe "CC 0000"
          listJobsResponse.jobs[0].stepData[2].stepName shouldBe "STEP3"
          listJobsResponse.jobs[0].stepData[2].abendReasonCode shouldBe 65295
          listJobsResponse.jobs[0].stepData[2].completion shouldBe "ABEND SEC6"
        }
      }
    }
  }
})
