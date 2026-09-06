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
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshChangeFileOwnerRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshChangeFileOwnerResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse

class SshChangeFileOwnerTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, FilesAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("changeFileOwner") {
    should("changeFileOwner change the owner of a USS file without the group provided") {
      val filePath = "/u/testuser/cfo1.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileOwner_owner_only",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val changeFileOwnerRequest = SshChangeFileOwnerRequest(mockSshConnection, filePath, "IBMUSER")
      val changeFileOwnerResponse = filesApi.changeFileOwner(changeFileOwnerRequest)

      if (changeFileOwnerResponse !is SshChangeFileOwnerResponse) {
        fail("Should be instance of ${SshChangeFileOwnerResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileOwnerResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "chown 'IBMUSER' '$filePath'"
          executedCommand?.shouldNotContain(":")
        }
      }
    }

    should("changeFileOwner change the owner and the group of a USS file") {
      val filePath = "/u/testuser/cfo2.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileOwner_owner_and_group",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val changeFileOwnerRequest = SshChangeFileOwnerRequest(mockSshConnection, filePath, "IBMUSER", "SYS1")
      val changeFileOwnerResponse = filesApi.changeFileOwner(changeFileOwnerRequest)

      if (changeFileOwnerResponse !is SshChangeFileOwnerResponse) {
        fail("Should be instance of ${SshChangeFileOwnerResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileOwnerResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "chown 'IBMUSER:SYS1' '$filePath'"
        }
      }
    }

    should("changeFileOwner change the owner provided as the numeric identifiers") {
      val filePath = "/u/testuser/cfo3.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileOwner_numeric_ids",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val changeFileOwnerRequest = SshChangeFileOwnerRequest(mockSshConnection, filePath, "0", "1")
      val changeFileOwnerResponse = filesApi.changeFileOwner(changeFileOwnerRequest)

      if (changeFileOwnerResponse !is SshChangeFileOwnerResponse) {
        fail("Should be instance of ${SshChangeFileOwnerResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileOwnerResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "chown '0:1' '$filePath'"
        }
      }
    }

    should("changeFileOwner change the owner of a USS directory recursively") {
      val dirPath = "/u/testuser/cfo4_dir"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileOwner_recursive",
        resolver = { it.contains(dirPath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val changeFileOwnerRequest = SshChangeFileOwnerRequest(
        mockSshConnection,
        dirPath,
        "IBMUSER",
        "SYS1",
        recursive = true
      )
      val changeFileOwnerResponse = filesApi.changeFileOwner(changeFileOwnerRequest)

      if (changeFileOwnerResponse !is SshChangeFileOwnerResponse) {
        fail("Should be instance of ${SshChangeFileOwnerResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileOwnerResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "chown -R 'IBMUSER:SYS1' '$dirPath'"
        }
      }
    }

    should("changeFileOwner change the owner of the symbolic link itself when the links are not followed") {
      val filePath = "/u/testuser/cfo5_link"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileOwner_links_change",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val changeFileOwnerRequest = SshChangeFileOwnerRequest(
        mockSshConnection,
        filePath,
        "IBMUSER",
        links = SshChangeFileOwnerRequest.Links.CHANGE
      )
      val changeFileOwnerResponse = filesApi.changeFileOwner(changeFileOwnerRequest)

      if (changeFileOwnerResponse !is SshChangeFileOwnerResponse) {
        fail("Should be instance of ${SshChangeFileOwnerResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileOwnerResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "chown -h 'IBMUSER' '$filePath'"
        }
      }
    }

    should("changeFileOwner follow the symbolic links without any option provided") {
      val filePath = "/u/testuser/cfo6_link"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileOwner_links_follow",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val changeFileOwnerRequest = SshChangeFileOwnerRequest(
        mockSshConnection,
        filePath,
        "IBMUSER",
        links = SshChangeFileOwnerRequest.Links.FOLLOW
      )
      filesApi.changeFileOwner(changeFileOwnerRequest)

      assertSoftly {
        executedCommand?.shouldNotContain("-h")
        executedCommand?.trim() shouldBe "chown 'IBMUSER' '$filePath'"
      }
    }

    should("changeFileOwner fail because the item to change the owner of does not exist") {
      val filePath = "/u/testuser/cfo7_not_found.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileOwner_fail_not_found",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "chown: FSUM6180 file \"$filePath\": EDC5129I No such file or directory.\n",
            exitCode = 1
          )
        }
      )

      val changeFileOwnerRequest = SshChangeFileOwnerRequest(mockSshConnection, filePath, "IBMUSER")
      val changeFileOwnerResponse = filesApi.changeFileOwner(changeFileOwnerRequest)

      if (changeFileOwnerResponse !is SshChangeFileOwnerResponse) {
        fail("Should be instance of ${SshChangeFileOwnerResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileOwnerResponse.status.type shouldBe StatusType.ERROR
          changeFileOwnerResponse.status.text shouldContain "RC: 1"
          changeFileOwnerResponse.status.text shouldContain "EDC5129I No such file or directory"
        }
      }
    }

    should("changeFileOwner fail because the owner to set is unknown") {
      val filePath = "/u/testuser/cfo8.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileOwner_fail_unknown_owner",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "chown: unknown user name \"NOSUCHID\"\n",
            exitCode = 1
          )
        }
      )

      val changeFileOwnerRequest = SshChangeFileOwnerRequest(mockSshConnection, filePath, "NOSUCHID")
      val changeFileOwnerResponse = filesApi.changeFileOwner(changeFileOwnerRequest)

      if (changeFileOwnerResponse !is SshChangeFileOwnerResponse) {
        fail("Should be instance of ${SshChangeFileOwnerResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileOwnerResponse.status.type shouldBe StatusType.ERROR
          changeFileOwnerResponse.status.text shouldContain "unknown user name"
        }
      }
    }

    should("changeFileOwner fail because there are no permissions to change the owner of the item") {
      val filePath = "/u/otheruser/cfo9.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileOwner_fail_no_permissions",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "chown: FSUM6180 file \"$filePath\": EDC5139I Operation not permitted.\n",
            exitCode = 1
          )
        }
      )

      val changeFileOwnerRequest = SshChangeFileOwnerRequest(mockSshConnection, filePath, "IBMUSER")
      val changeFileOwnerResponse = filesApi.changeFileOwner(changeFileOwnerRequest)

      if (changeFileOwnerResponse !is SshChangeFileOwnerResponse) {
        fail("Should be instance of ${SshChangeFileOwnerResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileOwnerResponse.status.type shouldBe StatusType.ERROR
          changeFileOwnerResponse.status.text shouldContain "EDC5139I Operation not permitted"
        }
      }
    }
  }
})
