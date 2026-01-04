/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.ssh.datasets

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.sshMockResponseDispatcher
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.mockSshConnection
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshRenameDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshRenameDatasetResponse
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockCommandResponse
import kotlin.text.contains
import kotlin.text.trim

class SshRenameDatasetTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.SSH_NATIVE, DatasetsAPI::class.java)

  afterSpec {
    sshMockResponseDispatcher.clearResolvers()
  }

  context("renameDataset") {
    should("renameDataset execute successfully renaming a data set") {
      val oldDsName = "TEST.OLDNM1"
      val newDsName = "TEST.NEWNM1"

      sshMockResponseDispatcher.injectResolver(
        "ssh:renameDataset_success",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("RENAME")
          && it.contains(oldDsName)
          && it.contains(newDsName)
        },
        handler = { _, _ ->
          SshMockCommandResponse("")
        }
      )

      val renameDatasetRequest = SshRenameDatasetRequest(mockSshConnection, oldDsName, newDsName)
      val renameDatasetResponse = datasetsApi.renameDataset(renameDatasetRequest)

      if (renameDatasetResponse !is SshRenameDatasetResponse) {
        fail("Should be instance of ${SshRenameDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          renameDatasetResponse.status.type shouldBe StatusType.SUCCESS
        }
      }
    }

    should("renameDataset execute successfully renaming a data set member") {
      val dsName = "TEST.DSN2"
      val oldDsAndMemName = "$dsName(OLDMEM1)"
      val newMemName = "NEWMEM1"

      sshMockResponseDispatcher.injectResolver(
        "ssh:renameDataset_success_member",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("RENAME")
          && it.contains(oldDsAndMemName)
          && it.contains(newMemName)
        },
        handler = { _, _ ->
          SshMockCommandResponse("")
        }
      )

      val renameDatasetRequest = SshRenameDatasetRequest(
        mockSshConnection,
        oldDsAndMemName,
        dsName,
        newMemName
      )
      val renameDatasetResponse = datasetsApi.renameDataset(renameDatasetRequest)

      if (renameDatasetResponse !is SshRenameDatasetResponse) {
        fail("Should be instance of ${SshRenameDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          renameDatasetResponse.status.type shouldBe StatusType.SUCCESS
        }
      }
    }

    should("renameDataset fail to rename a non-existing data set") {
      val oldDsName = "TEST.DSN3"
      val newDsName = "TEST.DSNNEW3"

      sshMockResponseDispatcher.injectResolver(
        "ssh:renameDataset_fail_ds_not_found",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("RENAME")
          && it.contains(oldDsName)
          && it.contains(newDsName)
        },
        handler = { _, _ ->
          SshMockCommandResponse(
            "IKJ58201I DATA SET '$oldDsName' NOT IN CATALOG OR AMOUNT OF DATASETS EXCEEDS WORKAREA FOR GENERIC RENAME\n",
            exitCode = 12
          )
        }
      )

      val renameDatasetRequest = SshRenameDatasetRequest(
        mockSshConnection,
        oldDsName,
        newDsName
      )
      val renameDatasetResponse = datasetsApi.renameDataset(renameDatasetRequest)

      if (renameDatasetResponse !is SshRenameDatasetResponse) {
        fail("Should be instance of ${SshRenameDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          renameDatasetResponse.status.type shouldBe StatusType.ERROR
          renameDatasetResponse.status.text shouldContain "IKJ58201I"
        }
      }
    }

    should("renameDataset fail to rename a non-existing data set member") {
      val dsName = "TEST.DSN4"
      val oldDsAndMemName = "$dsName(OLDMEM2)"
      val newMemName = "NEWMEM2"

      sshMockResponseDispatcher.injectResolver(
        "ssh:renameDataset_fail_mem_not_found",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("RENAME")
          && it.contains(oldDsAndMemName)
          && it.contains(newMemName)
        },
        handler = { _, _ ->
          SshMockCommandResponse("IKJ58217I SPECIFIED MEMBER NOT IN DATA SET '$dsName'\n", exitCode = 12)
        }
      )

      val renameDatasetRequest = SshRenameDatasetRequest(
        mockSshConnection,
        oldDsAndMemName,
        dsName,
        newMemName
      )
      val renameDatasetResponse = datasetsApi.renameDataset(renameDatasetRequest)

      if (renameDatasetResponse !is SshRenameDatasetResponse) {
        fail("Should be instance of ${SshRenameDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          renameDatasetResponse.status.type shouldBe StatusType.ERROR
          renameDatasetResponse.status.text shouldContain "IKJ58217I SPECIFIED MEMBER NOT IN DATA SET"
        }
      }
    }

    should("renameDataset fail to rename the data set cause a data set with the new name already exist") {
      val oldDsName = "TEST.DSN5"
      val newDsName = "TEST.DSNNEW5"

      sshMockResponseDispatcher.injectResolver(
        "ssh:renameDataset_fail_ds_already_exist",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("RENAME")
          && it.contains(oldDsName)
          && it.contains(newDsName)
        },
        handler = { _, _ ->
          SshMockCommandResponse(
            "IKJ58222I DATA SET '$newDsName' ALREADY EXISTS\n",
            exitCode = 12
          )
        }
      )

      val renameDatasetRequest = SshRenameDatasetRequest(
        mockSshConnection,
        oldDsName,
        newDsName
      )
      val renameDatasetResponse = datasetsApi.renameDataset(renameDatasetRequest)

      if (renameDatasetResponse !is SshRenameDatasetResponse) {
        fail("Should be instance of ${SshRenameDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          renameDatasetResponse.status.type shouldBe StatusType.ERROR
          renameDatasetResponse.status.text shouldContain "IKJ58222I"
        }
      }
    }

    should("renameDataset fail to rename the data set member because the data set member with the new name already exist") {
      val dsName = "TEST.DSN6"
      val oldDsAndMemName = "$dsName(OLDMEM6)"
      val newMemName = "NEWMEM6"

      sshMockResponseDispatcher.injectResolver(
        "ssh:renameDataset_fail_mem_already_exist",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("RENAME")
          && it.contains(oldDsAndMemName)
          && it.contains(newMemName)
        },
        handler = { _, _ ->
          SshMockCommandResponse("IKJ58223I MEMBER $newMemName ALREADY EXISTS\n", exitCode = 12)
        }
      )

      val renameDatasetRequest = SshRenameDatasetRequest(
        mockSshConnection,
        oldDsAndMemName,
        dsName,
        newMemName
      )
      val renameDatasetResponse = datasetsApi.renameDataset(renameDatasetRequest)

      if (renameDatasetResponse !is SshRenameDatasetResponse) {
        fail("Should be instance of ${SshRenameDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          renameDatasetResponse.status.type shouldBe StatusType.ERROR
          renameDatasetResponse.status.text shouldContain "IKJ58223I"
        }
      }
    }

    should("renameDataset fail to rename the data set member to a data set") {
      val dsName = "TEST.DSN7"
      val oldDsAndMemName = "$dsName(OLDMEM7)"
      val newDsName = "TEST.NEWDSN7"

      sshMockResponseDispatcher.injectResolver(
        "ssh:renameDataset_fail_mem_to_ds_not_allowed",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("RENAME")
          && it.contains(oldDsAndMemName)
          && it.contains(newDsName)
        },
        handler = { _, _ ->
          SshMockCommandResponse(
            "IKJ58208I INVALID USE OF COMMAND+\n" +
              "IKJ58208I A MEMBER NAME CAN NOT BE RENAMED TO A DATA SET\n",
            exitCode = 12
          )
        }
      )

      val renameDatasetRequest = SshRenameDatasetRequest(
        mockSshConnection,
        oldDsAndMemName,
        newDsName
      )
      val renameDatasetResponse = datasetsApi.renameDataset(renameDatasetRequest)

      if (renameDatasetResponse !is SshRenameDatasetResponse) {
        fail("Should be instance of ${SshRenameDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          renameDatasetResponse.status.type shouldBe StatusType.ERROR
          renameDatasetResponse.status.text shouldContain "IKJ58208I"
        }
      }
    }
  }
})
