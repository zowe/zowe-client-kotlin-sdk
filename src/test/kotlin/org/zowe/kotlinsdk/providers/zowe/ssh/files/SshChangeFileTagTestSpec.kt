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
import io.kotest.matchers.string.shouldNotContain
import io.kotest.provided.ProjectConfig.mockSshConnection
import io.kotest.provided.ProjectConfig.sshMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.files.api.FilesAPI
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshChangeFileTagRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshChangeFileTagResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse

class SshChangeFileTagTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, FilesAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("changeFileTag") {
    should("changeFileTag set the text tag with the code set provided") {
      val filePath = "/u/testuser/cft1.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileTag_set_text",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val changeFileTagRequest = SshChangeFileTagRequest(
        mockSshConnection,
        filePath,
        SshChangeFileTagRequest.Action.SET,
        SshChangeFileTagRequest.Type.TEXT,
        "IBM-1047"
      )
      val changeFileTagResponse = filesApi.changeFileTag(changeFileTagRequest)

      if (changeFileTagResponse !is SshChangeFileTagResponse) {
        fail("Should be instance of ${SshChangeFileTagResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileTagResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "chtag -t -c IBM-1047 '$filePath'"
          changeFileTagResponse.currentTagInfo shouldBe null
        }
      }
    }

    should("changeFileTag set the binary tag without any code set") {
      val filePath = "/u/testuser/cft2.bin"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileTag_set_binary",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val changeFileTagRequest = SshChangeFileTagRequest(
        mockSshConnection,
        filePath,
        SshChangeFileTagRequest.Action.SET,
        SshChangeFileTagRequest.Type.BINARY
      )
      val changeFileTagResponse = filesApi.changeFileTag(changeFileTagRequest)

      if (changeFileTagResponse !is SshChangeFileTagResponse) {
        fail("Should be instance of ${SshChangeFileTagResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileTagResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "chtag -b '$filePath'"
          executedCommand?.shouldNotContain("-c")
        }
      }
    }

    should("changeFileTag set the mixed tag of a USS directory recursively") {
      val dirPath = "/u/testuser/cft3_dir"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileTag_set_mixed_recursive",
        resolver = { it.contains(dirPath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val changeFileTagRequest = SshChangeFileTagRequest(
        mockSshConnection,
        dirPath,
        SshChangeFileTagRequest.Action.SET,
        SshChangeFileTagRequest.Type.MIXED,
        "ISO8859-1",
        recursive = true
      )
      val changeFileTagResponse = filesApi.changeFileTag(changeFileTagRequest)

      if (changeFileTagResponse !is SshChangeFileTagResponse) {
        fail("Should be instance of ${SshChangeFileTagResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileTagResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "chtag -m -c ISO8859-1 -R '$dirPath'"
        }
      }
    }

    should("changeFileTag remove the tag of a USS file") {
      val filePath = "/u/testuser/cft4.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileTag_remove",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val changeFileTagRequest = SshChangeFileTagRequest(
        mockSshConnection,
        filePath,
        SshChangeFileTagRequest.Action.REMOVE
      )
      val changeFileTagResponse = filesApi.changeFileTag(changeFileTagRequest)

      if (changeFileTagResponse !is SshChangeFileTagResponse) {
        fail("Should be instance of ${SshChangeFileTagResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileTagResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "chtag -r '$filePath'"
        }
      }
    }

    should("changeFileTag list the tag information of a USS file") {
      val filePath = "/u/testuser/cft5.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileTag_list",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("t IBM-1047    T=on  $filePath\n")
        }
      )

      val changeFileTagRequest = SshChangeFileTagRequest(
        mockSshConnection,
        filePath,
        SshChangeFileTagRequest.Action.LIST
      )
      val changeFileTagResponse = filesApi.changeFileTag(changeFileTagRequest)

      if (changeFileTagResponse !is SshChangeFileTagResponse) {
        fail("Should be instance of ${SshChangeFileTagResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileTagResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "chtag -p '$filePath'"
          changeFileTagResponse.currentTagInfo shouldBe "t IBM-1047    T=on  $filePath"
        }
      }
    }

    should("changeFileTag change the tag of the symbolic link itself when the links are not followed") {
      val filePath = "/u/testuser/cft6_link"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileTag_links_suppress",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val changeFileTagRequest = SshChangeFileTagRequest(
        mockSshConnection,
        filePath,
        SshChangeFileTagRequest.Action.SET,
        SshChangeFileTagRequest.Type.TEXT,
        "IBM-1047",
        links = SshChangeFileTagRequest.Links.SUPPRESS
      )
      val changeFileTagResponse = filesApi.changeFileTag(changeFileTagRequest)

      if (changeFileTagResponse !is SshChangeFileTagResponse) {
        fail("Should be instance of ${SshChangeFileTagResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileTagResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "chtag -t -c IBM-1047 -h '$filePath'"
        }
      }
    }

    should("changeFileTag follow the symbolic links without any option provided") {
      val filePath = "/u/testuser/cft7_link"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileTag_links_change",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val changeFileTagRequest = SshChangeFileTagRequest(
        mockSshConnection,
        filePath,
        SshChangeFileTagRequest.Action.REMOVE,
        links = SshChangeFileTagRequest.Links.CHANGE
      )
      filesApi.changeFileTag(changeFileTagRequest)

      assertSoftly {
        executedCommand?.shouldNotContain("-h")
        executedCommand?.trim() shouldBe "chtag -r '$filePath'"
      }
    }

    should("changeFileTag fail because the item to change the tag of does not exist") {
      val filePath = "/u/testuser/cft8_not_found.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:changeFileTag_fail_not_found",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "chtag: FSUMF180 file \"$filePath\": EDC5129I No such file or directory.\n",
            exitCode = 1
          )
        }
      )

      val changeFileTagRequest = SshChangeFileTagRequest(
        mockSshConnection,
        filePath,
        SshChangeFileTagRequest.Action.LIST
      )
      val changeFileTagResponse = filesApi.changeFileTag(changeFileTagRequest)

      if (changeFileTagResponse !is SshChangeFileTagResponse) {
        fail("Should be instance of ${SshChangeFileTagResponse::class.java.name}")
      } else {
        assertSoftly {
          changeFileTagResponse.status.type shouldBe StatusType.ERROR
          changeFileTagResponse.status.text shouldContain "RC: 1"
          changeFileTagResponse.status.text shouldContain "EDC5129I No such file or directory"
          changeFileTagResponse.currentTagInfo shouldBe null
        }
      }
    }

    should("changeFileTag fail to be built because the tag content is not provided correctly") {
      val filePath = "/u/testuser/cft9.txt"

      assertSoftly {
        shouldThrow<IllegalArgumentException> {
          SshChangeFileTagRequest(mockSshConnection, filePath, SshChangeFileTagRequest.Action.SET)
        }
        shouldThrow<IllegalArgumentException> {
          SshChangeFileTagRequest(
            mockSshConnection,
            filePath,
            SshChangeFileTagRequest.Action.REMOVE,
            SshChangeFileTagRequest.Type.TEXT
          )
        }
        shouldThrow<IllegalArgumentException> {
          SshChangeFileTagRequest(
            mockSshConnection,
            filePath,
            SshChangeFileTagRequest.Action.LIST,
            codeSet = "IBM-1047"
          )
        }
      }
    }
  }
})
