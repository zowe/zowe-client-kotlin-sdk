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
import org.zowe.kotlinsdk.core.files.data.FileItem
import org.zowe.kotlinsdk.providers.zowe.openssh.files.definitions.SshFileMode
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshCreateFileRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshCreateFileResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse

/** Build the "ls" command response for a USS item that does not exist yet */
private fun notFoundLsResponse(path: String) = SshMockCommandResponse(
  output = "",
  error = "ls: FSUM6785 File or directory \"$path\" is not found\n",
  exitCode = 1
)

class SshCreateFileTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, FilesAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("createFile") {
    should("createFile create a USS file without the mode provided") {
      val filePath = "/u/testuser/cf1.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:createFile_file_no_mode_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(filePath) },
        handler = { _, _ -> notFoundLsResponse(filePath) }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:createFile_file_no_mode",
        resolver = { it.trim().startsWith("touch") && it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val createFileRequest = SshCreateFileRequest(mockSshConnection, filePath)
      val createFileResponse = filesApi.createFile(createFileRequest)

      if (createFileResponse !is SshCreateFileResponse) {
        fail("Should be instance of ${SshCreateFileResponse::class.java.name}")
      } else {
        assertSoftly {
          createFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "touch '$filePath'"
        }
      }
    }

    should("createFile create a USS file with the mode provided") {
      val filePath = "/u/testuser/cf2.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:createFile_file_with_mode_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(filePath) },
        handler = { _, _ -> notFoundLsResponse(filePath) }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:createFile_file_with_mode",
        resolver = { it.trim().startsWith("touch") && it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val createFileRequest = SshCreateFileRequest(
        mockSshConnection,
        filePath,
        FileItem.FileType.FILE,
        SshFileMode.fromString("rw-r-----")
      )
      val createFileResponse = filesApi.createFile(createFileRequest)

      if (createFileResponse !is SshCreateFileResponse) {
        fail("Should be instance of ${SshCreateFileResponse::class.java.name}")
      } else {
        assertSoftly {
          createFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "touch '$filePath' && chmod 640 '$filePath'"
        }
      }
    }

    should("createFile create a USS directory with the mode provided") {
      val dirPath = "/u/testuser/cf3_dir"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:createFile_dir_with_mode",
        resolver = { it.trim().startsWith("mkdir") && it.contains(dirPath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:createFile_dir_with_mode_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(dirPath) },
        handler = { _, _ -> throw Exception("No existence check is expected for a directory") }
      )

      val createFileRequest = SshCreateFileRequest(
        mockSshConnection,
        dirPath,
        FileItem.FileType.DIRECTORY,
        SshFileMode.fromString("rwxr-x---")
      )
      val createFileResponse = filesApi.createFile(createFileRequest)

      if (createFileResponse !is SshCreateFileResponse) {
        fail("Should be instance of ${SshCreateFileResponse::class.java.name}")
      } else {
        assertSoftly {
          createFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "mkdir -m 750 '$dirPath'"
        }
      }
    }

    should("createFile create a USS directory without the mode provided") {
      val dirPath = "/u/testuser/cf4_dir"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:createFile_dir_no_mode",
        resolver = { it.trim().startsWith("mkdir") && it.contains(dirPath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val createFileRequest = SshCreateFileRequest(mockSshConnection, dirPath, FileItem.FileType.DIRECTORY)
      val createFileResponse = filesApi.createFile(createFileRequest)

      if (createFileResponse !is SshCreateFileResponse) {
        fail("Should be instance of ${SshCreateFileResponse::class.java.name}")
      } else {
        assertSoftly {
          createFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "mkdir '$dirPath'"
        }
      }
    }

    should("createFile fail because the file to create already exists") {
      val filePath = "/u/testuser/cf5.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:createFile_fail_exists_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            "-rw-r--r--    1 IBMUSER  SYS1         123 Sep  3 10:22 ".padEnd(54) + filePath + "\n"
          )
        }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:createFile_fail_exists_touch",
        resolver = { it.trim().startsWith("touch") && it.contains(filePath) },
        handler = { _, _ -> throw Exception("The file should not be created when it already exists") }
      )

      val createFileRequest = SshCreateFileRequest(mockSshConnection, filePath)
      val createFileResponse = filesApi.createFile(createFileRequest)

      if (createFileResponse !is SshCreateFileResponse) {
        fail("Should be instance of ${SshCreateFileResponse::class.java.name}")
      } else {
        assertSoftly {
          createFileResponse.status.type shouldBe StatusType.ERROR
          createFileResponse.status.text shouldContain "THE FILE \"$filePath\" ALREADY EXISTS"
        }
      }
    }

    should("createFile fail because the directory to create already exists") {
      val dirPath = "/u/testuser/cf6_dir"

      sshMockResponseDispatcher.injectResolver(
        "ssh:createFile_fail_dir_exists",
        resolver = { it.trim().startsWith("mkdir") && it.contains(dirPath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "mkdir: FSUM6404 directory \"$dirPath\": EDC5117I File exists.\n",
            exitCode = 1
          )
        }
      )

      val createFileRequest = SshCreateFileRequest(mockSshConnection, dirPath, FileItem.FileType.DIRECTORY)
      val createFileResponse = filesApi.createFile(createFileRequest)

      if (createFileResponse !is SshCreateFileResponse) {
        fail("Should be instance of ${SshCreateFileResponse::class.java.name}")
      } else {
        assertSoftly {
          createFileResponse.status.type shouldBe StatusType.ERROR
          createFileResponse.status.text shouldContain "RC: 1"
          createFileResponse.status.text shouldContain "EDC5117I File exists"
        }
      }
    }

    should("createFile fail because the path to hold the item does not exist") {
      val filePath = "/u/testuser/cf7_no_dir/file.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:createFile_fail_no_dir_ls",
        resolver = { it.trim().startsWith("ls -l") && it.contains(filePath) },
        handler = { _, _ -> notFoundLsResponse(filePath) }
      )
      sshMockResponseDispatcher.injectResolver(
        "ssh:createFile_fail_no_dir",
        resolver = { it.trim().startsWith("touch") && it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "touch: FSUM6180 file \"$filePath\": EDC5129I No such file or directory.\n",
            exitCode = 1
          )
        }
      )

