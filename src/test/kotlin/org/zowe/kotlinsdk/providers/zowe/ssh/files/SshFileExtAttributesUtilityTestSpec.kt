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
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshFileExtAttributesUtilityRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshFileExtAttributesUtilityResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse

class SshFileExtAttributesUtilityTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, FilesAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("fileExtAttributesUtility") {
    should("fileExtAttributesUtility set the extended attributes of a USS file") {
      val filePath = "/u/testuser/fea1.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:fileExtAttributesUtility_set",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val extAttributesRequest = SshFileExtAttributesUtilityRequest(mockSshConnection, filePath, set = "ap")
      val extAttributesResponse = filesApi.fileExtAttributesUtility(extAttributesRequest)

      if (extAttributesResponse !is SshFileExtAttributesUtilityResponse) {
        fail("Should be instance of ${SshFileExtAttributesUtilityResponse::class.java.name}")
      } else {
        assertSoftly {
          extAttributesResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "extattr +ap '$filePath'"
          extAttributesResponse.extendedAttributes shouldBe null
        }
      }
    }

    should("fileExtAttributesUtility reset the extended attributes of a USS file") {
      val filePath = "/u/testuser/fea2.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:fileExtAttributesUtility_reset",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val extAttributesRequest = SshFileExtAttributesUtilityRequest(mockSshConnection, filePath, reset = "ls")
      val extAttributesResponse = filesApi.fileExtAttributesUtility(extAttributesRequest)

      if (extAttributesResponse !is SshFileExtAttributesUtilityResponse) {
        fail("Should be instance of ${SshFileExtAttributesUtilityResponse::class.java.name}")
      } else {
        assertSoftly {
          extAttributesResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "extattr -ls '$filePath'"
        }
      }
    }

    should("fileExtAttributesUtility set and reset the extended attributes by the single request") {
      val filePath = "/u/testuser/fea3.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:fileExtAttributesUtility_set_and_reset",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse("")
        }
      )

      val extAttributesRequest = SshFileExtAttributesUtilityRequest(
        mockSshConnection,
        filePath,
        set = "ap",
        reset = "ls"
      )
      val extAttributesResponse = filesApi.fileExtAttributesUtility(extAttributesRequest)

      if (extAttributesResponse !is SshFileExtAttributesUtilityResponse) {
        fail("Should be instance of ${SshFileExtAttributesUtilityResponse::class.java.name}")
      } else {
        assertSoftly {
          extAttributesResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "extattr +ap -ls '$filePath'"
        }
      }
    }

    should("fileExtAttributesUtility display the extended attributes when neither of the operands is provided") {
      val filePath = "/u/testuser/fea4.txt"
      var executedCommand: String? = null

      sshMockResponseDispatcher.injectResolver(
        "ssh:fileExtAttributesUtility_display",
        resolver = { it.contains(filePath) },
        handler = { command, _ ->
          executedCommand = command
          SshMockCommandResponse(
            "$filePath\n" +
              "    APF authorized = NO\n" +
              "    Program controlled = YES\n" +
              "    Shared address space = NO\n" +
              "    Shared library = NO\n"
          )
        }
      )

      val extAttributesRequest = SshFileExtAttributesUtilityRequest(mockSshConnection, filePath)
      val extAttributesResponse = filesApi.fileExtAttributesUtility(extAttributesRequest)

      if (extAttributesResponse !is SshFileExtAttributesUtilityResponse) {
        fail("Should be instance of ${SshFileExtAttributesUtilityResponse::class.java.name}")
      } else {
        assertSoftly {
          extAttributesResponse.status.type shouldBe StatusType.SUCCESS
          executedCommand?.trim() shouldBe "extattr '$filePath'"
          extAttributesResponse.extendedAttributes shouldBe listOf(
            filePath,
            "APF authorized = NO",
            "Program controlled = YES",
            "Shared address space = NO",
            "Shared library = NO"
          )
        }
      }
    }

    should("fileExtAttributesUtility fail because the file to operate does not exist") {
      val filePath = "/u/testuser/fea5_not_found.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:fileExtAttributesUtility_fail_not_found",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "extattr: FSUM9384 file \"$filePath\": EDC5129I No such file or directory.\n",
            exitCode = 1
          )
        }
      )

      val extAttributesRequest = SshFileExtAttributesUtilityRequest(mockSshConnection, filePath, set = "a")
      val extAttributesResponse = filesApi.fileExtAttributesUtility(extAttributesRequest)

      if (extAttributesResponse !is SshFileExtAttributesUtilityResponse) {
        fail("Should be instance of ${SshFileExtAttributesUtilityResponse::class.java.name}")
      } else {
        assertSoftly {
          extAttributesResponse.status.type shouldBe StatusType.ERROR
          extAttributesResponse.status.text shouldContain "RC: 1"
          extAttributesResponse.status.text shouldContain "EDC5129I No such file or directory"
          extAttributesResponse.extendedAttributes shouldBe null
        }
      }
    }

    should("fileExtAttributesUtility fail because there are no permissions to set the attribute") {
      val filePath = "/u/testuser/fea6.txt"

      sshMockResponseDispatcher.injectResolver(
        "ssh:fileExtAttributesUtility_fail_no_permissions",
        resolver = { it.contains(filePath) },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "extattr: FSUM9384 file \"$filePath\": EDC5139I Operation not permitted.\n",
            exitCode = 1
          )
        }
      )

      val extAttributesRequest = SshFileExtAttributesUtilityRequest(mockSshConnection, filePath, set = "a")
      val extAttributesResponse = filesApi.fileExtAttributesUtility(extAttributesRequest)

      if (extAttributesResponse !is SshFileExtAttributesUtilityResponse) {
        fail("Should be instance of ${SshFileExtAttributesUtilityResponse::class.java.name}")
      } else {
        assertSoftly {
          extAttributesResponse.status.type shouldBe StatusType.ERROR
          extAttributesResponse.status.text shouldContain "EDC5139I Operation not permitted"
        }
      }
    }

    should("fileExtAttributesUtility fail to be built because the attributes are not provided correctly") {
      val filePath = "/u/testuser/fea7.txt"

      assertSoftly {
        shouldThrow<IllegalArgumentException> {
          SshFileExtAttributesUtilityRequest(mockSshConnection, filePath, set = "")
        }
        shouldThrow<IllegalArgumentException> {
          SshFileExtAttributesUtilityRequest(mockSshConnection, filePath, set = "ax")
        }
        shouldThrow<IllegalArgumentException> {
          SshFileExtAttributesUtilityRequest(mockSshConnection, filePath, set = "ap", reset = "pl")
        }
      }
    }
  }
})
