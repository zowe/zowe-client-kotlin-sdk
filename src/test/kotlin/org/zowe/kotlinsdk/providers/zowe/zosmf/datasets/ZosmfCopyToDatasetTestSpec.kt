/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import okhttp3.mockwebserver.MockResponse
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import io.kotest.provided.ProjectConfig.mockHttpConnection
import io.kotest.provided.ProjectConfig.zosmfMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfCopyToDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfCopyToDatasetRequestBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfCopyToDatasetResponse

class ZosmfCopyToDatasetTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, DatasetsAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("copyToDataset") {
    should("copyToDataset successfully copies a PS data set to a new PS data set") {
      val fromDsName = "CFDTEST1"
      val toDsName = "CTDTEST1"
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "copyToDataset_ps_to_ps_success",
        { it.requestLine.contains("/zosmf/restfiles/ds/$toDsName") },
        {
          recordedBody = it.body.readUtf8()
          MockResponse().setResponseCode(200)
        }
      )

      val copyToDatasetRequest = ZosmfCopyToDatasetRequest(
        mockHttpConnection,
        ZosmfCopyToDatasetRequestBody.FromDataset(fromDsName),
        toDsName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)
        as? ZosmfCopyToDatasetResponse
        ?: fail("Should be instance of ${ZosmfCopyToDatasetResponse::class.java.name}")

