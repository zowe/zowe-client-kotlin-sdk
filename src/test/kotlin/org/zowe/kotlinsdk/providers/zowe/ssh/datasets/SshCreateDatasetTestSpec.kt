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
import io.kotest.matchers.string.shouldContain
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.sshMockResponseDispatcher
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.mockSshConnection
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.definitions.SshDatasetItem
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshCreateDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshCreateDatasetResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse
import kotlin.text.contains
import kotlin.text.trim

class SshCreateDatasetTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.SSH_NATIVE, DatasetsAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("createDataset") {
    should("createDataset execute successfully creating a new PS dataset") {
      val dsName = "TEST.ALLOC1"

      sshMockResponseDispatcher.injectResolver(
        "ssh:createDataset_ps_success",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("ALLOC")
          && it.contains(dsName)
        },
        handler = { _, _ ->
          SshMockCommandResponse("")
        }
      )

      val createDatasetRequest = SshCreateDatasetRequest(
        mockSshConnection,
        dsName,
        datasetOrganization = SshDatasetItem.SshDatasetOrganization.PS,
        primaryAllocation = 2,
        secondaryAllocation = 0,
        allocationUnit = SshDatasetItem.SshSpaceUnits.TRACKS,
        recordLength = 80,
        blockSize = 8000,
        recordFormat = SshDatasetItem.SshRecordFormat(
          SshDatasetItem.SshRecordFormat.SshRecordFormatLength.F,
          SshDatasetItem.SshRecordFormat.SshRecordFormatBlocking.B
        )
      )

      val createDatasetResponse = datasetsApi.createDataset(createDatasetRequest)

      if (createDatasetResponse !is SshCreateDatasetResponse) {
        fail("Should be instance of ${SshCreateDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          createDatasetResponse.status.type shouldBe StatusType.SUCCESS
        }
      }
    }

    should("createDataset execute successfully creating a new PDS dataset") {
      val dsName = "TEST.ALLOC2"

      sshMockResponseDispatcher.injectResolver(
        "ssh:createDataset_pds_success",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("ALLOC")
          && it.contains(dsName)
        },
        handler = { _, _ ->
          SshMockCommandResponse("")
        }
      )

      val createDatasetRequest = SshCreateDatasetRequest(
        mockSshConnection,
        dsName,
        datasetOrganization = SshDatasetItem.SshDatasetOrganization.PO,
        primaryAllocation = 2,
        secondaryAllocation = 0,
        allocationUnit = SshDatasetItem.SshSpaceUnits.TRACKS,
        recordLength = 80,
        blockSize = 8000,
        recordFormat = SshDatasetItem.SshRecordFormat(
          SshDatasetItem.SshRecordFormat.SshRecordFormatLength.F,
          SshDatasetItem.SshRecordFormat.SshRecordFormatBlocking.B
        )
      )

      val createDatasetResponse = datasetsApi.createDataset(createDatasetRequest)

      if (createDatasetResponse !is SshCreateDatasetResponse) {
        fail("Should be instance of ${SshCreateDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          createDatasetResponse.status.type shouldBe StatusType.SUCCESS
        }
      }
    }

    should("createDataset fail cause there is a duplicate dataset name") {
      val dsName = "TEST.ALLOC3"

      sshMockResponseDispatcher.injectResolver(
        "ssh:createDataset_ps_fail_dupl",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("ALLOC")
          && it.contains(dsName)
        },
        handler = { _, _ ->
          val output = listOf(
            "IKJ56893I DATA SET $dsName NOT ALLOCATED+",
            "IGD17101I DATA SET $dsName",
            "NOT DEFINED BECAUSE DUPLICATE NAME EXISTS IN CATALOG",
            "RETURN CODE IS 8 REASON CODE IS 38 IGG0CLEH",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 12)
        }
      )

      val createDatasetRequest = SshCreateDatasetRequest(
        mockSshConnection,
        dsName,
        datasetOrganization = SshDatasetItem.SshDatasetOrganization.PS,
        primaryAllocation = 2,
        secondaryAllocation = 0,
        allocationUnit = SshDatasetItem.SshSpaceUnits.TRACKS,
        recordLength = 80,
        blockSize = 8000,
        recordFormat = SshDatasetItem.SshRecordFormat(
          SshDatasetItem.SshRecordFormat.SshRecordFormatLength.F,
          SshDatasetItem.SshRecordFormat.SshRecordFormatBlocking.B
        )
      )

      val createDatasetResponse = datasetsApi.createDataset(createDatasetRequest)

      if (createDatasetResponse !is SshCreateDatasetResponse) {
        fail("Should be instance of ${SshCreateDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          createDatasetResponse.status.type shouldBe StatusType.ERROR
          createDatasetResponse.status.text shouldContain "NOT ALLOCATED"
          createDatasetResponse.status.text shouldContain "DUPLICATE NAME EXISTS"
        }
      }
    }

    should("createDataset fail cause there is a duplicate dataset name, but the error is uncertain") {
      val dsName = "TEST.ALLOC4"

      sshMockResponseDispatcher.injectResolver(
        "ssh:createDataset_ps_fail_dupl_uncertain",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("ALLOC")
          && it.contains(dsName)
        },
        handler = { _, _ ->
          val output = listOf(
            "IKJ56229I DATA SET $dsName NOT ALLOCATED, CATALOG ERROR+",
            "IKJ56229I DATA SET NAME CONFLICTS WITH EXISTING DATA SET NAME OR USER IS NOT AUTHORIZED TO PERFORM THE OPERATION.",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 12)
        }
      )

      val createDatasetRequest = SshCreateDatasetRequest(
        mockSshConnection,
        dsName,
        datasetOrganization = SshDatasetItem.SshDatasetOrganization.PS,
        primaryAllocation = 2,
        secondaryAllocation = 0,
        allocationUnit = SshDatasetItem.SshSpaceUnits.TRACKS,
        recordLength = 80,
        blockSize = 8000,
        recordFormat = SshDatasetItem.SshRecordFormat(
          SshDatasetItem.SshRecordFormat.SshRecordFormatLength.F,
          SshDatasetItem.SshRecordFormat.SshRecordFormatBlocking.B
        )
      )

      val createDatasetResponse = datasetsApi.createDataset(createDatasetRequest)

      if (createDatasetResponse !is SshCreateDatasetResponse) {
        fail("Should be instance of ${SshCreateDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          createDatasetResponse.status.type shouldBe StatusType.ERROR
          createDatasetResponse.status.text shouldContain "NOT ALLOCATED"
          createDatasetResponse.status.text shouldContain "DATA SET NAME CONFLICTS"
          createDatasetResponse.status.text shouldContain "NOT AUTHORIZED"
        }
      }
    }
  }
})
