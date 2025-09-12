/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.providers.zowe.ssh

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.MOCK_SERVER_HOST
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.MOCK_SSH_SERVER_PORT
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.MOCK_USERNAME
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.sshAuthMethods
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.sshMockResponseDispatcher
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.core.datasets.data.DatasetItem
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.SshConnection
import org.zowe.kotlinsdk.providers.zowe.ssh.datasets.messaging.SshListDatasetsRequest
import org.zowe.kotlinsdk.providers.zowe.ssh.datasets.messaging.SshListDatasetsResponse
import kotlin.text.contains
import kotlin.text.trim

class SshListDatasetsTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.SSH_NATIVE, DatasetsAPI::class.java)

  context("listDatasets") {
    should("listDatasets return the correct list of datasets") {
      sshMockResponseDispatcher.injectResolver(
        "ssh:listDatasets_success",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("LISTDS")
          && it.contains("TEST1")
        },
        handler = {
          val output = listOf(
            "TEST1.TEST1".padEnd(44),
            "--RECFM-LRECL-BLKSIZE-DSORG",
            "  VB    12288 12292   PS".padEnd(114),
            "--VOLUMES--",
            "  VOLUM1",
            "--FORMAT 1 DSCB--",
            "F1 E5D6D3E4D4F1 0001 7C00C2 000000 01 00 00 C9C2D4D6E2E5E2F24040404040",
            "7D001180000000 4000 50 00 03E8 0064 00 0000 82 80000000 000001 E2FA 0000",
            "010000A9000600A90006 00000000000000000000 00000000000000000000 0000000000",
            "TEST1.TEST2".padEnd(44),
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
            "TEST1.TEST2.F".padEnd(44),
            "--RECFM-LRECL-BLKSIZE-DSORG",
            "  F     80    27920   PO".padEnd(114),
            "--VOLUMES------",
            "  VOLUM1,VOLUM2",
            "--FORMAT 1 DSCB--",
            "F1 E5D6D3E4D4F1 0001 7C00C2 000000 04 00 00 C9C2D4D6E2E5E2F24040404040",
            "7D002600801810 0200 80 00 1810 0050 00 0000 80 C000004F 003D03 D706 0000",
            "01001C1200001C140009 01011C14000A1C150004 01021C1500051C15000E 0000000405",
            "TEST1.TEST2.V".padEnd(44),
            "--RECFM-LRECL-BLKSIZE-DSORG",
            "  V     80    27920   PS".padEnd(114),
            "--VOLUMES--",
            "  VOLUM1",
            "--FORMAT 1 DSCB--",
            "F1 E5D6D3E4D4F1 0001 7C00C2 000000 04 00 00 C9C2D4D6E2E5E2F24040404040",
            "7D002600801810 0200 40 00 1810 0050 00 0000 80 5000004F 003D03 D706 0000",
            "01001C1200001C140009 01011C14000A1C150004 01021C1500051C15000E 0000000405",
            "TEST1.TEST2.VA".padEnd(44),
            "--RECFM-LRECL-BLKSIZE-DSORG",
            "  VA    80    84      PO".padEnd(114),
            "--VOLUMES--",
            "  VOLUM1",
            "--FORMAT 1 DSCB--",
            "F1 E5D6D3E4D4F1 0001 7C00C2 000000 01 00 00 C9C2D4D6E2E5E2F24040404040",
            "7D00D088000000 0200 44 00 0054 0050 00 0000 82 80000000 000000 0000 0000",
            "010012D3000E12D3000E 00000000000000000000 00000000000000000000 0000000000",
            "TEST1.TEST3".padEnd(44),
            "--RECFM-LRECL-BLKSIZE-DSORG",
            "  U     **    6144    PO".padEnd(114),
            "--VOLUMES--",
            "  VOLUM1",
            "--FORMAT 1 DSCB--",
            "F1 E5D6D3E4D4F1 0001 7C00C2 000000 04 00 00 C9C2D4D6E2E5E2F24040404040",
            "7D002600801810 0200 C0 00 1810 0050 00 0000 80 5000004F 003D03 D706 0000",
            "01001C1200001C140009 01011C14000A1C150004 01021C1500051C15000E 0000000405",
            "TEST1.VSAM1".padEnd(44),
            "--LRECL--DSORG",
            "  **     VSAM".padEnd(114),
            "--VOLUMES-BLKSIZE",
            "            **".padEnd(114),
            "TEST1.VSAM.TEST2".padEnd(44),
            "--LRECL--DSORG-",
            "  **     VSAM".padEnd(114),
            "--VOLUMES-BLKSIZE",
            "  VOLUM1     **".padEnd(114),
            "TEST1.TEST4".padEnd(44),
            "IKJ58507I DATA SET ${"TEST1.TEST4".padEnd(44)} NOT ALLOCATED, REQUIRED VOLUME NOT MOUNTED+".padEnd(114),
            "IKJ58507I VOLUME NOT ON SYSTEM AND CANNOT BE ACCESSED".padEnd(114),
            "TEST1.GDG1".padEnd(44),
            "--LRECL--DSORG-",
            "  **     GDG".padEnd(114),
            "--VOLUMES-BLKSIZE",
            "            **".padEnd(114),
            "",
          ).joinToString("\n")
          SshMockCommandResponse(output)
        }
      )

      val listDatasetsRequest = SshListDatasetsRequest(
        SshConnection(
          MOCK_SERVER_HOST,
          port = MOCK_SSH_SERVER_PORT,
          username = MOCK_USERNAME,
          authMethods = sshAuthMethods
        ),
        "TEST1.*"
      )

      val listDatasetsResponse = datasetsApi.listDatasets(listDatasetsRequest)

      val status = if (listDatasetsResponse is SshListDatasetsResponse) listDatasetsResponse.status else null

      assertSoftly {
        (listDatasetsResponse is SshListDatasetsResponse) shouldBe true
        status shouldNotBe null
        status?.exitStatus shouldBe 0
        listDatasetsResponse.dsItems.size shouldBe 9
        listDatasetsResponse.dsItems[0].datasetName shouldBe "TEST1.TEST1"
        listDatasetsResponse.dsItems[0].sizeInTracks shouldBe 1
        listDatasetsResponse.dsItems[0].spaceUnits shouldBe DatasetItem.SpaceUnits.TRACKS
        listDatasetsResponse.dsItems[1].datasetOrganization shouldBe DatasetItem.DatasetOrganization.PO
        listDatasetsResponse.dsItems[1].sizeInTracks shouldBe 70
        listDatasetsResponse.dsItems[1].spaceUnits shouldBe DatasetItem.SpaceUnits.BLOCKS
        listDatasetsResponse.dsItems[2].blockSize shouldBe 27920
        listDatasetsResponse.dsItems[2].volumeSerial shouldBe "VOLUM1,VOLUM2"
        listDatasetsResponse.dsItems[2].sizeInTracks shouldBe 60
        listDatasetsResponse.dsItems[2].spaceUnits shouldBe DatasetItem.SpaceUnits.CYLINDERS
        listDatasetsResponse.dsItems[3].recordLength shouldBe 80
        listDatasetsResponse.dsItems[4].recordFormat shouldBe DatasetItem.RecordFormat.V
        listDatasetsResponse.dsItems[5].recordFormat shouldBe DatasetItem.RecordFormat.U
        listDatasetsResponse.dsItems[6].volumeSerial shouldBe null
        listDatasetsResponse.dsItems[7].recordFormat shouldBe DatasetItem.RecordFormat.VSAM
        listDatasetsResponse.dsItems[8].datasetName shouldBe "TEST1.GDG1"
      }
    }

    should("listDatasets fail with SSH RC=8 as there is no such entity") {
      sshMockResponseDispatcher.injectResolver(
        "ssh:listDatasets_not_found",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("LISTDS")
          && it.contains("TEST2")
        },
        handler = {
          val output = listOf(
            "IKJ58518I  UNABLE TO COMPLETE PROCESSING FOR ENTRY 'TEST2'  +",
            "IKJ58518I LOCATE ERROR CODE   08",
            "",
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 8)
        }
      )

      val listDatasetsRequest = SshListDatasetsRequest(
        SshConnection(
          MOCK_SERVER_HOST,
          port = MOCK_SSH_SERVER_PORT,
          username = MOCK_USERNAME,
          authMethods = sshAuthMethods
        ),
        "TEST2.*"
      )

      val listDatasetsResponse = datasetsApi.listDatasets(listDatasetsRequest)

      val status = if (listDatasetsResponse is SshListDatasetsResponse) listDatasetsResponse.status else null

      assertSoftly {
        (listDatasetsResponse is SshListDatasetsResponse) shouldBe true
        status shouldNotBe null
        status?.exitStatus shouldBe 8
        status?.error shouldContain "LOCATE ERROR CODE   08"
      }
    }
  }
})
