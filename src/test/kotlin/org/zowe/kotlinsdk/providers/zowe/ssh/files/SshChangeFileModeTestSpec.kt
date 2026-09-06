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
import org.zowe.kotlinsdk.providers.zowe.openssh.files.definitions.SshFileMode
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshChangeFileModeRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshChangeFileModeResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse

class SshChangeFileModeTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, FilesAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("changeFileMode") {
    should("changeFileMode change the mode of a USS file") {
      val filePath = "/u/testuser/cfm1.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileMode_file",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val changeFileModeRequest = SshChangeFileModeRequest(
        mockSshConnection,
        filePath,
        SshFileMode.fromString("rw-r-----")
      )
      val changeFileModeResponse = filesApi.changeFileMode(changeFileModeRequest)

      if (changeFileModeResponse !is SshChangeFileModeResponse) {
        fail("Should be instance of ${SshChangeFileModeResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileModeResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "chmod 640 '$filePath'"
        }
      }
    }

    should("changeFileMode change the mode of a USS directory recursively") {
      val dirPath = "/u/testuser/cfm2_dir"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileMode_recursive",
        resolver = { it.contains(dirPath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val changeFileModeRequest = SshChangeFileModeRequest(
        mockSshConnection,
        dirPath,
        SshFileMode.fromString("rwxr-x---"),
        recursive = true
      )
      val changeFileModeResponse = filesApi.changeFileMode(changeFileModeRequest)

      if (changeFileModeResponse !is SshChangeFileModeResponse) {
        fail("Should be instance of ${SshChangeFileModeResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileModeResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "chmod -R 750 '$dirPath'"
        }
      }
    }

    should("changeFileMode change the mode of the symbolic link itself when the links are not followed") {
      val filePath = "/u/testuser/cfm3_link"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileMode_links_suppress",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val changeFileModeRequest = SshChangeFileModeRequest(
        mockSshConnection,
        filePath,
        SshFileMode.fromString("rwxrwxrwx"),
        links = SshChangeFileModeRequest.Links.SUPPRESS
      )
      val changeFileModeResponse = filesApi.changeFileMode(changeFileModeRequest)

      if (changeFileModeResponse !is SshChangeFileModeResponse) {
        fail("Should be instance of ${SshChangeFileModeResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileModeResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "chmod -h 777 '$filePath'"
        }
      }
    }

    should("changeFileMode follow the symbolic links without any option provided") {
      val filePath = "/u/testuser/cfm4_link"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileMode_links_follow",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val changeFileModeRequest = SshChangeFileModeRequest(
        mockSshConnection,
        filePath,
        SshFileMode.fromString("rw-rw-rw-"),
        links = SshChangeFileModeRequest.Links.FOLLOW
      )
      filesApi.changeFileMode(changeFileModeRequest)

      assertSoftly {
        executedCommand?.shouldNotContain("-h")
        executedCommand?.trim() shouldBe "chmod 666 '$filePath'"
      }
    }

    should("changeFileMode fail because the item to change the mode of does not exist") {
      val filePath = "/u/testuser/cfm5_not_found.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileMode_fail_not_found",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "chmod: FSUM6180 file \"$filePath\": EDC5129I No such file or directory.\n",
            exitCode = 1
          )
        }
      )

      val changeFileModeRequest = SshChangeFileModeRequest(
        mockSshConnection,
        filePath,
        SshFileMode.fromString("rw-r--r--")
      )
      val changeFileModeResponse = filesApi.changeFileMode(changeFileModeRequest)

      if (changeFileModeResponse !is SshChangeFileModeResponse) {
        fail("Should be instance of ${SshChangeFileModeResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileModeResponse.status.type shouldBe StatusType.ERROR
          changeFileModeResponse.status.text shouldContain "RC: 1"
          changeFileModeResponse.status.text shouldContain "EDC5129I No such file or directory"
        }
      }
    }

    should("changeFileMode fail because there are no permissions to change the mode of the item") {
      val filePath = "/u/otheruser/cfm6.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileMode_fail_no_permissions",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "chmod: FSUM6180 file \"$filePath\": EDC5139I Operation not permitted.\n",
            exitCode = 1
          )
        }
      )

      val changeFileModeRequest = SshChangeFileModeRequest(
        mockSshConnection,
        filePath,
        SshFileMode.fromString("rwxrwxrwx")
      )
      val changeFileModeResponse = filesApi.changeFileMode(changeFileModeRequest)

      if (changeFileModeResponse !is SshChangeFileModeResponse) {
        fail("Should be instance of ${SshChangeFileModeResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileModeResponse.status.type shouldBe StatusType.ERROR
          changeFileModeResponse.status.text shouldContain "EDC5139I Operation not permitted"
        }
      }
    }
  }
})
