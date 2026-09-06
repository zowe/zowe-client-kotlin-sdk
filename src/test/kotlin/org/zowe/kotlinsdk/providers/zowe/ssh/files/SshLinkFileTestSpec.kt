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
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshLinkFileRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshLinkFileResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse

class SshLinkFileTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, FilesAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("linkFile") {
    should("linkFile create a hard link to a USS file") {
      val filePath = "/u/testuser/link1.txt"
      val sourcePath = "/u/testuser/source1.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:linkFile_hard",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val linkFileRequest = SshLinkFileRequest(mockSshConnection, filePath, sourcePath)
      val linkFileResponse = filesApi.linkFile(linkFileRequest)

      if (linkFileResponse !is SshLinkFileResponse) {
        fail("Should be instance of ${SshLinkFileResponse::class.java.name}")
      } else {
        assertSoftly {
          linkFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "ln '$sourcePath' '$filePath'"
        }
      }
    }

    should("linkFile create a symbolic link to a USS file") {
      val filePath = "/u/testuser/link2.txt"
      val sourcePath = "/u/testuser/source2.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:linkFile_symbolic",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val linkFileRequest = SshLinkFileRequest(
        mockSshConnection,
        filePath,
        sourcePath,
        type = SshLinkFileRequest.Type.SYMBOLIC
      )
      val linkFileResponse = filesApi.linkFile(linkFileRequest)

      if (linkFileResponse !is SshLinkFileResponse) {
        fail("Should be instance of ${SshLinkFileResponse::class.java.name}")
      } else {
        assertSoftly {
          linkFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "ln -s '$sourcePath' '$filePath'"
        }
      }
    }

    should("linkFile create an external link to a non USS object") {
      val filePath = "/u/testuser/link3"
      val sourcePath = "TESTUSER.LOAD(PROGRAM)"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:linkFile_external",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val linkFileRequest = SshLinkFileRequest(
        mockSshConnection,
        filePath,
        sourcePath,
        type = SshLinkFileRequest.Type.EXTERNAL
      )
      val linkFileResponse = filesApi.linkFile(linkFileRequest)

      if (linkFileResponse !is SshLinkFileResponse) {
        fail("Should be instance of ${SshLinkFileResponse::class.java.name}")
      } else {
        assertSoftly {
          linkFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "ln -e '$sourcePath' '$filePath'"
        }
      }
    }

    should("linkFile link a USS directory recursively, replacing the existing path") {
      val filePath = "/u/testuser/link4"
      val sourcePath = "/u/testuser/source4"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:linkFile_recursive_force",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val linkFileRequest = SshLinkFileRequest(
        mockSshConnection,
        filePath,
        sourcePath,
        type = SshLinkFileRequest.Type.SYMBOLIC,
        recursive = true,
        force = true
      )
      val linkFileResponse = filesApi.linkFile(linkFileRequest)

      if (linkFileResponse !is SshLinkFileResponse) {
        fail("Should be instance of ${SshLinkFileResponse::class.java.name}")
      } else {
        assertSoftly {
          linkFileResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "ln -s -R -f '$sourcePath' '$filePath'"
        }
      }
    }

    should("linkFile fail because the path to create the link by already exists") {
      val filePath = "/u/testuser/link5_exists.txt"
      val sourcePath = "/u/testuser/source5.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:linkFile_fail_exists",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "ln: FSUM6248 cannot create link \"$filePath\": EDC5117I File exists.\n",
            exitCode = 1
          )
        }
      )

      val linkFileRequest = SshLinkFileRequest(mockSshConnection, filePath, sourcePath)
      val linkFileResponse = filesApi.linkFile(linkFileRequest)

      if (linkFileResponse !is SshLinkFileResponse) {
        fail("Should be instance of ${SshLinkFileResponse::class.java.name}")
      } else {
        assertSoftly {
          linkFileResponse.status.type shouldBe StatusType.ERROR
          linkFileResponse.status.text shouldContain "RC: 1"
          linkFileResponse.status.text shouldContain "EDC5117I File exists"
        }
      }
    }

    should("linkFile fail because the file to link to does not exist") {
      val filePath = "/u/testuser/link6.txt"
      val sourcePath = "/u/testuser/source6_not_found.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:linkFile_fail_source_not_found",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "ln: FSUM6248 cannot access \"$sourcePath\": EDC5129I No such file or directory.\n",
            exitCode = 1
          )
        }
      )

      val linkFileRequest = SshLinkFileRequest(mockSshConnection, filePath, sourcePath)
      val linkFileResponse = filesApi.linkFile(linkFileRequest)

      if (linkFileResponse !is SshLinkFileResponse) {
        fail("Should be instance of ${SshLinkFileResponse::class.java.name}")
      } else {
        assertSoftly {
          linkFileResponse.status.type shouldBe StatusType.ERROR
          linkFileResponse.status.text shouldContain "EDC5129I No such file or directory"
        }
      }
    }

    should("linkFile fail to be built because the path to link to is blank") {
      assertSoftly {
        shouldThrow<IllegalArgumentException> {
          SshLinkFileRequest(mockSshConnection, "/u/testuser/link7.txt", "")
        }
        shouldThrow<IllegalArgumentException> {
          SshLinkFileRequest(mockSshConnection, "/u/testuser/link7.txt", "   ")
        }
      }
    }
  }
})