      val createFileRequest = SshCreateFileRequest(mockSshConnection, filePath)
      val createFileResponse = filesApi.createFile(createFileRequest)

      if (createFileResponse !is SshCreateFileResponse) {
        fail("Should be instance of ${SshCreateFileResponse::class.java.name}")
      } else {
        assertSoftly {
          createFileResponse.status.type shouldBe StatusType.ERROR
          createFileResponse.status.text shouldContain "EDC5129I No such file or directory"
        }
      }
    }

    should("createFile fail because there are no permissions to create the item") {
      val dirPath = "/u/otheruser/cf8_dir"

      sshMockResponseDispatcher.injectResolver(
        "ssh:createFile_fail_no_permissions",
        resolver = { it.trim().startsWith("mkdir") && it.contains(dirPath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "mkdir: FSUM6404 directory \"$dirPath\": EDC5111I Permission denied.\n",
            exitCode = 1
          )
        }
      )

      val createFileRequest = SshCreateFileRequest(mockSshConnection, dirPath, FileItem.FileType.DIRECTORY)
      val createFileResponse = filesApi.createFile(createFileRequest)

      if (createFileResponse !is SshCreateFileResponse) {
        fail("Should be instance of ${SshCreateFileResponse::class.java.name}")
      } else {
        assertSoftly {
          createFileResponse.status.type shouldBe StatusType.ERROR
          createFileResponse.status.text shouldContain "EDC5111I Permission denied"
        }
      }
    }

    should("createFile build the mode of all the permission combinations correctly") {
      assertSoftly {
        SshFileMode.fromString("---------").toOctalString() shouldBe "000"
        SshFileMode.fromString("rwxrwxrwx").toOctalString() shouldBe "777"
        SshFileMode.fromString("rw-rw-rw-").toOctalString() shouldBe "666"
        SshFileMode.fromString("r-x-w---x").toOctalString() shouldBe "521"
        SshFileMode.fromString("--x-w-r--").toOctalString() shouldBe "124"
      }
    }

    should("createFile not pass the mode option when the mode is not provided") {
      val dirPath = "/u/testuser/cf10_dir"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:createFile_dir_no_mode_option",
        resolver = { it.trim().startsWith("mkdir") && it.contains(dirPath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val createFileRequest = SshCreateFileRequest(mockSshConnection, dirPath, FileItem.FileType.DIRECTORY)
      filesApi.createFile(createFileRequest)

      assertSoftly {
        executedCommand?.shouldNotContain("-m")
        executedCommand?.shouldNotContain("chmod")
      }
    }
  }
})
