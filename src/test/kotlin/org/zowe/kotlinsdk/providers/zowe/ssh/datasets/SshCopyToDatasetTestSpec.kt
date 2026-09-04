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
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.provided.ProjectConfig.mockSshConnection
import io.kotest.provided.ProjectConfig.sshMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.core.datasets.data.FromEntity
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.definitions.SshFromDataset
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.definitions.SshFromFile
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshCopyToDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshCopyToDatasetResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse

class SshCopyToDatasetTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, DatasetsAPI::class.java)

  /** Inject a "tsocmd LISTDS" resolver reporting the provided data set as an existing one */
  fun injectExistingDatasetResolver(name: String, dsName: String) {
    sshMockResponseDispatcher.injectResolver(
      name,
      resolver = { it.trim().startsWith("tsocmd") && it.contains("LISTDS") && it.contains(dsName) },
      handler = { _, _ -> SshMockCommandResponse(listOf(dsName.padEnd(44), "").joinToString("\n")) }
    )
  }

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("copyToDataset") {
    should("copyToDataset execute successfully copying a USS text file to a data set") {
      val dsName = "TEST.CPDS1"
      val filePath = "/u/testuser/copy1/file.txt"
      var executedCommand = ""

      sshMockResponseDispatcher.injectResolver(
        "ssh:copyToDataset_from_file_success",
        resolver = { it.trim().startsWith("cp ") && it.contains(dsName) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )
      injectExistingDatasetResolver("ssh:copyToDataset_from_file_success_listDatasets", dsName)

      val copyToDatasetRequest = SshCopyToDatasetRequest(mockSshConnection, SshFromFile(filePath), dsName)
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)

      if (copyToDatasetResponse !is SshCopyToDatasetResponse) {
        fail("Should be instance of ${SshCopyToDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          copyToDatasetResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand.trim() shouldBe "cp -T '$filePath' \"//'$dsName'\""
        }
      }
    }

    should("copyToDataset execute successfully copying a USS binary file to a data set member") {
      val dsName = "TEST.CPDS2"
      val memName = "TESTMEM1"
      val filePath = "/u/testuser/copy2/file.bin"
      var executedCommand = ""

      sshMockResponseDispatcher.injectResolver(
        "ssh:copyToDataset_from_binary_file_success",
        resolver = { it.trim().startsWith("cp ") && it.contains("$dsName($memName)") },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )
      injectExistingDatasetResolver("ssh:copyToDataset_from_binary_file_success_listDatasets", dsName)

      val copyToDatasetRequest = SshCopyToDatasetRequest(
        mockSshConnection,
        SshFromFile(filePath, SshFromFile.FileType.BINARY),
        dsName,
        memName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)

      if (copyToDatasetResponse !is SshCopyToDatasetResponse) {
        fail("Should be instance of ${SshCopyToDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          copyToDatasetResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand.trim() shouldBe "cp -B '$filePath' \"//'$dsName($memName)'\""
        }
      }
    }

    should("copyToDataset execute successfully copying a data set member to another data set member") {
      val fromDsName = "TEST.CPDS3.SRC"
      val fromMemName = "SRCMEM1"
      val toDsName = "TEST.CPDS3.TGT"
      val toMemName = "TGTMEM1"
      var executedCommand = ""

      sshMockResponseDispatcher.injectResolver(
        "ssh:copyToDataset_from_dataset_success",
        resolver = { it.trim().startsWith("cp ") && it.contains("$toDsName($toMemName)") },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )
      injectExistingDatasetResolver("ssh:copyToDataset_from_dataset_success_listDatasets", toDsName)

      val copyToDatasetRequest = SshCopyToDatasetRequest(
        mockSshConnection,
        SshFromDataset(fromDsName, fromMemName),
        toDsName,
        toMemName
      )
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)

      if (copyToDatasetResponse !is SshCopyToDatasetResponse) {
        fail("Should be instance of ${SshCopyToDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          copyToDatasetResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand.trim() shouldBe "cp \"//'$fromDsName($fromMemName)'\" \"//'$toDsName($toMemName)'\""
        }
      }
    }

    should("copyToDataset report a warning when the records are truncated during the copy") {
      val dsName = "TEST.CPDS7"
      val memName = "TESTMEM2"
      val filePath = "/u/testuser/copy7/long_lines.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:copyToDataset_truncation_warning",
        resolver = { it.trim().startsWith("cp ") && it.contains("$dsName($memName)") },
        handler = { _, _ ->
          val stderr = listOf(
            "cp: FSUM6260 write error on file \"//'$dsName($memName)'\": " +
              "EDC5003I Truncation of a record occurred during an I/O operation.",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output = "", error = stderr, exitCode = 1)
        }
      )
      injectExistingDatasetResolver("ssh:copyToDataset_truncation_warning_listDatasets", dsName)

      val copyToDatasetRequest = SshCopyToDatasetRequest(mockSshConnection, SshFromFile(filePath), dsName, memName)
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)

      if (copyToDatasetResponse !is SshCopyToDatasetResponse) {
        fail("Should be instance of ${SshCopyToDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          copyToDatasetResponse.status.type shouldBe StatusType.WARNING
          copyToDatasetResponse.status.text shouldContain "EDC5003I Truncation of a record occurred"
        }
      }
    }

    should("copyToDataset fail because the data set to copy to is not found") {
      val dsName = "TEST.CPDS4"
      val filePath = "/u/testuser/copy4/file.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:copyToDataset_fail_not_found",
        resolver = { it.trim().startsWith("cp ") && it.contains(dsName) },
        handler = { _, _ ->
          throw Exception("Copy should not happen because the data set must not be found")
        }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:copyToDataset_fail_not_found_listDatasets",
        resolver = { it.trim().startsWith("tsocmd") && it.contains("LISTDS") && it.contains(dsName) },
        handler = { _, _ ->
          val output = listOf(
            "IKJ58518I  UNABLE TO COMPLETE PROCESSING FOR ENTRY '$dsName'  +",
            "IKJ58518I LOCATE ERROR CODE   08",
            "",
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 8)
        }
      )

      val copyToDatasetRequest = SshCopyToDatasetRequest(mockSshConnection, SshFromFile(filePath), dsName)
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)

      if (copyToDatasetResponse !is SshCopyToDatasetResponse) {
        fail("Should be instance of ${SshCopyToDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          copyToDatasetResponse.status.type shouldBe StatusType.ERROR
          copyToDatasetResponse.status.text shouldContain "ERROR OCCURRED DURING SEARCH FOR THE DATA SET OR MEMBER"
        }
      }
    }

    should("copyToDataset fail because the data set to copy the member to is not found") {
      val dsName = "TEST.CPDS8"
      val memName = "TESTMEM3"
      val filePath = "/u/testuser/copy8/file.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:copyToDataset_member_fail_not_found",
        resolver = { it.trim().startsWith("cp ") && it.contains(dsName) },
        handler = { _, _ ->
          throw Exception("Copy should not happen because the data set to hold the member must not be found")
        }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:copyToDataset_member_fail_not_found_listDatasets",
        resolver = { it.trim().startsWith("tsocmd") && it.contains("LISTDS") && it.contains(dsName) },
        handler = { command, _ ->
          // The existence of the data set itself is to be checked, not the existence of the member
          assertSoftly { command shouldNotContain memName }
          val output = listOf(
            "IKJ58518I  UNABLE TO COMPLETE PROCESSING FOR ENTRY '$dsName'  +",
            "IKJ58518I LOCATE ERROR CODE   08",
            "",
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 8)
        }
      )

      val copyToDatasetRequest = SshCopyToDatasetRequest(mockSshConnection, SshFromFile(filePath), dsName, memName)
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)

      if (copyToDatasetResponse !is SshCopyToDatasetResponse) {
        fail("Should be instance of ${SshCopyToDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          copyToDatasetResponse.status.type shouldBe StatusType.ERROR
          copyToDatasetResponse.status.text shouldContain "ERROR OCCURRED DURING SEARCH FOR THE DATA SET OR MEMBER"
        }
      }
    }

    should("copyToDataset fail due to some error during the copy operation") {
      val dsName = "TEST.CPDS5"
      val filePath = "/u/testuser/copy5/file.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:copyToDataset_fail_internal_error",
        resolver = { it.trim().startsWith("cp ") && it.contains(dsName) },
        handler = { _, _ ->
          val stderr = listOf(
            "cp: FSUM6258 cannot open file \"//'$dsName'\": EDC5092I An I/O abend was trapped.",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output = "", error = stderr, exitCode = 1)
        }
      )
      injectExistingDatasetResolver("ssh:copyToDataset_fail_internal_error_listDatasets", dsName)

      val copyToDatasetRequest = SshCopyToDatasetRequest(mockSshConnection, SshFromFile(filePath), dsName)
      val copyToDatasetResponse = datasetsApi.copyToDataset(copyToDatasetRequest)

      if (copyToDatasetResponse !is SshCopyToDatasetResponse) {
        fail("Should be instance of ${SshCopyToDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          copyToDatasetResponse.status.type shouldBe StatusType.ERROR
          copyToDatasetResponse.status.text shouldContain "EDC5092I An I/O abend was trapped"
        }
      }
    }

    should("copyToDataset fail to be built with an unknown entity type to copy from") {
      val unknownEntity = object : FromEntity {
        override val entityName = "TEST.CPDS6"
      }

      val exception = shouldThrow<Exception> {
        SshCopyToDatasetRequest(mockSshConnection, unknownEntity, "TEST.CPDS6.TGT")
      }
      assertSoftly { exception.message shouldContain "Unknown entity type to copy from" }
    }
  }
})
