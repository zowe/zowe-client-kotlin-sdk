/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.ssh.datasets

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import io.kotest.provided.ProjectConfig.sshMockResponseDispatcher
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.core.datasets.data.DatasetItem
import io.kotest.provided.ProjectConfig.mockSshConnection
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshGetDatasetInfoRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshGetDatasetInfoResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse
import kotlin.text.contains
import kotlin.text.trim

class SshGetDatasetInfoTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, DatasetsAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("getDatasetInfo") {
    should("getDatasetInfo return data set's attributes") {
      sshMockResponseDispatcher.injectResolver(
        "ssh:getDatasetInfo_success",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("LISTDS")
          && it.contains("TEST.GETDS1.TEST1")
        },
        handler = { _, _ ->
          val output = listOf(
            "TEST.GETDS1.TEST1".padEnd(44),
            "--RECFM-LRECL-BLKSIZE-DSORG",
            "  FB    80    6160    PO".padEnd(114),
            "--VOLUMES--",
            "  VOLUM1",
            "--FORMAT 1 DSCB--",
            "F1 E5D6D3E4D4F1 0001 7C00C2 000000 04 00 00 C9C2D4D6E2E5E2F24040404040",
            "7D002600801810 0200 90 00 1810 0050 00 0000 80 5000004F 003D03 D706 0000",
            "01001C1200001C140009 01011C14000A1C150004 01021C1500051C15000E 0000000405",
            "--FORMAT 3 DSCB--",
            "03030303 01031C1600001C160009 00000000000000000000 00000000000000000000",
            "00000000000000000000 F3 00000000000000000000 00000000000000000000",
            "00000000000000000000 00000000000000000000 00000000000000000000",
            "00000000000000000000 00000000000000000000 00000000000000000000",
            "00000000000000000000 0000000000",
            "",
          ).joinToString("\n")
          SshMockCommandResponse(output)
        }
      )

      val getDatasetInfoRequest = SshGetDatasetInfoRequest(mockSshConnection, "TEST.GETDS1.TEST1")

      val getDatasetInfoResponse = datasetsApi.getDatasetInfo(getDatasetInfoRequest)

      val status = if (getDatasetInfoResponse is SshGetDatasetInfoResponse) getDatasetInfoResponse.status else null

      assertSoftly {
        (getDatasetInfoResponse is SshGetDatasetInfoResponse) shouldBe true
        status shouldNotBe null
        status?.type shouldBe StatusType.SUCCESS
        getDatasetInfoResponse.dataset.datasetOrganization shouldBe DatasetItem.DatasetOrganization.PO
        getDatasetInfoResponse.dataset.sizeInTracks shouldBe 70
        getDatasetInfoResponse.dataset.spaceUnits shouldBe DatasetItem.SpaceUnits.BLOCKS
      }
    }

    should("getDatasetInfo return error code when the data set is not found") {
      sshMockResponseDispatcher.injectResolver(
        "ssh:getDatasetInfo_fail_no_ds",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("LISTDS")
          && it.contains("TEST.GETDS2")
        },
        handler = { _, _ ->
          val output = listOf(
            "IKJ58518I  UNABLE TO COMPLETE PROCESSING FOR ENTRY 'TEST.GETDS2'  +",
            "IKJ58518I LOCATE ERROR CODE   08",
            "",
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 1)
        }
      )

      val getDatasetInfoRequest = SshGetDatasetInfoRequest(mockSshConnection, "TEST.GETDS2")
      val getDatasetInfoResponse = datasetsApi.getDatasetInfo(getDatasetInfoRequest)

      if (getDatasetInfoResponse !is SshGetDatasetInfoResponse) {
        fail("Should be instance of ${SshGetDatasetInfoResponse::class.java.name}")
      } else {
        assertSoftly {
          getDatasetInfoResponse.status.type shouldBe StatusType.ERROR
          getDatasetInfoResponse.status.text shouldContain "RC: 1"
          getDatasetInfoResponse.status.text shouldContain "UNABLE TO COMPLETE PROCESSING"
        }
      }
    }

    should("getDatasetInfo return error code when the data set, returned by listDatasets request, is not correct") {
      sshMockResponseDispatcher.injectResolver(
        "ssh:getDatasetInfo_fail_incorrect_ds",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("LISTDS")
          && it.contains("TEST.GETDS3")
        },
        handler = { _, _ ->
          val output = listOf(
            "TEST.GETDS1.TEST1".padEnd(44),
            "--RECFM-LRECL-BLKSIZE-DSORG",
            "  FB    80    6160    PO".padEnd(114),
            "--VOLUMES--",
            "  VOLUM1",
            "--FORMAT 1 DSCB--",
            "F1 E5D6D3E4D4F1 0001 7C00C2 000000 04 00 00 C9C2D4D6E2E5E2F24040404040",
            "7D002600801810 0200 90 00 1810 0050 00 0000 80 5000004F 003D03 D706 0000",
            "01001C1200001C140009 01011C14000A1C150004 01021C1500051C15000E 0000000405",
            "--FORMAT 3 DSCB--",
            "03030303 01031C1600001C160009 00000000000000000000 00000000000000000000",
            "00000000000000000000 F3 00000000000000000000 00000000000000000000",
            "00000000000000000000 00000000000000000000 00000000000000000000",
            "00000000000000000000 00000000000000000000 00000000000000000000",
            "00000000000000000000 0000000000",
            "",
          ).joinToString("\n")
          SshMockCommandResponse(output)
        }
      )

      val getDatasetInfoRequest = SshGetDatasetInfoRequest(mockSshConnection, "TEST.GETDS3")
      val getDatasetInfoResponse = datasetsApi.getDatasetInfo(getDatasetInfoRequest)

      if (getDatasetInfoResponse !is SshGetDatasetInfoResponse) {
        fail("Should be instance of ${SshGetDatasetInfoResponse::class.java.name}")
      } else {
        assertSoftly {
          getDatasetInfoResponse.status.type shouldBe StatusType.ERROR
          getDatasetInfoResponse.status.text shouldContain "RC: 8"
          getDatasetInfoResponse.status.text shouldContain "IS NOT FOUND"
        }
      }
    }
  }
})
