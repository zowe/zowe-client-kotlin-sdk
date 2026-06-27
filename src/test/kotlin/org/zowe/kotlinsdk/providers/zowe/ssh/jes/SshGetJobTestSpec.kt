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
import io.kotest.provided.ProjectConfig.sshMockResponseDispatcher
import org.zowe.kotlinsdk.core.jes.api.JesAPI
import io.kotest.provided.ProjectConfig.mockSshConnection
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.definitions.SshJobItem
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.messaging.SshGetJobRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.messaging.SshGetJobResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse
import kotlin.text.contains

class SshGetJobTestSpec : ShouldSpec({
  val jesApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, JesAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("getJob") {
    should("getJob execute successfully fetching a job info of a completed job") {
      val jobName = "TESTJN1"
      val jobId = "TESTJI1"

      sshMockResponseDispatcher.injectResolver(
        "ssh:getJob_success_output",
        resolver = {
          it.contains("### get_job") && it.contains(jobName) && it.contains(jobId)
        },
        handler = { _, _ ->
          SshMockCommandResponse(
            "=== JOBS INFO OUTPUT START ===\n" +
            "isfmsg2.1 is: ISF776I Processing started for action 1 of 1.\n" +
            "isfmsg2.2 is: ISF767I Request completed.\n" +
            "=== JOB 0 OUTPUT START ===\n" +
            "=|||= Job subsystem: JES2 =|||=\n" +
            "=|||= Job name: $jobName =|||=\n" +
            "=|||= Job ID: $jobId =|||=\n" +
            "=|||= Job owner: TESTJO1 =|||=\n" +
            "=|||= Job RC: CC 0000 =|||=\n" +
            "=|||= Job type: JOB =|||=\n" +
            "=|||= Job phase (num): 20 =|||=\n" +
            "=|||= Job phase name: AWAITING OUTPUT =|||=\n" +
            "=|||= Job class: A =|||=\n" +
            "=|||= Job queue: PRINT =|||=\n" +
            "=== JOB 0 OUTPUT END ===\n" +
            "=== JOBS INFO OUTPUT END ==="
          )
        }
      )

      val getJobRequest = SshGetJobRequest(mockSshConnection, jobName, jobId)
      val getJobResponse = jesApi.getJob(getJobRequest)

      if (getJobResponse !is SshGetJobResponse) {
        fail("Should be instance of ${SshGetJobResponse::class.java.name}")
      } else {
        assertSoftly {
          getJobResponse.status.type shouldBe StatusType.SUCCESS
          getJobResponse.job.jobStatus shouldBe SshJobItem.SshJobStatus.OUTPUT
          getJobResponse.job.returnCode shouldBe "CC 0000"
          getJobResponse.job.id shouldBe jobId
          getJobResponse.job.name shouldBe jobName
          getJobResponse.job.owner shouldBe "TESTJO1"
        }
      }
    }

    should("getJob execute successfully fetching a job info together with exec-data and step-data") {
      val jobName = "TESTJN2"
      val jobId = "TESTJI2"

      sshMockResponseDispatcher.injectResolver(
        "ssh:getJob_success_extended",
        resolver = {
          it.contains("### get_job") && it.contains(jobName) && it.contains(jobId)
        },
        handler = { _, _ ->
          SshMockCommandResponse(
            "=== JOBS INFO OUTPUT START ===\n" +
            "isfmsg2.1 is: ISF776I Processing started for action 1 of 1.\n" +
            "isfmsg2.2 is: ISF767I Request completed.\n" +
            "=== JOB 0 OUTPUT START ===\n" +
            "=|||= Job subsystem: JES2 =|||=\n" +
            "=|||= Job name: $jobName =|||=\n" +
            "=|||= Job ID: $jobId =|||=\n" +
            "=|||= Job owner: TESTJO2 =|||=\n" +
            "=|||= Job RC: CC 0000 =|||=\n" +
            "=|||= Job type: JOB =|||=\n" +
            "=|||= Job phase (num): 20 =|||=\n" +
            "=|||= Job phase name: AWAITING OUTPUT =|||=\n" +
            "=|||= Job class: A =|||=\n" +
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
            "=== JOBS INFO OUTPUT END ==="
          )
        }
      )

      val getJobRequest = SshGetJobRequest(
        mockSshConnection,
        jobName,
        jobId,
        isFetchStepData = true,
        isFetchExecData = true
      )
      val getJobResponse = jesApi.getJob(getJobRequest)

      if (getJobResponse !is SshGetJobResponse) {
        fail("Should be instance of ${SshGetJobResponse::class.java.name}")
      } else {
        assertSoftly {
          getJobResponse.status.type shouldBe StatusType.SUCCESS
          getJobResponse.job.jobStatus shouldBe SshJobItem.SshJobStatus.OUTPUT
          getJobResponse.job.returnCode shouldBe "CC 0000"
          getJobResponse.job.id shouldBe jobId
          getJobResponse.job.name shouldBe jobName
          getJobResponse.job.execSystem shouldBe "TSTS"
          getJobResponse.job.execMember shouldBe "TSTM"
          getJobResponse.job.stepData.size shouldBe 1
          getJobResponse.job.stepData[0].stepName shouldBe "STEP1"
          getJobResponse.job.stepData[0].abendReasonCode shouldBe null
        }
      }
    }

    should("getJob execute successfully fetching a job info of a running STC job") {
      val jobName = "TESTJN3"
      val jobId = "TESTJI3"

      sshMockResponseDispatcher.injectResolver(
        "ssh:getJob_success_active_stc",
        resolver = {
          it.contains("### get_job") && it.contains(jobName) && it.contains(jobId)
        },
        handler = { _, _ ->
          SshMockCommandResponse(
            "=== JOBS INFO OUTPUT START ===\n" +
            "isfmsg2.1 is: ISF776I Processing started for action 1 of 1.\n" +
            "isfmsg2.2 is: ISF767I Request completed.\n" +
            "=== JOB 0 OUTPUT START ===\n" +
            "=|||= Job subsystem: JES2 =|||=\n" +
            "=|||= Job name: $jobName =|||=\n" +
            "=|||= Job ID: $jobId =|||=\n" +
            "=|||= Job owner: IZUSVR =|||=\n" +
            "=|||= Job RC:  =|||=\n" +
            "=|||= Job type: STC =|||=\n" +
            "=|||= Job phase (num): 14 =|||=\n" +
            "=|||= Job phase name: EXECUTING =|||=\n" +
            "=|||= Job class:  =|||=\n" +
            "=|||= Job queue: EXECUTION =|||=\n" +
            "=== JOB 0 OUTPUT END ===\n" +
            "=== JOBS INFO OUTPUT END ==="
          )
        }
      )

      val getJobRequest = SshGetJobRequest(mockSshConnection, jobName, jobId)
      val getJobResponse = jesApi.getJob(getJobRequest)

      if (getJobResponse !is SshGetJobResponse) {
        fail("Should be instance of ${SshGetJobResponse::class.java.name}")
      } else {
        assertSoftly {
          getJobResponse.status.type shouldBe StatusType.SUCCESS
          getJobResponse.job.jobStatus shouldBe SshJobItem.SshJobStatus.ACTIVE
          getJobResponse.job.returnCode shouldBe null
          getJobResponse.job.id shouldBe jobId
          getJobResponse.job.name shouldBe jobName
          getJobResponse.job.jobClass shouldBe "STC"
        }
      }
    }

    should("getJob fail to find the job with the provided name and ID") {
      val jobName = "TESTJN4"
      val jobId = "TESTJI4"

      sshMockResponseDispatcher.injectResolver(
        "ssh:getJob_fail_not_found",
        resolver = {
          it.contains("### get_job") && it.contains(jobName) && it.contains(jobId)
        },
        handler = { _, _ ->
          SshMockCommandResponse("=== JOB BY NAME NONEXST IS NOT FOUND ===", exitCode = 255)
        }
      )

      val getJobRequest = SshGetJobRequest(mockSshConnection, jobName, jobId)
      val getJobResponse = jesApi.getJob(getJobRequest)

      if (getJobResponse !is SshGetJobResponse) {
        fail("Should be instance of ${SshGetJobResponse::class.java.name}")
      } else {
        assertSoftly {
          getJobResponse.status.type shouldBe StatusType.ERROR
        }
      }
    }

    should("getJob fail to execute SSH request due to ISFEXEC error") {
      val jobName = "TESTJN5"
      val jobId = "TESTJI5"

      sshMockResponseDispatcher.injectResolver(
        "ssh:getJob_fail_isfexec_error",
        resolver = {
          it.contains("### get_job") && it.contains(jobName) && it.contains(jobId)
        },
        handler = { _, _ ->
          SshMockCommandResponse("=== ISFEXEC ERROR, RC=8 ===", exitCode = 8)
        }
      )

      val getJobRequest = SshGetJobRequest(mockSshConnection, jobName, jobId)
      val getJobResponse = jesApi.getJob(getJobRequest)

      if (getJobResponse !is SshGetJobResponse) {
        fail("Should be instance of ${SshGetJobResponse::class.java.name}")
      } else {
        assertSoftly {
          getJobResponse.status.type shouldBe StatusType.ERROR
        }
      }
    }

    should("getJob execute successfully fetching an abended job info with step-data") {
      val jobName = "TESTJN6"
      val jobId = "TESTJI6"

      sshMockResponseDispatcher.injectResolver(
        "ssh:getJob_success_abended_job",
        resolver = {
          it.contains("### get_job") && it.contains(jobName) && it.contains(jobId)
        },
        handler = { _, _ ->
          SshMockCommandResponse(
            "=== JOBS INFO OUTPUT START ===\n" +
            "isfmsg2.1 is: ISF776I Processing started for action 1 of 1.\n" +
            "isfmsg2.2 is: ISF767I Request completed.\n" +
            "=== JOB 0 OUTPUT START ===\n" +
            "=|||= Job subsystem: JES2 =|||=\n" +
            "=|||= Job name: $jobName =|||=\n" +
            "=|||= Job ID: $jobId =|||=\n" +
            "=|||= Job owner: TESTJO6 =|||=\n" +
            "=|||= Job RC: ABEND S222 =|||=\n" +
            "=|||= Job type: JOB =|||=\n" +
            "=|||= Job phase (num): 20 =|||=\n" +
            "=|||= Job phase name: AWAITING OUTPUT =|||=\n" +
            "=|||= Job class: A =|||=\n" +
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
            " 02.19.19 TSTJOB12  -STEP0001          *S222    209      0       .00       .00     .0                BATCH        0     0     0     0\n" +
            " 02.19.19 TSTJOB12  -$jobName ENDED.  NAME-.TESTPGM             TOTAL TCB CPU TIME=      .00 TOTAL ELAPSED TIME=   0.0\n" +
            "=\\= JESMSGLG SUMMARY END =\\=\n" +
            "=/= JESYSMSG MESSAGES START =/=\n" +
            " IEF142I $jobName STEP0001 - COMPLETION CODE - SYSTEM=222 USER=0000 REASON=00000000\n" +
            " IEF373I STEP/STEP0001/START 2025150.0214\n" +
            " IEF032I STEP/STEP0001/STOP  2025150.0219\n" +
            "=\\= JESYSMSG MESSAGES END =\\=\n" +
            "=\\\\\\= STEP DATA END =\\\\\\=\n" +
            "=== JOB 0 OUTPUT END ===\n" +
            "=== JOBS INFO OUTPUT END ==="
          )
        }
      )

      val getJobRequest = SshGetJobRequest(mockSshConnection, jobName, jobId, isFetchStepData = true)
      val getJobResponse = jesApi.getJob(getJobRequest)

      if (getJobResponse !is SshGetJobResponse) {
        fail("Should be instance of ${SshGetJobResponse::class.java.name}")
      } else {
        assertSoftly {
          getJobResponse.status.type shouldBe StatusType.SUCCESS
          getJobResponse.job.jobStatus shouldBe SshJobItem.SshJobStatus.OUTPUT
          getJobResponse.job.id shouldBe jobId
          getJobResponse.job.name shouldBe jobName
          getJobResponse.job.stepData.size shouldBe 1
          getJobResponse.job.stepData[0].stepName shouldBe "STEP0001"
          getJobResponse.job.stepData[0].abendReasonCode shouldBe 0
          getJobResponse.job.stepData[0].completion shouldBe "ABEND S222"
        }
      }
    }

    should("getJob execute successfully fetching a cancelled job info with step-data") {
      val jobName = "TESTJN7"
      val jobId = "TESTJI7"

      sshMockResponseDispatcher.injectResolver(
        "ssh:getJob_success_cancelled_job",
        resolver = {
          it.contains("### get_job") && it.contains(jobName) && it.contains(jobId)
        },
        handler = { _, _ ->
          SshMockCommandResponse(
            "=== JOBS INFO OUTPUT START ===\n" +
            "isfmsg2.1 is: ISF776I Processing started for action 1 of 1.\n" +
            "isfmsg2.2 is: ISF767I Request completed.\n" +
            "=== JOB 0 OUTPUT START ===\n" +
            "=|||= Job subsystem: JES2 =|||=\n" +
            "=|||= Job name: $jobName =|||=\n" +
            "=|||= Job ID: $jobId =|||=\n" +
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

      val getJobRequest = SshGetJobRequest(mockSshConnection, jobName, jobId, isFetchStepData = true)
      val getJobResponse = jesApi.getJob(getJobRequest)

      if (getJobResponse !is SshGetJobResponse) {
        fail("Should be instance of ${SshGetJobResponse::class.java.name}")
      } else {
        assertSoftly {
          getJobResponse.status.type shouldBe StatusType.SUCCESS
          getJobResponse.job.jobStatus shouldBe SshJobItem.SshJobStatus.OUTPUT
          getJobResponse.job.id shouldBe jobId
          getJobResponse.job.name shouldBe jobName
          getJobResponse.job.stepData.size shouldBe 3
          getJobResponse.job.stepData[0].stepName shouldBe "STEP1"
          getJobResponse.job.stepData[0].abendReasonCode shouldBe null
          getJobResponse.job.stepData[0].completion shouldBe "CC 0000"
          getJobResponse.job.stepData[1].stepName shouldBe "STEP2"
          getJobResponse.job.stepData[1].abendReasonCode shouldBe null
          getJobResponse.job.stepData[1].completion shouldBe "CC 0000"
          getJobResponse.job.stepData[2].stepName shouldBe "STEP3"
          getJobResponse.job.stepData[2].abendReasonCode shouldBe 65295
          getJobResponse.job.stepData[2].completion shouldBe "ABEND SEC6"
        }
      }
    }
  }
})
