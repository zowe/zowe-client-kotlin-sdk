/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.ssh.files

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.provided.ProjectConfig.mockSshConnection
import io.kotest.provided.ProjectConfig.sshMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.core.DataType
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.files.api.FilesAPI
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshRetrieveFileContentRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshRetrieveFileContentResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse

class SshRetrieveFileContentTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, FilesAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("retrieveFileContent") {
    should("retrieveFileContent execute successfully retrieving a text file content") {
      val filePath = "/u/testuser/rtrv1.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:retrieveFileContent_text_success",
        resolver = {
          it.trim().startsWith("cp ")
          && it.contains("-T")
          && it.contains(filePath)
          && it.contains("/dev/fd1")
        },
        handler = { _, _ ->
          val output = listOf(
            "some test data here",
            "and here",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output)
        }
      )

      val retrieveFileContentRequest = SshRetrieveFileContentRequest(
        mockSshConnection,
        filePath,
        DataType.TEXT
      )
      val retrieveFileContentResponse = filesApi.retrieveFileContent(retrieveFileContentRequest)

      if (retrieveFileContentResponse !is SshRetrieveFileContentResponse) {
        fail("Should be instance of ${SshRetrieveFileContentResponse::class.java.name}")
      } else {
        assertSoftly {
          retrieveFileContentResponse.status.type shouldBe StatusType.SUCCESS
          retrieveFileContentResponse.fetchedDataType shouldBe DataType.TEXT
          retrieveFileContentResponse.fetchedText shouldContain "some test data"
          retrieveFileContentResponse.fetchedText shouldContain "and here"
        }
      }
    }

    should("retrieveFileContent execute successfully retrieving an empty file") {
      val filePath = "/u/testuser/rtrv2.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:retrieveFileContent_empty_success",
        resolver = {
          it.trim().startsWith("cp ")
          && it.contains(filePath)
          && it.contains("/dev/fd1")
        },
        handler = { _, _ -> SshMockCommandResponse("") }
      )

      val retrieveFileContentRequest = SshRetrieveFileContentRequest(
        mockSshConnection,
        filePath,
        DataType.TEXT
      )
      val retrieveFileContentResponse = filesApi.retrieveFileContent(retrieveFileContentRequest)

      if (retrieveFileContentResponse !is SshRetrieveFileContentResponse) {
        fail("Should be instance of ${SshRetrieveFileContentResponse::class.java.name}")
      } else {
        assertSoftly {
          retrieveFileContentResponse.status.type shouldBe StatusType.SUCCESS
          retrieveFileContentResponse.fetchedText shouldNotBe null
          retrieveFileContentResponse.fetchedText shouldBe ""
        }
      }
    }

    should("retrieveFileContent get a file content in binary format") {
      val filePath = "/u/testuser/rtrv3.bin"

      sshMockResponseDispatcher.injectResolver(
        "ssh:retrieveFileContent_binary_success",
        resolver = {
          it.trim().startsWith("cp ")
          && it.contains("-B")
          && it.contains(filePath)
          && it.contains("/dev/fd1")
        },
        handler = { _, _ ->
          val buffer = mutableListOf<Byte>()

          buffer.addAll("Hello World".toByteArray().toList())
          buffer.add(0x00.toByte()) // null byte
          buffer.add(0xFF.toByte()) // 255
          buffer.add(0x01.toByte()) // 1
          buffer.add(0x7F.toByte()) // 127

          SshMockCommandResponse(String(buffer.toByteArray()))
        }
      )

      val retrieveFileContentRequest = SshRetrieveFileContentRequest(
        mockSshConnection,
        filePath,
        DataType.BINARY
      )
      val retrieveFileContentResponse = filesApi.retrieveFileContent(retrieveFileContentRequest)

      if (retrieveFileContentResponse !is SshRetrieveFileContentResponse) {
        fail("Should be instance of ${SshRetrieveFileContentResponse::class.java.name}")
      } else {
        val readChunks = retrieveFileContentResponse.readAsIs()
        val resultingString = readChunks
          .fold("") { acc, nextChunk -> acc + nextChunk.toString(Charsets.UTF_8) }
        assertSoftly {
          retrieveFileContentResponse.status.type shouldBe StatusType.SUCCESS
          retrieveFileContentResponse.fetchedDataType shouldBe DataType.BINARY
          retrieveFileContentResponse.fetchedText shouldBe null
          resultingString shouldContain "Hello World"
        }
      }
    }

    should("retrieveFileContent fail because there is no such file") {
      val filePath = "/u/testuser/rtrv4.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:retrieveFileContent_fail_not_found",
        resolver = {
          it.trim().startsWith("cp ")
          && it.contains(filePath)
          && it.contains("/dev/fd1")
        },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "cp: FSUM6785 File or directory \"$filePath\" is not found\n",
            exitCode = 1
          )
        }
      )

      val retrieveFileContentRequest = SshRetrieveFileContentRequest(
        mockSshConnection,
        filePath,
        DataType.TEXT
      )
      val retrieveFileContentResponse = filesApi.retrieveFileContent(retrieveFileContentRequest)

      if (retrieveFileContentResponse !is SshRetrieveFileContentResponse) {
        fail("Should be instance of ${SshRetrieveFileContentResponse::class.java.name}")
      } else {
        assertSoftly {
          retrieveFileContentResponse.status.type shouldBe StatusType.ERROR
          retrieveFileContentResponse.status.text shouldContain "RC: 1"
          retrieveFileContentResponse.status.text shouldContain "FSUM6785"
          retrieveFileContentResponse.fetchedDataType shouldBe DataType.ERROR
        }
      }
    }

    should("retrieveFileContent fail due to the incorrect data type requested") {
      val filePath = "/u/testuser/rtrv5.txt"

      val retrieveFileContentRequest = SshRetrieveFileContentRequest(
        mockSshConnection,
        filePath,
        DataType.ERROR
      )
      val retrieveFileContentResponse = filesApi.retrieveFileContent(retrieveFileContentRequest)

      if (retrieveFileContentResponse !is SshRetrieveFileContentResponse) {
        fail("Should be instance of ${SshRetrieveFileContentResponse::class.java.name}")
      } else {
        assertSoftly {
          retrieveFileContentResponse.status.type shouldBe StatusType.ERROR
          retrieveFileContentResponse.status.text shouldContain "DATA TYPE ERROR"
          retrieveFileContentResponse.fetchedDataType shouldBe DataType.ERROR
        }
      }
    }
  }
})
