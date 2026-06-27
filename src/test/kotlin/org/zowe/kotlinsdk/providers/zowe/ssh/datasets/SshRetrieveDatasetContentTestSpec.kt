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
import io.kotest.matchers.string.shouldNotContain
import org.zowe.kotlinsdk.core.DataType
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import io.kotest.provided.ProjectConfig.sshMockResponseDispatcher
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import io.kotest.provided.ProjectConfig.mockSshConnection
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshRetrieveDatasetContentRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshRetrieveDatasetContentResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse
import kotlin.text.contains
import kotlin.text.trim

class SshRetrieveDatasetContentTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, DatasetsAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("retrieveDatasetContent") {
    should("retrieveDatasetContent execute successfully retrieving a PS data set content (Fixed record length, LRECL=80)") {
      val entityName = "TEST.RTRIV1"

      sshMockResponseDispatcher.injectResolver(
        "ssh:retrieveDatasetContent_ps_success_f_80",
        resolver = {
          it.trim().startsWith("cp ")
          && it.contains(entityName)
          && it.contains("/dev/fd1")
        },
        handler = { _, _ ->
          val output = listOf(
            "some test data here                                                     00000100",
            "and here                                                                00000200",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output)
        }
      )

      val retrieveDatasetContentRequest = SshRetrieveDatasetContentRequest(
        mockSshConnection,
        entityName,
        DataType.TEXT
      )
      val retrieveDatasetContentResponse = datasetsApi.retrieveDatasetContent(retrieveDatasetContentRequest)

      if (retrieveDatasetContentResponse !is SshRetrieveDatasetContentResponse) {
        fail("Should be instance of ${SshRetrieveDatasetContentResponse::class.java.name}")
      } else {
        assertSoftly {
          retrieveDatasetContentResponse.status.type shouldBe StatusType.SUCCESS
          retrieveDatasetContentResponse.fetchedText shouldContain "some test data"
        }
      }
    }

    should("retrieveDatasetContent execute successfully retrieving a PDS data set member content (Variable record length, LRECL=100)") {
      val entityName = "TEST.RTRIV2(TESTMEM1)"

      sshMockResponseDispatcher.injectResolver(
        "ssh:retrieveDatasetContent_pds_mem_success_v_100",
        resolver = {
          it.trim().startsWith("cp ")
          && it.contains(entityName)
          && it.contains("/dev/fd1")
        },
        handler = { _, _ ->
          val output = listOf(
            "some test data here",
            "and here in member",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output)
        }
      )

      val retrieveDatasetContentRequest = SshRetrieveDatasetContentRequest(
        mockSshConnection,
        entityName,
        DataType.TEXT
      )
      val retrieveDatasetContentResponse = datasetsApi.retrieveDatasetContent(retrieveDatasetContentRequest)

      if (retrieveDatasetContentResponse !is SshRetrieveDatasetContentResponse) {
        fail("Should be instance of ${SshRetrieveDatasetContentResponse::class.java.name}")
      } else {
        assertSoftly {
          retrieveDatasetContentResponse.status.type shouldBe StatusType.SUCCESS
          retrieveDatasetContentResponse.fetchedText shouldContain "some test data"
          retrieveDatasetContentResponse.fetchedText shouldNotContain "     "
        }
      }
    }

    should("retrieveDatasetContent execute successfully retrieving an empty PS data set") {
      val entityName = "TEST.RTRIV3"

      sshMockResponseDispatcher.injectResolver(
        "ssh:retrieveDatasetContent_empty_ps_success",
        resolver = {
          it.trim().startsWith("cp ")
          && it.contains(entityName)
          && it.contains("/dev/fd1")
        },
        handler = { _, _ ->
          val output = listOf(
            "",
          ).joinToString("\n")
          SshMockCommandResponse(output)
        }
      )

      val retrieveDatasetContentRequest = SshRetrieveDatasetContentRequest(
        mockSshConnection,
        entityName,
        DataType.TEXT
      )
      val retrieveDatasetContentResponse = datasetsApi.retrieveDatasetContent(retrieveDatasetContentRequest)

      if (retrieveDatasetContentResponse !is SshRetrieveDatasetContentResponse) {
        fail("Should be instance of ${SshRetrieveDatasetContentResponse::class.java.name}")
      } else {
        assertSoftly {
          retrieveDatasetContentResponse.status.type shouldBe StatusType.SUCCESS
          retrieveDatasetContentResponse.fetchedText shouldNotBe null
          retrieveDatasetContentResponse.fetchedText shouldBe ""
        }
      }
    }

