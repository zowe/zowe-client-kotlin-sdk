/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.providers.zowe.ssh

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.sshMockResponseDispatcher
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.mockSshConnection
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshListDatasetMembersRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshListDatasetMembersResponse
import kotlin.text.contains
import kotlin.text.trim

class SshListDatasetMembersTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.SSH_NATIVE, DatasetsAPI::class.java)

  context("listDatasetMembers") {
    should("listDatasetMembers return the correct list of data set members") {
      val dsName = "TEST.LISTM1"

      sshMockResponseDispatcher.injectResolver(
        "ssh:listDatasetMembers_success",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("LISTDS")
          && it.contains("MEMBERS")
          && it.contains(dsName)
        },
        handler = {
          val output = listOf(
            dsName.padEnd(44),
            "--RECFM-LRECL-BLKSIZE-DSORG",
            "  FB    80    8000    PO".padEnd(114),
            "--VOLUMES--",
            "  VOLUM1",
            "--MEMBERS--",
            "  TESTM1",
            "  TESTM2",
            "  TESTM3",
            "  TESTM4",
            "",
          ).joinToString("\n")
          SshMockCommandResponse(output)
        }
      )

      val listDatasetMembersRequest = SshListDatasetMembersRequest(mockSshConnection, dsName)
      val listDatasetMembersResponse = datasetsApi.listDatasetMembers(listDatasetMembersRequest)

      if (listDatasetMembersResponse !is SshListDatasetMembersResponse) {
        fail("Should be instance of ${SshListDatasetMembersResponse::class.java.name}")
      } else {
        assertSoftly {
          listDatasetMembersResponse.status.exitStatus shouldBe 0
          listDatasetMembersResponse.memberItems.size shouldBe 4
          listDatasetMembersResponse.memberItems[0].memberName shouldBe "TESTM1"
          listDatasetMembersResponse.memberItems[1].memberName shouldBe "TESTM2"
          listDatasetMembersResponse.memberItems[2].memberName shouldBe "TESTM3"
          listDatasetMembersResponse.memberItems[3].memberName shouldBe "TESTM4"
        }
      }
    }

    should("listDatasetMembers fail with SSH RC=8 as there is no such data set entity") {
      val dsName = "TEST.LISTM2"

      sshMockResponseDispatcher.injectResolver(
        "ssh:listDatasetMembers_fail_no_data_set",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("LISTDS")
          && it.contains("MEMBERS")
          && it.contains(dsName)
        },
        handler = {
          val output = listOf(
            dsName.padEnd(44),
            "IKJ58503I DATA SET '$dsName' NOT IN CATALOG",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 8)
        }
      )

      val listDatasetMembersRequest = SshListDatasetMembersRequest(mockSshConnection, dsName)
      val listDatasetMembersResponse = datasetsApi.listDatasetMembers(listDatasetMembersRequest)

      if (listDatasetMembersResponse !is SshListDatasetMembersResponse) {
        fail("Should be instance of ${SshListDatasetMembersResponse::class.java.name}")
      } else {
        assertSoftly {
          listDatasetMembersResponse.status.exitStatus shouldBe 8
          listDatasetMembersResponse.memberItems.size shouldBe 0
        }
      }
    }

    should("listDatasetMembers fail to return data set members as it is not a PDS / PDSE") {
      val dsName = "TEST.LISTM3"

      sshMockResponseDispatcher.injectResolver(
        "ssh:listDatasets_fail_entity_incorrect",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("LISTDS")
          && it.contains("MEMBERS")
          && it.contains(dsName)
        },
        handler = {
          val output = listOf(
            dsName.padEnd(44),
            "--RECFM-LRECL-BLKSIZE-DSORG",
            "  FB    80    8000    PS".padEnd(114),
            "--VOLUMES--",
            "  VOLUM1",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output)
        }
      )

      val listDatasetMembersRequest = SshListDatasetMembersRequest(mockSshConnection, dsName)
      val listDatasetMembersResponse = datasetsApi.listDatasetMembers(listDatasetMembersRequest)

      if (listDatasetMembersResponse !is SshListDatasetMembersResponse) {
        fail("Should be instance of ${SshListDatasetMembersResponse::class.java.name}")
      } else {
        assertSoftly {
          listDatasetMembersResponse.status.exitStatus shouldBe 8
          listDatasetMembersResponse.status.output shouldContain "NOT A PDS / PDSE"
          listDatasetMembersResponse.memberItems.size shouldBe 0
        }
      }
    }

    should("listDatasetMembers fail to return data set members as there is no members") {
      val dsName = "TEST.LISTM4"

      sshMockResponseDispatcher.injectResolver(
        "ssh:listDatasetMembers_fail_no_members",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("LISTDS")
          && it.contains("MEMBERS")
          && it.contains(dsName)
        },
        handler = {
          val output = listOf(
            dsName.padEnd(44),
            "--RECFM-LRECL-BLKSIZE-DSORG",
            "  FB    80    8000    PO".padEnd(114),
            "--VOLUMES--",
            "  VOLUM1",
            "--MEMBERS--",
            "",
          ).joinToString("\n")
          SshMockCommandResponse(output)
        }
      )

      val listDatasetMembersRequest = SshListDatasetMembersRequest(mockSshConnection, dsName)
      val listDatasetMembersResponse = datasetsApi.listDatasetMembers(listDatasetMembersRequest)

      if (listDatasetMembersResponse !is SshListDatasetMembersResponse) {
        fail("Should be instance of ${SshListDatasetMembersResponse::class.java.name}")
      } else {
        assertSoftly {
          listDatasetMembersResponse.status.exitStatus shouldBe 4
          listDatasetMembersResponse.memberItems.size shouldBe 0
        }
      }
    }

    should("listDatasetMembers fail to return data set members due to some error") {
      val dsName = "TEST.LISTM5"

      sshMockResponseDispatcher.injectResolver(
        "ssh:listDatasetMembers_fail_generic",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("LISTDS")
          && it.contains("MEMBERS")
          && it.contains(dsName)
        },
        handler = {
          val output = listOf(
            dsName.padEnd(44),
            "TESTERR SOME GENERIC ERROR, RC=12",
            "",
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 12)
        }
      )

      val listDatasetMembersRequest = SshListDatasetMembersRequest(mockSshConnection, dsName)
      val listDatasetMembersResponse = datasetsApi.listDatasetMembers(listDatasetMembersRequest)

      if (listDatasetMembersResponse !is SshListDatasetMembersResponse) {
        fail("Should be instance of ${SshListDatasetMembersResponse::class.java.name}")
      } else {
        assertSoftly {
          listDatasetMembersResponse.status.exitStatus shouldBe 12
          listDatasetMembersResponse.memberItems.size shouldBe 0
        }
      }
    }

    should("listDatasetMembers return the correct list of data set members, even when there is some trash at the end of the list") {
      val dsName = "TEST.LISTM6"

      sshMockResponseDispatcher.injectResolver(
        "ssh:listDatasetMembers_success_trash_msg_at_the_end",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("LISTDS")
          && it.contains("MEMBERS")
          && it.contains(dsName)
        },
        handler = {
          val output = listOf(
            dsName.padEnd(44),
            "--RECFM-LRECL-BLKSIZE-DSORG",
            "  FB    80    8000    PO".padEnd(114),
            "--VOLUMES--",
            "  VOLUM1",
            "--MEMBERS--",
            "  TESTM1",
            "  TESTM2",
            "  TESTM3",
            "  TESTM4",
            "IKJ0001I SOME TRASH MESSAGE ACCIDENTALLY APPEARED",
            "",
          ).joinToString("\n")
          SshMockCommandResponse(output)
        }
      )

      val listDatasetMembersRequest = SshListDatasetMembersRequest(mockSshConnection, dsName)
      val listDatasetMembersResponse = datasetsApi.listDatasetMembers(listDatasetMembersRequest)

      if (listDatasetMembersResponse !is SshListDatasetMembersResponse) {
        fail("Should be instance of ${SshListDatasetMembersResponse::class.java.name}")
      } else {
        assertSoftly {
          listDatasetMembersResponse.status.exitStatus shouldBe 0
          listDatasetMembersResponse.memberItems.size shouldBe 4
          listDatasetMembersResponse.memberItems[0].memberName shouldBe "TESTM1"
          listDatasetMembersResponse.memberItems[1].memberName shouldBe "TESTM2"
          listDatasetMembersResponse.memberItems[2].memberName shouldBe "TESTM3"
          listDatasetMembersResponse.memberItems[3].memberName shouldBe "TESTM4"
        }
      }
    }
  }
})
