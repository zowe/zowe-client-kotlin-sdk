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
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.provided.ProjectConfig.mockSshConnection
import io.kotest.provided.ProjectConfig.sshMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.files.api.FilesAPI
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.definitions.SshFromDataset
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshCopyFileRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshCopyFileResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse

/** Build the "ls" command response for a USS item that does not exist yet */
private fun notFoundLsResponse(path: String) = SshMockCommandResponse(
  output = "",
  error = "ls: FSUM6785 File or directory \"$path\" is not found\n",
  exitCode = 1
)

class SshCopyFileTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, FilesAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("copyFile") {
    should("copyFile copy a USS file to the target that does not exist yet") {
      val fromPath = "/u/testuser/cpf1_source.txt"
      val filePath = "/u/testuser/cpf1_target.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:copyFile_file_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(filePath) },
        handler = { _, _ -> notFoundLsResponse(filePath) }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:copyFile_file",
        resolver = { it.trim().startsWith("cp") && it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val copyFileRequest = SshCopyFileRequest(mockSshConnection, filePath, from = fromPath)
      val copyFileResponse = filesApi.copyFile(copyFileRequest)

      if (copyFileResponse !is SshCopyFileResponse) {
        fail("Should be instance of ${SshCopyFileResponse::class.java.name}")
      } else {
        assertSoftly {
          copyFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "cp '$fromPath' '$filePath'"
        }
      }
    }

    should("copyFile copy a USS directory with the recursive, the links and the preserve options provided") {
      val fromPath = "/u/testuser/cpf2_source_dir"
      val dirPath = "/u/testuser/cpf2_target_dir"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:copyFile_options_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(dirPath) },
        handler = { _, _ -> notFoundLsResponse(dirPath) }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:copyFile_options",
        resolver = { it.trim().startsWith("cp") && it.contains(dirPath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val copyFileRequest = SshCopyFileRequest(
        mockSshConnection,
        dirPath,
        from = fromPath,
        recursive = true,
        links = SshCopyFileRequest.Links.ALL,
        preserve = SshCopyFileRequest.Preserve.ALL
      )
      val copyFileResponse = filesApi.copyFile(copyFileRequest)

      if (copyFileResponse !is SshCopyFileResponse) {
        fail("Should be instance of ${SshCopyFileResponse::class.java.name}")
      } else {
        assertSoftly {
          copyFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "cp -R -L -p '$fromPath' '$dirPath'"
        }
      }
    }

    should("copyFile copy a data set member to a USS file") {
      val filePath = "/u/testuser/cpf3_target.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:copyFile_from_dataset_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(filePath) },
        handler = { _, _ -> notFoundLsResponse(filePath) }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:copyFile_from_dataset",
        resolver = { it.trim().startsWith("cp") && it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val copyFileRequest = SshCopyFileRequest(
        mockSshConnection,
        filePath,
        fromDataset = SshFromDataset("TEST.COPY.DS", "MEMBER1")
      )
      val copyFileResponse = filesApi.copyFile(copyFileRequest)

      if (copyFileResponse !is SshCopyFileResponse) {
        fail("Should be instance of ${SshCopyFileResponse::class.java.name}")
      } else {
        assertSoftly {
          copyFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "cp \"//'TEST.COPY.DS(MEMBER1)'\" '$filePath'"
        }
      }
    }

    should("copyFile overwrite the existing target when the overwrite option is provided") {
      val fromPath = "/u/testuser/cpf4_source.txt"
      val filePath = "/u/testuser/cpf4_target.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:copyFile_overwrite_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(filePath) },
        handler = { _, _ -> throw Exception("No existence check is expected when the overwrite is requested") }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:copyFile_overwrite",
        resolver = { it.trim().startsWith("cp") && it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val copyFileRequest = SshCopyFileRequest(mockSshConnection, filePath, from = fromPath, overwrite = true)
      val copyFileResponse = filesApi.copyFile(copyFileRequest)

      if (copyFileResponse !is SshCopyFileResponse) {
        fail("Should be instance of ${SshCopyFileResponse::class.java.name}")
      } else {
        assertSoftly {
          copyFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "cp '$fromPath' '$filePath'"
        }
      }
    }

    should("copyFile fail because the target already exists and the overwrite option is not provided") {
      val fromPath = "/u/testuser/cpf5_source.txt"
      val filePath = "/u/testuser/cpf5_target.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:copyFile_fail_exists_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            "-rw-r--r--    1 IBMUSER  SYS1         123 Sep  3 10:22 ".padEnd(54) + filePath + "\n"
          )
        }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:copyFile_fail_exists_cp",
        resolver = { it.trim().startsWith("cp") && it.contains(filePath) },
        handler = { _, _ -> throw Exception("The copy should not be performed when the target already exists") }
      )

      val copyFileRequest = SshCopyFileRequest(mockSshConnection, filePath, from = fromPath)
      val copyFileResponse = filesApi.copyFile(copyFileRequest)

      if (copyFileResponse !is SshCopyFileResponse) {
        fail("Should be instance of ${SshCopyFileResponse::class.java.name}")
      } else {
        assertSoftly {
          copyFileResponse.status.type shouldBe StatusType.ERROR
          copyFileResponse.status.text shouldContain "THE FILE \"$filePath\" ALREADY EXISTS"
        }
      }
    }

    should("copyFile fail because the source to copy from does not exist") {
      val fromPath = "/u/testuser/cpf6_source.txt"
      val filePath = "/u/testuser/cpf6_target.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:copyFile_fail_no_source_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(filePath) },
        handler = { _, _ -> notFoundLsResponse(filePath) }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:copyFile_fail_no_source",
        resolver = { it.trim().startsWith("cp") && it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "cp: FSUM6258 cannot open file \"$fromPath\": EDC5129I No such file or directory.\n",
            exitCode = 1
          )
        }
      )

