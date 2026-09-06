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
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshSetFileACLRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshSetFileACLResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse

class SshSetFileACLTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, FilesAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("setFileACL") {
    should("setFileACL replace the access control list of a USS file") {
      val filePath = "/u/testuser/setacl1.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:setFileACL_set",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val setFileACLRequest = SshSetFileACLRequest(
        mockSshConnection,
        filePath,
        set = "user::rwx,group::r-x,other::---"
      )
      val setFileACLResponse = filesApi.setFileACL(setFileACLRequest)

      if (setFileACLResponse !is SshSetFileACLResponse) {
        fail("Should be instance of ${SshSetFileACLResponse::class.java.name}")
      } else {
        assertSoftly {
          setFileACLResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "setfacl -s 'user::rwx,group::r-x,other::---' '$filePath'"
        }
      }
    }

    should("setFileACL modify and delete the access control list entries by the single request") {
      val filePath = "/u/testuser/setacl2.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:setFileACL_modify_and_delete",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val setFileACLRequest = SshSetFileACLRequest(
        mockSshConnection,
        filePath,
        modify = "user:ANOTHER:rw-",
        delete = "user:OBSOLETE:"
      )
      val setFileACLResponse = filesApi.setFileACL(setFileACLRequest)

      if (setFileACLResponse !is SshSetFileACLResponse) {
        fail("Should be instance of ${SshSetFileACLResponse::class.java.name}")
      } else {
        assertSoftly {
          setFileACLResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe
            "setfacl -m 'user:ANOTHER:rw-' -x 'user:OBSOLETE:' '$filePath'"
        }
      }
    }

    should("setFileACL delete all the extended access control list entries by the type") {
      val filePath = "/u/testuser/setacl3"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:setFileACL_delete_type",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val setFileACLRequest = SshSetFileACLRequest(
        mockSshConnection,
        filePath,
        deleteType = SshSetFileACLRequest.DeleteType.EVERY
      )
      val setFileACLResponse = filesApi.setFileACL(setFileACLRequest)

      if (setFileACLResponse !is SshSetFileACLResponse) {
        fail("Should be instance of ${SshSetFileACLResponse::class.java.name}")
      } else {
        assertSoftly {
          setFileACLResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "setfacl -D e '$filePath'"
        }
      }
    }

    should("setFileACL modify the access control list, aborting on an error and suppressing the links") {
      val filePath = "/u/testuser/setacl4.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:setFileACL_abort_and_links",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val setFileACLRequest = SshSetFileACLRequest(
        mockSshConnection,
        filePath,
        abort = true,
        links = SshSetFileACLRequest.Links.SUPPRESS,
        modify = "group:ACLGRP:r--"
      )
      val setFileACLResponse = filesApi.setFileACL(setFileACLRequest)

      if (setFileACLResponse !is SshSetFileACLResponse) {
        fail("Should be instance of ${SshSetFileACLResponse::class.java.name}")
      } else {
        assertSoftly {
          setFileACLResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "setfacl -a -h -m 'group:ACLGRP:r--' '$filePath'"
        }
      }
    }

    should("setFileACL fail because the file to operate does not exist") {
      val filePath = "/u/testuser/setacl5_not_found.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:setFileACL_fail_not_found",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "setfacl: FSUMB021 stat() error for file \"$filePath\": EDC5129I No such file or directory.\n",
            exitCode = 1
          )
        }
      )

      val setFileACLRequest = SshSetFileACLRequest(mockSshConnection, filePath, modify = "user:ANOTHER:rw-")
      val setFileACLResponse = filesApi.setFileACL(setFileACLRequest)

      if (setFileACLResponse !is SshSetFileACLResponse) {
        fail("Should be instance of ${SshSetFileACLResponse::class.java.name}")
      } else {
        assertSoftly {
          setFileACLResponse.status.type shouldBe StatusType.ERROR
          setFileACLResponse.status.text shouldContain "RC: 1"
          setFileACLResponse.status.text shouldContain "EDC5129I No such file or directory"
        }
      }
    }

    should("setFileACL fail because the access control list entries are malformed") {
      val filePath = "/u/testuser/setacl6.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:setFileACL_fail_malformed_entries",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "setfacl: FSUMB026 invalid ACL entry: not_an_entry\n",
            exitCode = 1
          )
        }
      )

      val setFileACLRequest = SshSetFileACLRequest(mockSshConnection, filePath, set = "not_an_entry")
      val setFileACLResponse = filesApi.setFileACL(setFileACLRequest)

      if (setFileACLResponse !is SshSetFileACLResponse) {
        fail("Should be instance of ${SshSetFileACLResponse::class.java.name}")
      } else {
        assertSoftly {
          setFileACLResponse.status.type shouldBe StatusType.ERROR
          setFileACLResponse.status.text shouldContain "FSUMB026 invalid ACL entry"
        }
      }
    }

    should("setFileACL fail to be built because the operation to perform is not requested correctly") {
      val filePath = "/u/testuser/setacl7.txt"

      assertSoftly {
        shouldThrow<IllegalArgumentException> {
          SshSetFileACLRequest(mockSshConnection, filePath)
        }
        shouldThrow<IllegalArgumentException> {
          SshSetFileACLRequest(mockSshConnection, filePath, set = "user::rwx", modify = "user:ANOTHER:rw-")
        }
        shouldThrow<IllegalArgumentException> {
          SshSetFileACLRequest(
            mockSshConnection,
            filePath,
            deleteType = SshSetFileACLRequest.DeleteType.ACCESS,
            modify = "user:ANOTHER:rw-"
          )
        }
        shouldThrow<IllegalArgumentException> {
          SshSetFileACLRequest(mockSshConnection, filePath, modify = "   ")
        }
      }
    }
  }
})
