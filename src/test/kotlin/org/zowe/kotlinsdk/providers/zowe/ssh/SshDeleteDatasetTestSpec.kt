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
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshDeleteDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.messaging.SshDeleteDatasetResponse
import kotlin.text.contains
import kotlin.text.trim

class SshDeleteDatasetTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.SSH_NATIVE, DatasetsAPI::class.java)

  context("deleteDataset") {
    should("deleteDataset execute successfully deleting a PS data set") {
      val dsName = "TEST.DELETE1"

      sshMockResponseDispatcher.injectResolver(
        "ssh:deleteDataset_ps_success",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("DELETE")
          && it.contains(dsName)
        },
        handler = {
          SshMockCommandResponse("IDC0550I ENTRY (A) $dsName DELETED\n")
        }
      )

      val deleteDatasetRequest = SshDeleteDatasetRequest(mockSshConnection, dsName)
      val deleteDatasetResponse = datasetsApi.deleteDataset(deleteDatasetRequest)

      if (deleteDatasetResponse !is SshDeleteDatasetResponse) {
        fail("Should be instance of ${SshDeleteDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          deleteDatasetResponse.status.exitStatus shouldBe 0
          deleteDatasetResponse.status.output shouldContain "ENTRY"
          deleteDatasetResponse.status.output shouldContain "$dsName DELETED"
        }
      }
    }

    should("deleteDataset execute successfully deleting a PDS member") {
      val dsName = "TEST.DELETE2"
      val memName = "TESTMEM1"

      sshMockResponseDispatcher.injectResolver(
        "ssh:deleteDataset_pds_mem_success",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("DELETE")
          && it.contains("$dsName($memName)")
        },
        handler = {
          SshMockCommandResponse("IDC0549I MEMBER $memName DELETED\n")
        }
      )

      val deleteDatasetRequest = SshDeleteDatasetRequest(mockSshConnection, "$dsName($memName)")
      val deleteDatasetResponse = datasetsApi.deleteDataset(deleteDatasetRequest)

      if (deleteDatasetResponse !is SshDeleteDatasetResponse) {
        fail("Should be instance of ${SshDeleteDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          deleteDatasetResponse.status.exitStatus shouldBe 0
          deleteDatasetResponse.status.output shouldContain "MEMBER $memName DELETED"
        }
      }
    }

    should("deleteDataset fail trying to delete a non-existent member") {
      val dsName = "TEST.DELETE3"
      val memName = "TESTMEM1"

      sshMockResponseDispatcher.injectResolver(
        "ssh:deleteDataset_mem_fail_not_exist",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("DELETE")
          && it.contains("$dsName($memName)")
        },
        handler = {
          val output = listOf(
            "IDC3302I  ACTION ERROR ON $dsName",
            "IDC3330I ** ${memName.padEnd(8)} NOT FOUND",
            "IDC0548I ** MEMBER $memName NOT DELETED",
            "IDC0014I LASTCC=8",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 8)
        }
      )

      val deleteDatasetRequest = SshDeleteDatasetRequest(mockSshConnection, "$dsName($memName)")
      val deleteDatasetResponse = datasetsApi.deleteDataset(deleteDatasetRequest)

      if (deleteDatasetResponse !is SshDeleteDatasetResponse) {
        fail("Should be instance of ${SshDeleteDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          deleteDatasetResponse.status.exitStatus shouldBe 8
          deleteDatasetResponse.status.output shouldContain "MEMBER"
          deleteDatasetResponse.status.output shouldContain "$memName NOT DELETED"
        }
      }
    }

    should("deleteDataset fail trying to delete a non-existent data set") {
      val dsName = "TEST.DELETE4"

      sshMockResponseDispatcher.injectResolver(
        "ssh:deleteDataset_ds_fail_not_exist",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("DELETE")
          && it.contains(dsName)
        },
        handler = {
          val output = listOf(
            "IDC3012I ENTRY $dsName NOT FOUND+",
            "IDC3009I ** VSAM CATALOG RETURN CODE IS 8 - REASON CODE IS IGG0CLEG-42",
            "IDC0551I ** ENTRY $dsName NOT DELETED",
            "IDC0014I LASTCC=8",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 8)
        }
      )

      val deleteDatasetRequest = SshDeleteDatasetRequest(mockSshConnection, dsName)
      val deleteDatasetResponse = datasetsApi.deleteDataset(deleteDatasetRequest)

      if (deleteDatasetResponse !is SshDeleteDatasetResponse) {
        fail("Should be instance of ${SshDeleteDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          deleteDatasetResponse.status.exitStatus shouldBe 8
          deleteDatasetResponse.status.output shouldContain "ENTRY $dsName NOT FOUND"
        }
      }
    }

    should("deleteDataset execute successfully with an unexpected warning") {
      val dsName = "TEST.DELETE5"

      sshMockResponseDispatcher.injectResolver(
        "ssh:deleteDataset_ds_ggeneric_warn",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("DELETE")
          && it.contains(dsName)
        },
        handler = {
          val output = listOf(
            "IDC3012I ENTRY (A) $dsName DELETED, BUT THERE WAS SOME UNEXPECTED WARN",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 4)
        }
      )

      val deleteDatasetRequest = SshDeleteDatasetRequest(mockSshConnection, dsName)
      val deleteDatasetResponse = datasetsApi.deleteDataset(deleteDatasetRequest)

      if (deleteDatasetResponse !is SshDeleteDatasetResponse) {
        fail("Should be instance of ${SshDeleteDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          deleteDatasetResponse.status.exitStatus shouldBe 4
          deleteDatasetResponse.status.output shouldContain "ENTRY (A) $dsName DELETED"
          deleteDatasetResponse.status.output shouldContain "WARN"
        }
      }
    }

    should("deleteDataset fail with an unexpected error (RC=8)") {
      val dsName = "TEST.DELETE6"

      sshMockResponseDispatcher.injectResolver(
        "ssh:deleteDataset_ds_generic_fail",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("DELETE")
          && it.contains(dsName)
        },
        handler = {
          val output = listOf(
            "IDC3012I ENTRY $dsName NOT DELETED DUE TO SOME GENERIC ERROR",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 8)
        }
      )

      val deleteDatasetRequest = SshDeleteDatasetRequest(mockSshConnection, dsName)
      val deleteDatasetResponse = datasetsApi.deleteDataset(deleteDatasetRequest)

      if (deleteDatasetResponse !is SshDeleteDatasetResponse) {
        fail("Should be instance of ${SshDeleteDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          deleteDatasetResponse.status.exitStatus shouldBe 8
          deleteDatasetResponse.status.output shouldContain "NOT DELETED"
        }
      }
    }

    should("deleteDataset fail with an unexpected error (RC=12)") {
      val dsName = "TEST.DELETE7"

      sshMockResponseDispatcher.injectResolver(
        "ssh:deleteDataset_ds_fail_unsucc_processing",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("DELETE")
          && it.contains(dsName)
        },
        handler = {
          val output = listOf(
            "IDC3012I ENTRY $dsName NOT DELETED, PROCESSING UNSUCCESSFUL",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 12)
        }
      )

      val deleteDatasetRequest = SshDeleteDatasetRequest(mockSshConnection, dsName)
      val deleteDatasetResponse = datasetsApi.deleteDataset(deleteDatasetRequest)

      if (deleteDatasetResponse !is SshDeleteDatasetResponse) {
        fail("Should be instance of ${SshDeleteDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          deleteDatasetResponse.status.exitStatus shouldBe 12
          deleteDatasetResponse.status.output shouldContain "NOT DELETED"
        }
      }
    }

    should("deleteDataset fail with an unexpected severe problem (RC=16)") {
      val dsName = "TEST.DELETE8"

      sshMockResponseDispatcher.injectResolver(
        "ssh:deleteDataset_ds_fail_severe",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("DELETE")
          && it.contains(dsName)
        },
        handler = {
          val output = listOf(
            "IDC3012I ENTRY $dsName NOT DELETED, SEVERE ERROR OCCURRED",
            ""
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 16)
        }
      )

      val deleteDatasetRequest = SshDeleteDatasetRequest(mockSshConnection, dsName)
      val deleteDatasetResponse = datasetsApi.deleteDataset(deleteDatasetRequest)

      if (deleteDatasetResponse !is SshDeleteDatasetResponse) {
        fail("Should be instance of ${SshDeleteDatasetResponse::class.java.name}")
      } else {
        assertSoftly {
          deleteDatasetResponse.status.exitStatus shouldBe 16
          deleteDatasetResponse.status.output shouldContain "NOT DELETED"
        }
      }
    }
  }
})
