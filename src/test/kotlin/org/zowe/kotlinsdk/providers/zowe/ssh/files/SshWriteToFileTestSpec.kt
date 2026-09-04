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
import io.kotest.matchers.string.shouldContain
import io.kotest.provided.ProjectConfig.mockSshConnection
import io.kotest.provided.ProjectConfig.sshMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.core.DataType
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.files.api.FilesAPI
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshWriteToFileRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshWriteToFileResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse

class SshWriteToFileTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, FilesAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("writeToFile") {
    should("writeToFile execute successfully writing a text content to a USS file") {
      val filePath = "/u/testuser/wtf1.txt"
      val testContent = "Hello World!\nAnd the next line\n".toByteArray()
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToFile_text_success",
        resolver = {
          it.trim().startsWith("cp ")
          && it.contains(filePath)
          && it.contains("/dev/fd0")
        },
        handler = { command, inputStream ->
          executedCommand = command
          val inputContent = inputStream.readBytes()
          assertSoftly { inputContent shouldBe testContent }
          SshMockCommandResponse("")
        }
      )

      val writeToFileRequest = SshWriteToFileRequest(mockSshConnection, filePath, testContent)
      val writeToFileResponse = filesApi.writeToFile(writeToFileRequest)

      if (writeToFileResponse !is SshWriteToFileResponse) {
        fail("Should be instance of ${SshWriteToFileResponse::class.java.name}")
      } else {
        assertSoftly {
          writeToFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "cp -T '/dev/fd0' '$filePath'"
        }
      }
    }

    should("writeToFile execute successfully writing a binary content to a USS file") {
      val filePath = "/u/testuser/wtf2.bin"
      val testContent = "Hello World".toByteArray() +
        byteArrayOf(0x00.toByte(), 0xFF.toByte(), 0x01.toByte(), 0x7F.toByte())
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToFile_binary_success",
        resolver = {
          it.trim().startsWith("cp ")
          && it.contains(filePath)
          && it.contains("/dev/fd0")
        },
        handler = { command, inputStream ->
          executedCommand = command
          val inputContent = inputStream.readBytes()
          assertSoftly { inputContent shouldBe testContent }
          SshMockCommandResponse("")
        }
      )

      val writeToFileRequest = SshWriteToFileRequest(mockSshConnection, filePath, testContent, DataType.BINARY)
      val writeToFileResponse = filesApi.writeToFile(writeToFileRequest)

      if (writeToFileResponse !is SshWriteToFileResponse) {
        fail("Should be instance of ${SshWriteToFileResponse::class.java.name}")
      } else {
        assertSoftly {
          writeToFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "cp -B '/dev/fd0' '$filePath'"
        }
      }
    }

    should("writeToFile execute successfully writing an empty content to a USS file") {
      val filePath = "/u/testuser/wtf3.txt"
      val testContent = ByteArray(0)

      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToFile_empty_success",
        resolver = {
          it.trim().startsWith("cp ")
          && it.contains(filePath)
          && it.contains("/dev/fd0")
        },
        handler = { _, inputStream ->
          val inputContent = inputStream.readBytes()
          assertSoftly { inputContent.size shouldBe 0 }
          SshMockCommandResponse("")
        }
      )

      val writeToFileRequest = SshWriteToFileRequest(mockSshConnection, filePath, testContent)
      val writeToFileResponse = filesApi.writeToFile(writeToFileRequest)

      if (writeToFileResponse !is SshWriteToFileResponse) {
        fail("Should be instance of ${SshWriteToFileResponse::class.java.name}")
      } else {
        assertSoftly { writeToFileResponse.status.type shouldBe StatusType.SUCCESS }
      }
    }

    should("writeToFile fail because the directory to hold the file does not exist") {
      val filePath = "/u/testuser/wtf4_no_dir/file.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToFile_fail_no_dir",
        resolver = {
          it.trim().startsWith("cp ")
          && it.contains(filePath)
          && it.contains("/dev/fd0")
        },
        handler = { _, inputStream ->
          inputStream.readBytes()
          SshMockCommandResponse(
            output = "",
            error = "cp: FSUM6258 cannot open file \"$filePath\": EDC5129I No such file or directory.\n",
            exitCode = 1
          )
        }
      )

      val writeToFileRequest = SshWriteToFileRequest(mockSshConnection, filePath, "Hello World!".toByteArray())
      val writeToFileResponse = filesApi.writeToFile(writeToFileRequest)

      if (writeToFileResponse !is SshWriteToFileResponse) {
        fail("Should be instance of ${SshWriteToFileResponse::class.java.name}")
      } else {
        assertSoftly {
          writeToFileResponse.status.type shouldBe StatusType.ERROR
          writeToFileResponse.status.text shouldContain "RC: 1"
          writeToFileResponse.status.text shouldContain "EDC5129I No such file or directory"
        }
      }
    }

    should("writeToFile fail because there are no permissions to write to the file") {
      val filePath = "/u/testuser/wtf5.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToFile_fail_no_permissions",
        resolver = {
          it.trim().startsWith("cp ")
          && it.contains(filePath)
          && it.contains("/dev/fd0")
        },
        handler = { _, inputStream ->
          inputStream.readBytes()
          SshMockCommandResponse(
            output = "",
            error = "cp: FSUM6258 cannot open file \"$filePath\": EDC5111I Permission denied.\n",
            exitCode = 1
          )
        }
      )

      val writeToFileRequest = SshWriteToFileRequest(mockSshConnection, filePath, "Hello World!".toByteArray())
      val writeToFileResponse = filesApi.writeToFile(writeToFileRequest)

      if (writeToFileResponse !is SshWriteToFileResponse) {
        fail("Should be instance of ${SshWriteToFileResponse::class.java.name}")
      } else {
        assertSoftly {
          writeToFileResponse.status.type shouldBe StatusType.ERROR
          writeToFileResponse.status.text shouldContain "EDC5111I Permission denied"
        }
      }
    }

    should("writeToFile fail due to the incorrect data type provided") {
      val filePath = "/u/testuser/wtf6.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToFile_fail_data_type_error",
        resolver = {
          it.trim().startsWith("cp ")
          && it.contains(filePath)
          && it.contains("/dev/fd0")
        },
        handler = { _, _ ->
          throw Exception("The write should not happen when the data type is incorrect")
        }
      )

      val writeToFileRequest = SshWriteToFileRequest(
        mockSshConnection,
        filePath,
        "Hello World!".toByteArray(),
        DataType.ERROR
      )
      val writeToFileResponse = filesApi.writeToFile(writeToFileRequest)

      if (writeToFileResponse !is SshWriteToFileResponse) {
        fail("Should be instance of ${SshWriteToFileResponse::class.java.name}")
      } else {
        assertSoftly {
          writeToFileResponse.status.type shouldBe StatusType.ERROR
          writeToFileResponse.status.text shouldContain "DATA TYPE ERROR"
        }
      }
    }
  }
})
