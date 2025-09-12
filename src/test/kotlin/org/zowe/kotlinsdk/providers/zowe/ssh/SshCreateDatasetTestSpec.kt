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

import io.kotest.core.spec.style.ShouldSpec
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.sshMockResponseDispatcher
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.zoweAPIProvider
import kotlin.text.contains
import kotlin.text.trim

class SshCreateDatasetTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.SSH_NATIVE, DatasetsAPI::class.java)

  context("createDataset") {
    should("createDataset execute successfully creating a new PS dataset") {
      sshMockResponseDispatcher.injectResolver(
        "ssh:createDataset_ps_success",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("ALLOC")
          && it.contains("TEST.ALLOC1")
        },
        handler = {
          SshMockCommandResponse("")
        }
      )

      // ALLOC DA('TEST.ALLOC1') DSORG(PS) SPACE(2,0) TRACKS LRECL(80) BLKSIZE(8000) RECFM(F,B) NEW
    }

    should("createDataset execute successfully creating a new PDS dataset") {
      sshMockResponseDispatcher.injectResolver(
        "ssh:createDataset_pds_success",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("ALLOC")
          && it.contains("TEST.ALLOC2")
        },
        handler = {
          SshMockCommandResponse("")
        }
      )

      // ALLOC DA('TEST.ALLOC2') DSORG(PO) DIR(2) SPACE(2,0) TRACKS LRECL(80) BLKSIZE(8000) RECFM(F,B) NEW
    }

    should("createDataset fail cause there is a duplicate dataset name") {
      sshMockResponseDispatcher.injectResolver(
        "ssh:createDataset_ps_fail_dupl",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("ALLOC")
          && it.contains("TEST.ALLOC3")
        },
        handler = {
          val output = listOf(
            "IKJ56893I DATA SET TEST.ALLOC3 NOT ALLOCATED+",
            "IGD17101I DATA SET TEST.ALLOC3",
            "NOT DEFINED BECAUSE DUPLICATE NAME EXISTS IN CATALOG",
            "RETURN CODE IS 8 REASON CODE IS 38 IGG0CLEH",
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 12)
        }
      )

      // ALLOC DA('TEST.ALLOC3') DSORG(PS) SPACE(2,0) TRACKS LRECL(80) BLKSIZE(8000) RECFM(F,B) NEW
    }

    should("createDataset fail cause there is a duplicate dataset name, but the error is uncertain") {
      sshMockResponseDispatcher.injectResolver(
        "ssh:createDataset_ps_fail_dupl_uncertain",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("ALLOC")
          && it.contains("TEST.ALLOC4")
        },
        handler = {
          val output = listOf(
            "IKJ56229I DATA SET TEST.ALLOC4 NOT ALLOCATED, CATALOG ERROR+",
            "IKJ56229I DATA SET NAME CONFLICTS WITH EXISTING DATA SET NAME OR USER IS NOT AUTHORIZED TO PERFORM THE OPERATION."
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 12)
        }
      )

      // ALLOC DA('TEST.ALLOC4') DSORG(PS) SPACE(2,0) TRACKS LRECL(80) BLKSIZE(8000) RECFM(F,B) NEW
    }
  }
})
