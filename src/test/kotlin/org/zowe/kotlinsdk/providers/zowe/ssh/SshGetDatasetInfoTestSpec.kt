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
import org.zowe.kotlinsdk.providers.zowe.ssh.datasets.messaging.SshGetDatasetInfoRequest
import org.zowe.kotlinsdk.providers.zowe.ssh.datasets.messaging.SshGetDatasetInfoResponse
import kotlin.text.contains
import kotlin.text.trim

class SshGetDatasetInfoTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.SSH_NATIVE, DatasetsAPI::class.java)

  context("getDatasetInfo") {
    should("getDatasetInfo return data set's attributes") {
      sshMockResponseDispatcher.injectResolver(
        "ssh:getDatasetInfo_success",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("LISTDS")
          && it.contains("TEST1.TEST2")
        },
        handler = {
          val output = listOf(
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
            "",
          ).joinToString("\n")
          SshMockCommandResponse(output)
        }
      )

      val getDatasetInfoRequest = SshGetDatasetInfoRequest(
        SshConnection(
          MOCK_SERVER_HOST,
          port = MOCK_SSH_SERVER_PORT,
          username = MOCK_USERNAME,
          authMethods = sshAuthMethods
        ),
        "TEST1.TEST2"
      )

      val getDatasetInfoResponse = datasetsApi.getDatasetInfo(getDatasetInfoRequest)

      val status = if (getDatasetInfoResponse is SshGetDatasetInfoResponse) getDatasetInfoResponse.status else null

      assertSoftly {
        (getDatasetInfoResponse is SshGetDatasetInfoResponse) shouldBe true
        status shouldNotBe null
        status?.exitStatus shouldBe 0
        getDatasetInfoResponse.dataset.datasetOrganization shouldBe DatasetItem.DatasetOrganization.PO
        getDatasetInfoResponse.dataset.sizeInTracks shouldBe 70
        getDatasetInfoResponse.dataset.spaceUnits shouldBe DatasetItem.SpaceUnits.BLOCKS
      }
    }

    should("getDatasetInfo return error code when the data set is not found") {
      sshMockResponseDispatcher.injectResolver(
        "ssh:getDatasetInfo_fail",
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

      val getDatasetInfoRequest = SshGetDatasetInfoRequest(
        SshConnection(
          MOCK_SERVER_HOST,
          port = MOCK_SSH_SERVER_PORT,
          username = MOCK_USERNAME,
          authMethods = sshAuthMethods
        ),
        "TEST2"
      )

      val getDatasetInfoResponse = datasetsApi.getDatasetInfo(getDatasetInfoRequest)

      val status = if (getDatasetInfoResponse is SshGetDatasetInfoResponse) getDatasetInfoResponse.status else null

      assertSoftly {
        (getDatasetInfoResponse is SshGetDatasetInfoResponse) shouldBe true
        status shouldNotBe null
        status?.exitStatus shouldBe 8
        status?.error shouldContain "LOCATE ERROR CODE   08"
      }
    }
  }
})
