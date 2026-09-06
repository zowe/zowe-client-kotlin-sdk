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
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshUnlinkFileRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshUnlinkFileResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse

class SshUnlinkFileTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, FilesAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("unlinkFile") {
    should("unlinkFile remove a link to a USS file") {
      val filePath = "/u/testuser/unlink1.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:unlinkFile_success",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val unlinkFileRequest = SshUnlinkFileRequest(mockSshConnection, filePath)
      val unlinkFileResponse = filesApi.unlinkFile(unlinkFileRequest)

      if (unlinkFileResponse !is SshUnlinkFileResponse) {
        fail("Should be instance of ${SshUnlinkFileResponse::class.java.name}")
      } else {
        assertSoftly {
          unlinkFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "unlink '$filePath'"
        }
      }
    }

    should("unlinkFile remove a link with the path containing the characters to escape") {
      val filePath = "/u/testuser/unlink 2.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:unlinkFile_success_path_with_space",
        resolver = { it.contains("unlink 2.txt") },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val unlinkFileRequest = SshUnlinkFileRequest(mockSshConnection, filePath)
      val unlinkFileResponse = filesApi.unlinkFile(unlinkFileRequest)

      if (unlinkFileResponse !is SshUnlinkFileResponse) {
        fail("Should be instance of ${SshUnlinkFileResponse::class.java.name}")
      } else {
        assertSoftly {
          unlinkFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "unlink '$filePath'"
        }
      }
    }

    should("unlinkFile fail because the link to remove does not exist") {
      val filePath = "/u/testuser/unlink3_not_found.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:unlinkFile_fail_not_found",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "unlink: FSUM6255 cannot unlink \"$filePath\": EDC5129I No such file or directory.\n",
            exitCode = 1
          )
        }
      )

      val unlinkFileRequest = SshUnlinkFileRequest(mockSshConnection, filePath)
      val unlinkFileResponse = filesApi.unlinkFile(unlinkFileRequest)

      if (unlinkFileResponse !is SshUnlinkFileResponse) {
        fail("Should be instance of ${SshUnlinkFileResponse::class.java.name}")
      } else {
        assertSoftly {
          unlinkFileResponse.status.type shouldBe StatusType.ERROR
          unlinkFileResponse.status.text shouldContain "RC: 1"
          unlinkFileResponse.status.text shouldContain "EDC5129I No such file or directory"
        }
      }
    }

    should("unlinkFile fail because the path to unlink is a directory") {
      val filePath = "/u/testuser/unlink4"

      sshMockResponseDispatcher.injectResolver(
        "ssh:unlinkFile_fail_directory",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "unlink: FSUM6255 cannot unlink \"$filePath\": EDC5123I Is a directory.\n",
            exitCode = 1
          )
        }
      )

      val unlinkFileRequest = SshUnlinkFileRequest(mockSshConnection, filePath)
      val unlinkFileResponse = filesApi.unlinkFile(unlinkFileRequest)

      if (unlinkFileResponse !is SshUnlinkFileResponse) {
        fail("Should be instance of ${SshUnlinkFileResponse::class.java.name}")
      } else {
        assertSoftly {
          unlinkFileResponse.status.type shouldBe StatusType.ERROR
          unlinkFileResponse.status.text shouldContain "EDC5123I Is a directory"
        }
      }
    }

    should("unlinkFile fail because there are no permissions to remove the link") {
      val filePath = "/u/testuser/unlink5.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:unlinkFile_fail_no_permissions",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "unlink: FSUM6255 cannot unlink \"$filePath\": EDC5111I Permission denied.\n",
            exitCode = 1
          )
        }
      )

      val unlinkFileRequest = SshUnlinkFileRequest(mockSshConnection, filePath)
      val unlinkFileResponse = filesApi.unlinkFile(unlinkFileRequest)

      if (unlinkFileResponse !is SshUnlinkFileResponse) {
        fail("Should be instance of ${SshUnlinkFileResponse::class.java.name}")
      } else {
        assertSoftly {
          unlinkFileResponse.status.type shouldBe StatusType.ERROR
          unlinkFileResponse.status.text shouldContain "EDC5111I Permission denied"
        }
      }
    }
  }
})
