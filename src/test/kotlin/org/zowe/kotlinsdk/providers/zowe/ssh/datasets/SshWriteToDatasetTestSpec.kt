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
import io.kotest.provided.ProjectConfig.sshMockResponseDispatcher
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.core.datasets.api.messaging.WriteToDatasetRequest
import io.kotest.provided.ProjectConfig.mockSshConnection
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshWriteToDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshWriteToDatasetResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse
import kotlin.text.contains
import kotlin.text.trim

class SshWriteToDatasetTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, DatasetsAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("writeToDataset") {
    should("writeToDataset execute successfully writing text data to a data set") {
      val dsName = "TEST.WTDS1"
      val testContent = "Hello World!".toByteArray()

      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToDataset_success",
        resolver = {
          it.trim().startsWith("cp -T '/dev/fd0'")
          && it.contains(dsName)
        },
        handler = { _, inputStream ->
          val inputContent = inputStream.readBytes()
          assertSoftly { inputContent shouldBe testContent }
          SshMockCommandResponse("")
        }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToDataset_success_listDatasets_$dsName",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("LISTDS")
          && it.contains(dsName)
        },
        handler = { _, _ ->
          val output = listOf(dsName.padEnd(44), "").joinToString("\n")
          SshMockCommandResponse(output)
        }
      )

      val writeToDatasetRequest = SshWriteToDatasetRequest(mockSshConnection, dsName, testContent)
      val writeToDatasetResponse = datasetsApi.writeToDataset(writeToDatasetRequest)

      if (writeToDatasetResponse !is SshWriteToDatasetResponse) {
        fail("Should be instance of ${SshWriteToDatasetResponse::class.java.name}")
      } else {
        assertSoftly { writeToDatasetResponse.status.type shouldBe StatusType.SUCCESS }
      }
    }

    should("writeToDataset execute successfully writing binary data to a data set") {
      val dsName = "TEST.WTDS2"
      val testContent = "Hello World".toByteArray() +
        byteArrayOf(0x00.toByte(), 0xFF.toByte(), 0x01.toByte(), 0x7F.toByte())

      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToDataset_binary_success",
        resolver = {
          it.trim().startsWith("cp -B '/dev/fd0'")
          && it.contains(dsName)
        },
        handler = { _, inputStream ->
          val inputContent = inputStream.readBytes()
          assertSoftly { inputContent shouldBe testContent }
          SshMockCommandResponse("")
        }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToDataset_binary_success_listDatasets_$dsName",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("LISTDS")
          && it.contains(dsName)
        },
        handler = { _, _ ->
          val output = listOf(dsName.padEnd(44), "").joinToString("\n")
          SshMockCommandResponse(output)
        }
      )

      val writeToDatasetRequest = SshWriteToDatasetRequest(
        mockSshConnection,
        dsName,
        testContent,
        contentType = WriteToDatasetRequest.ContentType.BINARY
      )
      val writeToDatasetResponse = datasetsApi.writeToDataset(writeToDatasetRequest)

      if (writeToDatasetResponse !is SshWriteToDatasetResponse) {
        fail("Should be instance of ${SshWriteToDatasetResponse::class.java.name}")
      } else {
        assertSoftly { writeToDatasetResponse.status.type shouldBe StatusType.SUCCESS }
      }
    }

    should("writeToDataset execute successfully creating or rewriting a data set member") {
      val dsName = "TEST.WTDS3"
      val memName = "TESTMEM1"
      val testContent = "Hello World!".toByteArray()

      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToDataset_success",
        resolver = {
          it.trim().startsWith("cp -T '/dev/fd0'")
          && it.contains("$dsName($memName)")
        },
        handler = { _, inputStream ->
          val inputContent = inputStream.readBytes()
          assertSoftly { inputContent shouldBe testContent }
          SshMockCommandResponse("")
        }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToDataset_new_mem_success_listDatasets_$dsName",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("LISTDS")
          && it.contains(dsName)
        },
        handler = { _, _ -> throw Exception("LISTDS must not be called on members") }
      )

      val writeToDatasetRequest = SshWriteToDatasetRequest(mockSshConnection, "$dsName($memName)", testContent)
      val writeToDatasetResponse = datasetsApi.writeToDataset(writeToDatasetRequest)

      if (writeToDatasetResponse !is SshWriteToDatasetResponse) {
        fail("Should be instance of ${SshWriteToDatasetResponse::class.java.name}")
      } else {
        assertSoftly { writeToDatasetResponse.status.type shouldBe StatusType.SUCCESS }
      }
    }

    should("writeToDataset fail because the data set is not found") {
      val dsName = "TEST.WTDS4"
      val testContent = "Hello World!".toByteArray()

      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToDataset_fail_not_found",
        resolver = {
          it.trim().startsWith("cp -T '/dev/fd0'")
          && it.contains(dsName)
        },
        handler = { _, _ ->
          throw Exception("Write should not happen because the data set must not be found")
        }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToDataset_fail_not_found_listDatasets_$dsName",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("LISTDS")
          && it.contains(dsName)
        },
        handler = { _, _ ->
          val output = listOf(
            "IKJ58518I  UNABLE TO COMPLETE PROCESSING FOR ENTRY '$dsName'  +",
            "IKJ58518I LOCATE ERROR CODE   08",
            "",
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 8)
        }
      )

      val writeToDatasetRequest = SshWriteToDatasetRequest(mockSshConnection, dsName, testContent)
      val writeToDatasetResponse = datasetsApi.writeToDataset(writeToDatasetRequest)

      if (writeToDatasetResponse !is SshWriteToDatasetResponse) {
        fail("Should be instance of ${SshWriteToDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          writeToDatasetResponse.status.type shouldBe StatusType.ERROR
          writeToDatasetResponse.status.text shouldContain "ERROR OCCURRED DURING SEARCH FOR THE DATA SET OR MEMBER"
        }
      }
    }

    should("writeToDataset fail due to some error during write operation") {
      val dsName = "TEST.WTDS5"
      val testContent = "Hello World!".toByteArray()

      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToDataset_fail_internal_error",
        resolver = {
          it.trim().startsWith("cp -T '/dev/fd0'")
          && it.contains(dsName)
        },
        handler = { _, _ ->
          val stderr = listOf(
            "cp: FSUM6258 cannot open file \"//'$dsName'\": EDC5092I An I/O abend was trapped.",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output = "", error = stderr, exitCode = 1)
        }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToDataset_fail_internal_error_listDatasets_$dsName",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("LISTDS")
          && it.contains(dsName)
        },
        handler = { _, _ ->
          val output = listOf(dsName.padEnd(44), "").joinToString("\n")
          SshMockCommandResponse(output)
        }
      )

      val writeToDatasetRequest = SshWriteToDatasetRequest(mockSshConnection, dsName, testContent)
      val writeToDatasetResponse = datasetsApi.writeToDataset(writeToDatasetRequest)

      if (writeToDatasetResponse !is SshWriteToDatasetResponse) {
        fail("Should be instance of ${SshWriteToDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          writeToDatasetResponse.status.type shouldBe StatusType.ERROR
          writeToDatasetResponse.status.text shouldContain "EDC5092I An I/O abend was trapped"
        }
      }
    }
  }
})