    should("retrieveDatasetContent fail because there is no entity found") {
      val entityName = "TEST.RTRIV4"

      sshMockResponseDispatcher.injectResolver(
        "ssh:retrieveDatasetContent_fail_entity_not_found",
        resolver = {
          it.trim().startsWith("cp ")
          && it.contains(entityName)
          && it.contains("/dev/fd1")
        },
        handler = { _, _ ->
          val output = listOf(
            "IKJ56228I DATA SET ULADZ.NOTEST.SSH80.PDS NOT IN CATALOG OR CATALOG CAN NOT BE ACCESSED",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 12)
        }
      )

      val retrieveDatasetContentRequest = SshRetrieveDatasetContentRequest(
        mockSshConnection,
        entityName,
        DataType.TEXT
      )
      val retrieveDatasetContentResponse = datasetsApi.retrieveDatasetContent(retrieveDatasetContentRequest)

      if (retrieveDatasetContentResponse !is SshRetrieveDatasetContentResponse) {
        fail("Should be instance of ${SshRetrieveDatasetContentResponse::class.java.name}")
      } else {
        assertSoftly {
          retrieveDatasetContentResponse.status.type shouldBe StatusType.ERROR
          retrieveDatasetContentResponse.status.text shouldContain "RC: 12"
          retrieveDatasetContentResponse.status.text shouldContain "NOT IN CATALOG"
        }
      }
    }

    should("retrieveDatasetContent fail due to some other error") {
      val entityName = "TEST.RTRIV5"

      sshMockResponseDispatcher.injectResolver(
        "ssh:retrieveDatasetContent_fail_generic",
        resolver = {
          it.trim().startsWith("cp ")
          && it.contains(entityName)
          && it.contains("/dev/fd1")
        },
        handler = { _, _ ->
          val output = listOf(
            "IKJ1234I Some generic error occurred, needs to be processed",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 12)
        }
      )

      val retrieveDatasetContentRequest = SshRetrieveDatasetContentRequest(
        mockSshConnection,
        entityName,
        DataType.TEXT
      )
      val retrieveDatasetContentResponse = datasetsApi.retrieveDatasetContent(retrieveDatasetContentRequest)

      if (retrieveDatasetContentResponse !is SshRetrieveDatasetContentResponse) {
        fail("Should be instance of ${SshRetrieveDatasetContentResponse::class.java.name}")
      } else {
        assertSoftly {
          retrieveDatasetContentResponse.status.type shouldBe StatusType.ERROR
          retrieveDatasetContentResponse.status.text shouldContain "RC: 12"
          retrieveDatasetContentResponse.status.text shouldContain "generic error"
        }
      }
    }

    should("retrieveDatasetContent get a PDS member content in binary format") {
      val entityName = "TEST.RTRIV6(TEST1)"

      sshMockResponseDispatcher.injectResolver(
        "ssh:retrieveDatasetContent_success_pds_mem_bin",
        resolver = {
          it.trim().startsWith("cp ")
          && it.contains(entityName)
          && it.contains("/dev/fd1")
        },
        handler = { _, _ ->
          val buffer = mutableListOf<Byte>()

          buffer.addAll("Hello World".toByteArray().toList())
          buffer.add(0x00.toByte()) // null byte
          buffer.add(0xFF.toByte()) // 255
          buffer.add(0x01.toByte()) // 1
          buffer.add(0x7F.toByte()) // 127

          val output = buffer.toByteArray()
          SshMockCommandResponse(String(output))
        }
      )

      val retrieveDatasetContentRequest = SshRetrieveDatasetContentRequest(
        mockSshConnection,
        entityName,
        DataType.BINARY
      )
      val retrieveDatasetContentResponse = datasetsApi.retrieveDatasetContent(retrieveDatasetContentRequest)

      if (retrieveDatasetContentResponse !is SshRetrieveDatasetContentResponse) {
        fail("Should be instance of ${SshRetrieveDatasetContentResponse::class.java.name}")
      } else {
        val readChunks = retrieveDatasetContentResponse.readAsIs() as List<ByteArray>
        val resultingString = readChunks
          .fold("") { acc, nextChunk -> acc + nextChunk.toString(Charsets.UTF_8) }
        assertSoftly {
          retrieveDatasetContentResponse.status.type shouldBe StatusType.SUCCESS
          resultingString shouldContain "Hello World"
        }
      }
    }
  }
})
