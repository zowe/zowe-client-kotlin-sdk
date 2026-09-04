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
import org.zowe.kotlinsdk.core.files.data.FileItem
import org.zowe.kotlinsdk.providers.zowe.openssh.files.definitions.SshFileItem
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshListFilesRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.files.messaging.SshListFilesResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse

/**
 * Build a single "ls -l" output line the same way z/OS USS does it:
 * the file name always starts at the 54th column
 */
private fun lsLine(mode: String, meta: String, name: String): String = "$mode $meta".padEnd(54) + name

class SshListFilesTestSpec : ShouldSpec({
  val filesApi = zoweAPIProvider.getApi(WrapperType.OPEN_SSH, FilesAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("listFiles") {
    should("listFiles return the correct list of the directory items") {
      val dirPath = "/u/testuser/list1"

      sshMockResponseDispatcher.injectResolver(
        "ssh:listFiles_success",
        resolver = {
          it.trim().startsWith("ls -l")
          && it.contains(dirPath)
        },
        handler = { _, _ ->
          val output = listOf(
            "total 96",
            lsLine("drwxr-xr-x", "   2 IBMUSER  SYS1     8192 Sep  3 10:22", "subdir"),
            lsLine("-rw-r--r--", "   1 IBMUSER  SYS1      123 Sep  3 10:22", "file.txt"),
            lsLine("-rwxrwxrwx", "   1 IBMUSER  SYS1      512 Sep  3 10:23", "script.sh"),
            lsLine("lrwxrwxrwx", "   1 IBMUSER  SYS1        8 Sep  3 10:24", "link -> file.txt"),
            lsLine("lrwxrwxrwx", "   1 IBMUSER  SYS1       21 Sep  3 10:25", "abs.link -> /u/testuser/list1/file.txt"),
            "",
          ).joinToString("\n")
          SshMockCommandResponse(output)
        }
      )

      val listFilesRequest = SshListFilesRequest(mockSshConnection, dirPath)
      val listFilesResponse = filesApi.listFiles(listFilesRequest)

      if (listFilesResponse !is SshListFilesResponse) {
        fail("Should be instance of ${SshListFilesResponse::class.java.name}")
      } else {
        assertSoftly {
          listFilesResponse.status.type shouldBe StatusType.SUCCESS
          listFilesResponse.items.size shouldBe 5

          listFilesResponse.items[0].name shouldBe "subdir"
          listFilesResponse.items[0].fileType shouldBe FileItem.FileType.DIRECTORY
          listFilesResponse.items[0].isSymlink shouldBe false
          listFilesResponse.items[0].fileMode.toString() shouldBe "rwxr-xr-x"

          listFilesResponse.items[1].name shouldBe "file.txt"
          listFilesResponse.items[1].fileType shouldBe FileItem.FileType.FILE
          listFilesResponse.items[1].isSymlink shouldBe false
          listFilesResponse.items[1].fileMode?.owner?.write shouldBe true
          listFilesResponse.items[1].fileMode?.group?.write shouldBe false
          listFilesResponse.items[1].fileMode?.others?.execute shouldBe false

          listFilesResponse.items[2].name shouldBe "script.sh"
          listFilesResponse.items[2].fileMode.toString() shouldBe "rwxrwxrwx"

          listFilesResponse.items[3].name shouldBe "link"
          listFilesResponse.items[3].fileType shouldBe FileItem.FileType.FILE
          listFilesResponse.items[3].isSymlink shouldBe true
          (listFilesResponse.items[3] as SshFileItem).target shouldBe "file.txt"

          listFilesResponse.items[4].name shouldBe "abs.link"
          listFilesResponse.items[4].isSymlink shouldBe true
          (listFilesResponse.items[4] as SshFileItem).target shouldBe "/u/testuser/list1/file.txt"
        }
      }
    }

    should("listFiles return an empty list for an empty directory") {
      val dirPath = "/u/testuser/list2"

      sshMockResponseDispatcher.injectResolver(
        "ssh:listFiles_empty",
        resolver = {
          it.trim().startsWith("ls -l")
          && it.contains(dirPath)
        },
        handler = { _, _ ->
          SshMockCommandResponse(listOf("total 0", "").joinToString("\n"))
        }
      )

      val listFilesRequest = SshListFilesRequest(mockSshConnection, dirPath)
      val listFilesResponse = filesApi.listFiles(listFilesRequest)

      if (listFilesResponse !is SshListFilesResponse) {
        fail("Should be instance of ${SshListFilesResponse::class.java.name}")
      } else {
        assertSoftly {
          listFilesResponse.status.type shouldBe StatusType.SUCCESS
          listFilesResponse.items.size shouldBe 0
        }
      }
    }

    should("listFiles fail as there is no such file or directory") {
      val dirPath = "/u/testuser/list3"

      sshMockResponseDispatcher.injectResolver(
        "ssh:listFiles_not_found",
        resolver = {
          it.trim().startsWith("ls -l")
          && it.contains(dirPath)
        },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "ls: FSUM6785 File or directory \"$dirPath\" is not found\n",
            exitCode = 1
          )
        }
      )

      val listFilesRequest = SshListFilesRequest(mockSshConnection, dirPath)
      val listFilesResponse = filesApi.listFiles(listFilesRequest)

      if (listFilesResponse !is SshListFilesResponse) {
        fail("Should be instance of ${SshListFilesResponse::class.java.name}")
      } else {
        assertSoftly {
          listFilesResponse.status.type shouldBe StatusType.ERROR
          listFilesResponse.status.text shouldContain "FSUM6785"
          listFilesResponse.status.text shouldContain "is not found"
          listFilesResponse.items.size shouldBe 0
        }
      }
    }

    should("listFiles fail with SSH RC=1 due to the permissions issue") {
      val dirPath = "/u/testuser/list4"

      sshMockResponseDispatcher.injectResolver(
        "ssh:listFiles_fail_generic",
        resolver = {
          it.trim().startsWith("ls -l")
          && it.contains(dirPath)
        },
        handler = { _, _ ->
          SshMockCommandResponse(
            output = "",
            error = "ls: FSUM6785 EDC5111I Permission denied. (errno2=0x0C0F00B5)\n",
            exitCode = 1
          )
        }
      )

      val listFilesRequest = SshListFilesRequest(mockSshConnection, dirPath)
      val listFilesResponse = filesApi.listFiles(listFilesRequest)

      if (listFilesResponse !is SshListFilesResponse) {
        fail("Should be instance of ${SshListFilesResponse::class.java.name}")
      } else {
        assertSoftly {
          listFilesResponse.status.type shouldBe StatusType.ERROR
          listFilesResponse.status.text shouldContain "RC: 1"
          listFilesResponse.status.text shouldContain "Permission denied"
          listFilesResponse.items.size shouldBe 0
        }
      }
    }
  }
})