      assertSoftly {
        copyToDatasetResponse.status.type shouldBe StatusType.SUCCESS
        recordedBody shouldContain "\"request\": \"copy\""
        recordedBody shouldContain "\"from-dataset\""
      }
    }

    should("copyToDataset successfully copies a PS data set on a specified volume to an existing PS data set on a specified volume") {
      val fromDsName = "CFDTEST2"
      val toDsName = "CTDTEST2"
      val fromVolser = "FTEST"
      val toVolser = "TTEST"

      zosmfMockResponseDispatcher.injectResolver(
        "copyToDataset_ps_to_ps_volumes_success",
        { it.requestLine.contains("/zosmf/restfiles/ds/-($toVolser)/$toDsName") },
        { MockResponse().setResponseCode(200) }
      )

      val copyToDatasetRequest = ZosmfCopyToDatasetRequest(
        mockHttpConnection,
        ZosmfCopyToDatasetRequestBody.FromDataset(fromDsName, volser = fromVolser),
        toDsName,
        toVolser = toVolser
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)
        as? ZosmfCopyToDatasetResponse
        ?: fail("Should be instance of ${ZosmfCopyToDatasetResponse::class.java.name}")

      assertSoftly { copyToDatasetResponse.status.type shouldBe StatusType.SUCCESS }
    }

    should("copyToDataset successfully copies a PS data set to a new PDS data set member") {
      val fromDsName = "CFDTEST3"
      val toDsName = "CTDTEST3"
      val toMemberName = "TESTMEM1"

      zosmfMockResponseDispatcher.injectResolver(
        "copyToDataset_ps_to_pds_mem_success",
        { it.requestLine.contains("/zosmf/restfiles/ds/$toDsName($toMemberName)") },
        { MockResponse().setResponseCode(200) }
      )

      val copyToDatasetRequest = ZosmfCopyToDatasetRequest(
        mockHttpConnection,
        ZosmfCopyToDatasetRequestBody.FromDataset(fromDsName),
        toDsName,
        toMemberName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)
        as? ZosmfCopyToDatasetResponse
        ?: fail("Should be instance of ${ZosmfCopyToDatasetResponse::class.java.name}")

      assertSoftly { copyToDatasetResponse.status.type shouldBe StatusType.SUCCESS }
    }

    should("copyToDataset successfully copies a USS text file to a new PS data set") {
      val fromFile = "/test/path/text"
      val toDsName = "CTDTEST4"

      zosmfMockResponseDispatcher.injectResolver(
        "copyToDataset_uss_txt_to_ps_success",
        { it.requestLine.contains("/zosmf/restfiles/ds/$toDsName") },
        { MockResponse().setResponseCode(200) }
      )

      val copyToDatasetRequest = ZosmfCopyToDatasetRequest(
        mockHttpConnection,
        ZosmfCopyToDatasetRequestBody.FromFile(fromFile),
        toDsName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)
        as? ZosmfCopyToDatasetResponse
        ?: fail("Should be instance of ${ZosmfCopyToDatasetResponse::class.java.name}")

      assertSoftly { copyToDatasetResponse.status.type shouldBe StatusType.SUCCESS }
    }

    should("copyToDataset successfully copies a USS binary file to an existing PS data set") {
      val fromFile = "/test/path/bin"
      val toDsName = "CTDTEST5"

      zosmfMockResponseDispatcher.injectResolver(
        "copyToDataset_uss_bin_to_ps_success",
        { it.requestLine.contains("/zosmf/restfiles/ds/$toDsName") },
        { MockResponse().setResponseCode(200) }
      )

      val copyToDatasetRequest = ZosmfCopyToDatasetRequest(
        mockHttpConnection,
        ZosmfCopyToDatasetRequestBody.FromFile(fromFile),
        toDsName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)
        as? ZosmfCopyToDatasetResponse
        ?: fail("Should be instance of ${ZosmfCopyToDatasetResponse::class.java.name}")

      assertSoftly { copyToDatasetResponse.status.type shouldBe StatusType.SUCCESS }
    }

    should("copyToDataset successfully copies a USS file to an existing PDS data set member") {
      val fromFile = "/test/path/text"
      val toDsName = "CTDTEST6"
      val toMemberName = "TESTMEM1"

      zosmfMockResponseDispatcher.injectResolver(
        "copyToDataset_uss_txt_to_pds_success",
        { it.requestLine.contains("/zosmf/restfiles/ds/$toDsName($toMemberName)") },
        { MockResponse().setResponseCode(200) }
      )

      val copyToDatasetRequest = ZosmfCopyToDatasetRequest(
        mockHttpConnection,
        ZosmfCopyToDatasetRequestBody.FromFile(fromFile),
        toDsName,
        toMemberName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)
        as? ZosmfCopyToDatasetResponse
        ?: fail("Should be instance of ${ZosmfCopyToDatasetResponse::class.java.name}")

      assertSoftly { copyToDatasetResponse.status.type shouldBe StatusType.SUCCESS }
    }

    should("copyToDataset successfully copies a PDS member to an existing PS data set (member LRECL is larger than PS LRECL)") {
      val fromDsName = "CFDTEST7"
      val fromMemberName = "TESTMEM1"
      val toDsName = "CTDTEST7"
      val toMemberName = "TESTMEM2"

      zosmfMockResponseDispatcher.injectResolver(
        "copyToDataset_pds_to_pds_diff_lrecl_success",
        { it.requestLine.contains("/zosmf/restfiles/ds/$toDsName($toMemberName)") },
        { MockResponse().setResponseCode(200) }
      )

      val copyToDatasetRequest = ZosmfCopyToDatasetRequest(
        mockHttpConnection,
        ZosmfCopyToDatasetRequestBody.FromDataset(fromDsName, memberName = fromMemberName),
        toDsName,
        toMemberName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)
        as? ZosmfCopyToDatasetResponse
        ?: fail("Should be instance of ${ZosmfCopyToDatasetResponse::class.java.name}")

      assertSoftly { copyToDatasetResponse.status.type shouldBe StatusType.SUCCESS }
    }

    should("copyToDataset fail due to truncation error during copy from USS to an existing PS data set") {
      val fromFile = "/test/file/text"
      val toDsName = "CTDTEST8"

      zosmfMockResponseDispatcher.injectResolver(
        "copyToDataset_uss_to_ps_fail_trunc",
        { it.requestLine.contains("/zosmf/restfiles/ds/$toDsName") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":1," +
                "\"rc\":8," +
                "\"reason\":9," +
                "\"message\":\"Error executing command. exit_code=1\"," +
                "\"details\":[\"/bin/cp -T $fromFile //'$toDsName'\", \"cp: FSUM6260 write error on file \\\"//'$toDsName' \\\": EDC5003I Truncation of a record occurred during an I/O operation. (errno2=0xC0400060)\"]" +
                "\"stack\":[\"/bin/cp -T $fromFile //'$toDsName'\", \"cp: FSUM6260 write error on file \\\"//'$toDsName' \\\": EDC5003I Truncation of a record occurred during an I/O operation. (errno2=0xC0400060)\"]" +
              "}"
            )
            .setResponseCode(500)
        }
      )

      val copyToDatasetRequest = ZosmfCopyToDatasetRequest(
        mockHttpConnection,
        ZosmfCopyToDatasetRequestBody.FromFile(fromFile),
        toDsName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)
        as? ZosmfCopyToDatasetResponse
        ?: fail("Should be instance of ${ZosmfCopyToDatasetResponse::class.java.name}")

      assertSoftly {
        copyToDatasetResponse.status.type shouldBe StatusType.ERROR
        copyToDatasetResponse.status.text shouldContain "Truncation of a record occurred"
      }
    }

    should("copyToDataset fail due to truncation error during copy from USS to a new PDS member") {
      val fromFile = "/test/file/text"
      val toDsName = "CTDTEST9"
      val toMemberName = "TESTMEM1"

      zosmfMockResponseDispatcher.injectResolver(
        "copyToDataset_uss_to_pds_fail_trunc",
        { it.requestLine.contains("/zosmf/restfiles/ds/$toDsName($toMemberName)") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":6," +
                "\"rc\":8," +
                "\"reason\":520," +
                "\"message\":\"fwrite() error\"," +
                "\"details\":[\"EDC5003I Truncation of a record occurred during an I/O operation. (errno2=0xC0440016), __amrc: __last_op=151 __code=0x00000000\"]" +
                "\"stack\":[\"EDC5003I Truncation of a record occurred during an I/O operation. (errno2=0xC0440016), __amrc: __last_op=151 __code=0x00000000\"]" +
              "}"
            )
            .setResponseCode(500)
        }
      )

      val copyToDatasetRequest = ZosmfCopyToDatasetRequest(
        mockHttpConnection,
        ZosmfCopyToDatasetRequestBody.FromFile(fromFile),
        toDsName,
        toMemberName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)
        as? ZosmfCopyToDatasetResponse
        ?: fail("Should be instance of ${ZosmfCopyToDatasetResponse::class.java.name}")

      assertSoftly {
        copyToDatasetResponse.status.type shouldBe StatusType.ERROR
        copyToDatasetResponse.status.text shouldContain "Truncation of a record occurred"
      }
    }

    should("copyToDataset fail due to truncation error during copy a PS data set to a new PDS member") {
      val fromDsName = "CFDTESTA"
      val toDsName = "CTDTESTA"
      val toMemberName = "TESTMEM1"

      zosmfMockResponseDispatcher.injectResolver(
        "copyToDataset_ps_to_pds_fail_trunc",
        { it.requestLine.contains("/zosmf/restfiles/ds/$toDsName($toMemberName)") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":4," +
                "\"rc\":16," +
                "\"reason\":0," +
                "\"message\":\"Copy dataset failed\"," +
                "\"details\":[\"ISRZ002 'LMCOPY' terminated - 'LMCOPY' has been terminated due to truncation continue processing.\"]" +
                "\"stack\":[\"ISRZ002 'LMCOPY' terminated - 'LMCOPY' has been terminated due to truncation continue processing.\"]" +
              "}"
            )
            .setResponseCode(500)
        }
      )

      val copyToDatasetRequest = ZosmfCopyToDatasetRequest(
        mockHttpConnection,
        ZosmfCopyToDatasetRequestBody.FromDataset(fromDsName),
        toDsName,
        toMemberName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)
        as? ZosmfCopyToDatasetResponse
        ?: fail("Should be instance of ${ZosmfCopyToDatasetResponse::class.java.name}")

      assertSoftly {
        copyToDatasetResponse.status.type shouldBe StatusType.ERROR
        copyToDatasetResponse.status.text shouldContain "truncation continue processing"
      }
    }

    should("copyToDataset fail due to truncation error during copy a PS data set to an existing PS data set (first PS LRECL is better than the second's)") {
      val fromDsName = "CFDTESTB"
      val toDsName = "CTDTESTB"

      zosmfMockResponseDispatcher.injectResolver(
        "copyToDataset_ps_to_ps_fail_trunc",
        { it.requestLine.contains("/zosmf/restfiles/ds/$toDsName") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":4," +
                "\"rc\":16," +
                "\"reason\":0," +
                "\"message\":\"Copy dataset failed\"," +
                "\"details\":[\"ISRZ002 'LMCOPY' terminated - 'LMCOPY' has been terminated due to truncation continue processing.\"]" +
                "\"stack\":[\"ISRZ002 'LMCOPY' terminated - 'LMCOPY' has been terminated due to truncation continue processing.\"]" +
              "}"
            )
            .setResponseCode(500)
        }
      )

      val copyToDatasetRequest = ZosmfCopyToDatasetRequest(
        mockHttpConnection,
        ZosmfCopyToDatasetRequestBody.FromDataset(fromDsName),
        toDsName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)
        as? ZosmfCopyToDatasetResponse
        ?: fail("Should be instance of ${ZosmfCopyToDatasetResponse::class.java.name}")

      assertSoftly {
        copyToDatasetResponse.status.type shouldBe StatusType.ERROR
        copyToDatasetResponse.status.text shouldContain "truncation continue processing"
      }
    }

    should("copyToDataset fail due to truncation error during copy of a PDS member to an existing PDS member (first member LRECL is better than the second's)") {
      val fromDsName = "CFDTESTC"
      val fromMemberName = "TESTMEM1"
      val toDsName = "CTDTESTC"
      val toMemberName = "TESTMEM2"

      zosmfMockResponseDispatcher.injectResolver(
        "copyToDataset_pds_to_pds_fail_trunc",
        { it.requestLine.contains("/zosmf/restfiles/ds/$toDsName($toMemberName)") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":4," +
                "\"rc\":16," +
                "\"reason\":0," +
                "\"message\":\"Copy dataset failed\"," +
                "\"details\":[\"ISRZ002 'LMCOPY' terminated - 'LMCOPY' has been terminated due to truncation continue processing.\"]" +
                "\"stack\":[\"ISRZ002 'LMCOPY' terminated - 'LMCOPY' has been terminated due to truncation continue processing.\"]" +
              "}"
            )
            .setResponseCode(500)
        }
      )

      val copyToDatasetRequest = ZosmfCopyToDatasetRequest(
        mockHttpConnection,
        ZosmfCopyToDatasetRequestBody.FromDataset(fromDsName, memberName = fromMemberName),
        toDsName,
        toMemberName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)
        as? ZosmfCopyToDatasetResponse
        ?: fail("Should be instance of ${ZosmfCopyToDatasetResponse::class.java.name}")

      assertSoftly {
        copyToDatasetResponse.status.type shouldBe StatusType.ERROR
        copyToDatasetResponse.status.text shouldContain "truncation continue processing"
      }
    }

    should("copyToDataset fail due to try to copy the USS folder to a new PDS data set") {
      val fromFile = "/tmp/test/folder"
      val toDsName = "CTDTESTD"

      zosmfMockResponseDispatcher.injectResolver(
        "copyToDataset_uss_folder_to_pds_fail",
        { it.requestLine.contains("/zosmf/restfiles/ds/$toDsName") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":1," +
                "\"rc\":8," +
                "\"reason\":9," +
                "\"message\":\"Error executing command. exit_code=1\"," +
                "\"details\":[\"/bin/cp -T $fromFile //'$toDsName'\",\"cp: FSUMF134 source \\\"$fromFile\\\" is a directory, not allowed for MVS data set target\"]" +
                "\"stack\":[\"/bin/cp -T $fromFile //'$toDsName'\",\"cp: FSUMF134 source \\\"$fromFile\\\" is a directory, not allowed for MVS data set target\"]" +
              "}"
            )
            .setResponseCode(500)
        }
      )

      val copyToDatasetRequest = ZosmfCopyToDatasetRequest(
        mockHttpConnection,
        ZosmfCopyToDatasetRequestBody.FromFile(fromFile),
        toDsName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)
        as? ZosmfCopyToDatasetResponse
        ?: fail("Should be instance of ${ZosmfCopyToDatasetResponse::class.java.name}")

      assertSoftly {
        copyToDatasetResponse.status.type shouldBe StatusType.ERROR
        copyToDatasetResponse.status.text shouldContain "is a directory, not allowed for MVS data set target"
      }
    }

    should("copyToDataset fail due to try to copy the USS folder to a PDS member") {
      val fromFile = "/tmp/test/folder"
      val toDsName = "CTDTESTE"
      val toMemberName = "TESTMEM1"

      zosmfMockResponseDispatcher.injectResolver(
        "copyToDataset_uss_folder_to_pds_mem_fail",
        { it.requestLine.contains("/zosmf/restfiles/ds/$toDsName($toMemberName)") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":1," +
                "\"rc\":4," +
                "\"reason\":19," +
                "\"message\":\"Member not replaced\"," +
                "\"details\":[\"$toMemberName\"]" +
                "\"stack\":[\"$toMemberName\"]" +
              "}"
            )
            .setResponseCode(500)
        }
      )

      val copyToDatasetRequest = ZosmfCopyToDatasetRequest(
        mockHttpConnection,
        ZosmfCopyToDatasetRequestBody.FromFile(fromFile),
        toDsName,
        toMemberName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)
        as? ZosmfCopyToDatasetResponse
        ?: fail("Should be instance of ${ZosmfCopyToDatasetResponse::class.java.name}")

      assertSoftly {
        copyToDatasetResponse.status.type shouldBe StatusType.ERROR
        copyToDatasetResponse.status.text shouldContain "Member not replaced"
      }
    }

    should("copyToDataset fail due to the USS file to copy is not found") {
      val fromFile = "/tmp/test/not_exist"
      val toDsName = "CTDTESTF"

      zosmfMockResponseDispatcher.injectResolver(
        "copyToDataset_uss_to_ps_fail_uss_not_exist",
        { it.requestLine.contains("/zosmf/restfiles/ds/$toDsName") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":6," +
                "\"rc\":8," +
                "\"reason\":98762850," +
                "\"message\":\"File not found.\"," +
                "\"details\":[\"EDC5129I No such file or directory. (errno2=0x05620062)\"]" +
                "\"stack\":[\"EDC5129I No such file or directory. (errno2=0x05620062)\"]" +
              "}"
            )
            .setResponseCode(404)
        }
      )

      val copyToDatasetRequest = ZosmfCopyToDatasetRequest(
        mockHttpConnection,
        ZosmfCopyToDatasetRequestBody.FromFile(fromFile),
        toDsName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)
        as? ZosmfCopyToDatasetResponse
        ?: fail("Should be instance of ${ZosmfCopyToDatasetResponse::class.java.name}")

      assertSoftly {
        copyToDatasetResponse.status.type shouldBe StatusType.ERROR
        copyToDatasetResponse.status.text shouldContain "No such file or directory"
      }
    }

    should("copyToDataset fail due to the PS data set to copy is not found") {
      val fromDsName = "CFDTESTG"
      val toDsName = "CTDTESTG"

      zosmfMockResponseDispatcher.injectResolver(
        "copyToDataset_ps_to_ps_fail_not_exist",
        { it.requestLine.contains("/zosmf/restfiles/ds/$toDsName") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":6," +
                "\"rc\":8," +
                "\"reason\":386400778," +
                "\"message\":\"ISPF LMINIT - data set not found\"," +
                "\"details\":[\"'$fromDsName' was not found in catalog.\"]" +
                "\"stack\":[\"'$fromDsName' was not found in catalog.\"]" +
              "}"
            )
            .setResponseCode(500)
        }
      )

      val copyToDatasetRequest = ZosmfCopyToDatasetRequest(
        mockHttpConnection,
        ZosmfCopyToDatasetRequestBody.FromDataset(fromDsName),
        toDsName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)
        as? ZosmfCopyToDatasetResponse
        ?: fail("Should be instance of ${ZosmfCopyToDatasetResponse::class.java.name}")

      assertSoftly {
        copyToDatasetResponse.status.type shouldBe StatusType.ERROR
        copyToDatasetResponse.status.text shouldContain "data set not found"
      }
    }

    should("copyToDataset fail due to the PDS member to copy is not found") {
      val fromDsName = "CFDTESTH"
      val fromMemberName = "TESTMEM1"
      val toDsName = "CTDTESTH"

      zosmfMockResponseDispatcher.injectResolver(
        "copyToDataset_pds_to_ps_fail_not_exist",
        { it.requestLine.contains("/zosmf/restfiles/ds/$toDsName") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":4," +
                "\"rc\":8," +
                "\"reason\":0," +
                "\"message\":\"Copy dataset failed\"," +
                "\"details\":[\"ISRZ002 Member not found - Member '$fromMemberName' not found in specified input library.\"]" +
                "\"stack\":[\"ISRZ002 Member not found - Member '$fromMemberName' not found in specified input library.\"]" +
              "}"
            )
            .setResponseCode(500)
        }
      )

      val copyToDatasetRequest = ZosmfCopyToDatasetRequest(
        mockHttpConnection,
        ZosmfCopyToDatasetRequestBody.FromDataset(fromDsName, memberName = fromMemberName),
        toDsName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)
        as? ZosmfCopyToDatasetResponse
        ?: fail("Should be instance of ${ZosmfCopyToDatasetResponse::class.java.name}")

      assertSoftly {
        copyToDatasetResponse.status.type shouldBe StatusType.ERROR
        copyToDatasetResponse.status.text shouldContain "not found in specified input library"
      }
    }
  }
})