      val copyFileRequest = SshCopyFileRequest(mockSshConnection, filePath, from = fromPath)
      val copyFileResponse = filesApi.copyFile(copyFileRequest)

      if (copyFileResponse !is SshCopyFileResponse) {
        fail("Should be instance of ${SshCopyFileResponse::class.java.name}")
      } else {
        assertSoftly {
          copyFileResponse.status.type shouldBe StatusType.ERROR
          copyFileResponse.status.text shouldContain "RC: 1"
          copyFileResponse.status.text shouldContain "EDC5129I No such file or directory"
        }
      }
    }

    should("copyFile produce warning because the records being copied are truncated") {
      val filePath = "/u/testuser/cpf7_target.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:copyFile_warn_truncation_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(filePath) },
        handler = { _, _ -> notFoundLsResponse(filePath) }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:copyFile_warn_truncation",
        resolver = { it.trim().startsWith("cp") && it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "cp: EDC5003I Truncation of a record occurred during an I/O operation.\n",
            exitCode = 1
          )
        }
      )

      val copyFileRequest = SshCopyFileRequest(
        mockSshConnection,
        filePath,
        fromDataset = SshFromDataset("TEST.COPY.DS")
      )
      val copyFileResponse = filesApi.copyFile(copyFileRequest)

      if (copyFileResponse !is SshCopyFileResponse) {
        fail("Should be instance of ${SshCopyFileResponse::class.java.name}")
      } else {
        assertSoftly {
          copyFileResponse.status.type shouldBe StatusType.WARNING
          copyFileResponse.status.text shouldContain "EDC5003I"
        }
      }
    }

    should("copyFile fail to be built because the source to copy from is not provided correctly") {
      val filePath = "/u/testuser/cpf8_target.txt"

      assertSoftly {
        shouldThrow<IllegalArgumentException> {
          SshCopyFileRequest(mockSshConnection, filePath)
        }
        shouldThrow<IllegalArgumentException> {
          SshCopyFileRequest(
            mockSshConnection,
            filePath,
            from = "/u/testuser/cpf8_source.txt",
            fromDataset = SshFromDataset("TEST.COPY.DS")
          )
        }
      }
    }
  }
})
