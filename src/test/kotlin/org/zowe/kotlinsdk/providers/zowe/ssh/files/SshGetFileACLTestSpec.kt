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
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshGetFileACLRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshGetFileACLResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse

class SshGetFileACLTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, FilesAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("getFileACL") {
    should("getFileACL retrieve the access control list of a USS file") {
      val filePath = "/u/testuser/acl1.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:getFileACL_success",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse(
            "#file: $filePath\n" +
              "#owner: TESTUSER\n" +
              "#group: TESTGRP\n" +
              "user::rwx\n" +
              "group::r-x\n" +
              "other::r--\n" +
              "user:ANOTHER:rw-\n"
          )
        }
      )

      val getFileACLRequest = SshGetFileACLRequest(mockSshConnection, filePath)
      val getFileACLResponse = filesApi.getFileACL(getFileACLRequest)

      if (getFileACLResponse !is SshGetFileACLResponse) {
        fail("Should be instance of ${SshGetFileACLResponse::class.java.name}")
      } else {
        assertSoftly {
          getFileACLResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "getfacl '$filePath'"
          getFileACLResponse.fileACL shouldBe listOf(
            "#file: $filePath",
            "#owner: TESTUSER",
            "#group: TESTGRP",
            "user::rwx",
            "group::r-x",
            "other::r--",
            "user:ANOTHER:rw-"
          )
        }
      }
    }

    should("getFileACL retrieve the default access control list entries of a USS directory") {
      val filePath = "/u/testuser/acl2"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:getFileACL_success_dir_type",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("default:user::rwx\ndefault:group::r-x\ndefault:other::---\n")
        }
      )

      val getFileACLRequest = SshGetFileACLRequest(
        mockSshConnection,
        filePath,
        type = SshGetFileACLRequest.Type.DIR
      )
      val getFileACLResponse = filesApi.getFileACL(getFileACLRequest)

      if (getFileACLResponse !is SshGetFileACLResponse) {
        fail("Should be instance of ${SshGetFileACLResponse::class.java.name}")
      } else {
        assertSoftly {
          getFileACLResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "getfacl -d '$filePath'"
          getFileACLResponse.fileACL shouldBe listOf(
            "default:user::rwx",
            "default:group::r-x",
            "default:other::---"
          )
        }
      }
    }

    should("getFileACL retrieve the access control list with all the display options applied") {
      val filePath = "/u/testuser/acl3.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:getFileACL_success_all_options",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("user:ANOTHER:rw-,group:TESTGRP:r--\n")
        }
      )

      val getFileACLRequest = SshGetFileACLRequest(
        mockSshConnection,
        filePath,
        type = SshGetFileACLRequest.Type.ACCESS,
        user = "ANOTHER",
        useCommas = true,
        suppressHeader = true,
        suppressBaseACL = true
      )
      val getFileACLResponse = filesApi.getFileACL(getFileACLRequest)

      if (getFileACLResponse !is SshGetFileACLResponse) {
        fail("Should be instance of ${SshGetFileACLResponse::class.java.name}")
      } else {
        assertSoftly {
          getFileACLResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "getfacl -a -c -m -o -e 'ANOTHER' '$filePath'"
          getFileACLResponse.fileACL shouldBe listOf("user:ANOTHER:rw-,group:TESTGRP:r--")
        }
      }
    }

    should("getFileACL retrieve the access control list of a USS file with no entry to display") {
      val filePath = "/u/testuser/acl4.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:getFileACL_success_empty",
        resolver = { it.contains(filePath) },
        handler = { _, _ -> SshMockCommandResponse("") }
      )

      val getFileACLRequest = SshGetFileACLRequest(mockSshConnection, filePath, suppressBaseACL = true)
      val getFileACLResponse = filesApi.getFileACL(getFileACLRequest)

      if (getFileACLResponse !is SshGetFileACLResponse) {
        fail("Should be instance of ${SshGetFileACLResponse::class.java.name}")
      } else {
        assertSoftly {
          getFileACLResponse.status.type shouldBe StatusType.SUCCESS
          getFileACLResponse.fileACL shouldBe null
        }
      }
    }

    should("getFileACL fail because the file to operate does not exist") {
      val filePath = "/u/testuser/acl5_not_found.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:getFileACL_fail_not_found",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "getfacl: FSUMB012 stat() error for file \"$filePath\": EDC5129I No such file or directory.\n",
            exitCode = 1
          )
        }
      )

      val getFileACLRequest = SshGetFileACLRequest(mockSshConnection, filePath)
      val getFileACLResponse = filesApi.getFileACL(getFileACLRequest)

      if (getFileACLResponse !is SshGetFileACLResponse) {
        fail("Should be instance of ${SshGetFileACLResponse::class.java.name}")
      } else {
        assertSoftly {
          getFileACLResponse.status.type shouldBe StatusType.ERROR
          getFileACLResponse.status.text shouldContain "RC: 1"
          getFileACLResponse.status.text shouldContain "EDC5129I No such file or directory"
          getFileACLResponse.fileACL shouldBe null
        }
      }
    }

    should("getFileACL fail because there are no permissions to read the file access control list") {
      val filePath = "/u/testuser/acl6.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:getFileACL_fail_no_permissions",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "getfacl: FSUMB012 stat() error for file \"$filePath\": EDC5111I Permission denied.\n",
            exitCode = 1
          )
        }
      )

      val getFileACLRequest = SshGetFileACLRequest(mockSshConnection, filePath)
      val getFileACLResponse = filesApi.getFileACL(getFileACLRequest)

      if (getFileACLResponse !is SshGetFileACLResponse) {
        fail("Should be instance of ${SshGetFileACLResponse::class.java.name}")
      } else {
        assertSoftly {
          getFileACLResponse.status.type shouldBe StatusType.ERROR
          getFileACLResponse.status.text shouldContain "EDC5111I Permission denied"
        }
      }
    }

    should("getFileACL fail to be built because the user to filter the entries by is blank") {
      val filePath = "/u/testuser/acl7.txt"

      assertSoftly {
        shouldThrow<IllegalArgumentException> {
          SshGetFileACLRequest(mockSshConnection, filePath, user = "")
        }
        shouldThrow<IllegalArgumentException> {
          SshGetFileACLRequest(mockSshConnection, filePath, user = "   ")
        }
      }
    }
  }
})
