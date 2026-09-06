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
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.files.api.FilesAPI
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshMoveFileRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshMoveFileResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse

/** Build the "ls" command response for a USS item that does not exist yet */
private fun notFoundLsResponse(path: String) = SshMockCommandResponse(
  output = "",
  error = "ls: FSUM6785 File or directory \"$path\" is not found\n",
  exitCode = 1
)

class SshMoveFileTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, FilesAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("moveFile") {
    should("moveFile move a USS file to the target that does not exist yet") {
      val fromPath = "/u/testuser/mvf1_source.txt"
      val filePath = "/u/testuser/mvf1_target.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:moveFile_file_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(filePath) },
        handler = { _, _ -> notFoundLsResponse(filePath) }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:moveFile_file",
        resolver = { it.trim().startsWith("mv") && it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val moveFileRequest = SshMoveFileRequest(mockSshConnection, filePath, fromPath)
      val moveFileResponse = filesApi.moveFile(moveFileRequest)

      if (moveFileResponse !is SshMoveFileResponse) {
        fail("Should be instance of ${SshMoveFileResponse::class.java.name}")
      } else {
        assertSoftly {
          moveFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "mv '$fromPath' '$filePath'"
        }
      }
    }

    should("moveFile move a USS directory with all of its content without any recursion option") {
      val fromPath = "/u/testuser/mvf2_source_dir"
      val dirPath = "/u/testuser/mvf2_target_dir"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:moveFile_dir_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(dirPath) },
        handler = { _, _ -> notFoundLsResponse(dirPath) }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:moveFile_dir",
        resolver = { it.trim().startsWith("mv") && it.contains(dirPath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val moveFileRequest = SshMoveFileRequest(mockSshConnection, dirPath, fromPath)
      val moveFileResponse = filesApi.moveFile(moveFileRequest)

      if (moveFileResponse !is SshMoveFileResponse) {
        fail("Should be instance of ${SshMoveFileResponse::class.java.name}")
      } else {
        assertSoftly {
          moveFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "mv '$fromPath' '$dirPath'"
        }
      }
    }

    should("moveFile overwrite the existing target when the overwrite option is provided") {
      val fromPath = "/u/testuser/mvf3_source.txt"
      val filePath = "/u/testuser/mvf3_target.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:moveFile_overwrite_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(filePath) },
        handler = { _, _ -> throw Exception("No existence check is expected when the overwrite is requested") }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:moveFile_overwrite",
        resolver = { it.trim().startsWith("mv") && it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val moveFileRequest = SshMoveFileRequest(mockSshConnection, filePath, fromPath, overwrite = true)
      val moveFileResponse = filesApi.moveFile(moveFileRequest)

      if (moveFileResponse !is SshMoveFileResponse) {
        fail("Should be instance of ${SshMoveFileResponse::class.java.name}")
      } else {
        assertSoftly {
          moveFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "mv '$fromPath' '$filePath'"
        }
      }
    }

    should("moveFile fail because the target already exists and the overwrite option is not provided") {
      val fromPath = "/u/testuser/mvf4_source.txt"
      val filePath = "/u/testuser/mvf4_target.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:moveFile_fail_exists_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            "-rw-r--r--    1 IBMUSER  SYS1         123 Sep  3 10:22 ".padEnd(54) + filePath + "\n"
          )
        }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:moveFile_fail_exists_mv",
        resolver = { it.trim().startsWith("mv") && it.contains(filePath) },
        handler = { _, _ -> throw Exception("The move should not be performed when the target already exists") }
      )

      val moveFileRequest = SshMoveFileRequest(mockSshConnection, filePath, fromPath)
      val moveFileResponse = filesApi.moveFile(moveFileRequest)

      if (moveFileResponse !is SshMoveFileResponse) {
        fail("Should be instance of ${SshMoveFileResponse::class.java.name}")
      } else {
        assertSoftly {
          moveFileResponse.status.type shouldBe StatusType.ERROR
          moveFileResponse.status.text shouldContain "THE FILE \"$filePath\" ALREADY EXISTS"
        }
      }
    }

    should("moveFile fail because the item to move does not exist") {
      val fromPath = "/u/testuser/mvf5_no_source.txt"
      val filePath = "/u/testuser/mvf5_target.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:moveFile_fail_no_source_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(filePath) },
        handler = { _, _ -> notFoundLsResponse(filePath) }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:moveFile_fail_no_source",
        resolver = { it.trim().startsWith("mv") && it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "mv: FSUM6258 cannot open file \"$fromPath\": EDC5129I No such file or directory.\n",
            exitCode = 1
          )
        }
      )

      val moveFileRequest = SshMoveFileRequest(mockSshConnection, filePath, fromPath)
      val moveFileResponse = filesApi.moveFile(moveFileRequest)

      if (moveFileResponse !is SshMoveFileResponse) {
        fail("Should be instance of ${SshMoveFileResponse::class.java.name}")
      } else {
        assertSoftly {
          moveFileResponse.status.type shouldBe StatusType.ERROR
          moveFileResponse.status.text shouldContain "RC: 1"
          moveFileResponse.status.text shouldContain "EDC5129I No such file or directory"
        }
      }
    }

    should("moveFile fail because there are no permissions to move the item") {
      val fromPath = "/u/otheruser/mvf6_source.txt"
      val filePath = "/u/otheruser/mvf6_target.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:moveFile_fail_no_permissions_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(filePath) },
        handler = { _, _ -> notFoundLsResponse(filePath) }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:moveFile_fail_no_permissions",
        resolver = { it.trim().startsWith("mv") && it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "mv: FSUM6260 cannot rename file \"$fromPath\": EDC5111I Permission denied.\n",
            exitCode = 1
          )
        }
      )

      val moveFileRequest = SshMoveFileRequest(mockSshConnection, filePath, fromPath)
      val moveFileResponse = filesApi.moveFile(moveFileRequest)

      if (moveFileResponse !is SshMoveFileResponse) {
        fail("Should be instance of ${SshMoveFileResponse::class.java.name}")
      } else {
        assertSoftly {
          moveFileResponse.status.type shouldBe StatusType.ERROR
          moveFileResponse.status.text shouldContain "EDC5111I Permission denied"
        }
      }
    }
  }
})
