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
import io.kotest.matchers.string.shouldNotContain
import io.kotest.provided.ProjectConfig.mockSshConnection
import io.kotest.provided.ProjectConfig.sshMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.files.api.FilesAPI
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshDeleteFileRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshDeleteFileResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse

/** Build the command, that is expected to be run to delete the USS item without the recursive option */
private fun expectedDeleteCommand(path: String) =
  "if test -d '$path'; then rmdir '$path'; else rm '$path'; fi"

class SshDeleteFileTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, FilesAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("deleteFile") {
    should("deleteFile delete a USS file") {
      val filePath = "/u/testuser/df1.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:deleteFile_file",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val deleteFileRequest = SshDeleteFileRequest(mockSshConnection, filePath)
      val deleteFileResponse = filesApi.deleteFile(deleteFileRequest)

      if (deleteFileResponse !is SshDeleteFileResponse) {
        fail("Should be instance of ${SshDeleteFileResponse::class.java.name}")
      } else {
        assertSoftly {
          deleteFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe expectedDeleteCommand(filePath)
        }
      }
    }

    should("deleteFile delete an empty USS directory") {
      val dirPath = "/u/testuser/df2_dir"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:deleteFile_empty_dir",
        resolver = { it.contains(dirPath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val deleteFileRequest = SshDeleteFileRequest(mockSshConnection, dirPath)
      val deleteFileResponse = filesApi.deleteFile(deleteFileRequest)

      if (deleteFileResponse !is SshDeleteFileResponse) {
        fail("Should be instance of ${SshDeleteFileResponse::class.java.name}")
      } else {
        assertSoftly {
          deleteFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe expectedDeleteCommand(dirPath)
        }
      }
    }

    should("deleteFile delete a USS directory with all its content recursively") {
      val dirPath = "/u/testuser/df3_dir"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:deleteFile_recursive",
        resolver = { it.contains(dirPath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val deleteFileRequest = SshDeleteFileRequest(mockSshConnection, dirPath, true)
      val deleteFileResponse = filesApi.deleteFile(deleteFileRequest)

      if (deleteFileResponse !is SshDeleteFileResponse) {
        fail("Should be instance of ${SshDeleteFileResponse::class.java.name}")
      } else {
        assertSoftly {
          deleteFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "rm -r '$dirPath'"
        }
      }
    }

    should("deleteFile fail because the directory to delete is not empty") {
      val dirPath = "/u/testuser/df4_dir"

      sshMockResponseDispatcher.injectResolver(
        "ssh:deleteFile_fail_not_empty",
        resolver = { it.contains(dirPath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "rmdir: FSUM6706 cannot remove directory \"$dirPath\": EDC5136I Directory not empty.\n",
            exitCode = 1
          )
        }
      )

      val deleteFileRequest = SshDeleteFileRequest(mockSshConnection, dirPath)
      val deleteFileResponse = filesApi.deleteFile(deleteFileRequest)

      if (deleteFileResponse !is SshDeleteFileResponse) {
        fail("Should be instance of ${SshDeleteFileResponse::class.java.name}")
      } else {
        assertSoftly {
          deleteFileResponse.status.type shouldBe StatusType.ERROR
          deleteFileResponse.status.text shouldContain "RC: 1"
          deleteFileResponse.status.text shouldContain "EDC5136I Directory not empty"
        }
      }
    }

    should("deleteFile fail because the item to delete does not exist") {
      val filePath = "/u/testuser/df5_not_found.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:deleteFile_fail_not_found",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "rm: FSUM6785 File or directory \"$filePath\" is not found\n",
            exitCode = 1
          )
        }
      )

      val deleteFileRequest = SshDeleteFileRequest(mockSshConnection, filePath)
      val deleteFileResponse = filesApi.deleteFile(deleteFileRequest)

      if (deleteFileResponse !is SshDeleteFileResponse) {
        fail("Should be instance of ${SshDeleteFileResponse::class.java.name}")
      } else {
        assertSoftly {
          deleteFileResponse.status.type shouldBe StatusType.ERROR
          deleteFileResponse.status.text shouldContain "is not found"
        }
      }
    }

    should("deleteFile fail because there are no permissions to delete the item") {
      val filePath = "/u/otheruser/df6.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:deleteFile_fail_no_permissions",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "rm: FSUM6702 cannot unlink entry \"$filePath\": EDC5111I Permission denied.\n",
            exitCode = 1
          )
        }
      )

      val deleteFileRequest = SshDeleteFileRequest(mockSshConnection, filePath)
      val deleteFileResponse = filesApi.deleteFile(deleteFileRequest)

      if (deleteFileResponse !is SshDeleteFileResponse) {
        fail("Should be instance of ${SshDeleteFileResponse::class.java.name}")
      } else {
        assertSoftly {
          deleteFileResponse.status.type shouldBe StatusType.ERROR
          deleteFileResponse.status.text shouldContain "EDC5111I Permission denied"
        }
      }
    }

    should("deleteFile not delete the directory content when the recursive option is not provided") {
      val dirPath = "/u/testuser/df7_dir"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:deleteFile_no_recursive_option",
        resolver = { it.contains(dirPath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val deleteFileRequest = SshDeleteFileRequest(mockSshConnection, dirPath)
      filesApi.deleteFile(deleteFileRequest)

      assertSoftly {
        executedCommand?.shouldNotContain("rm -r")
        executedCommand?.shouldContain("rmdir")
      }
    }
  }
})
