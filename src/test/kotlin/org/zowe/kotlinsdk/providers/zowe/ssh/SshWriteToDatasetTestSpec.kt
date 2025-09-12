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

class SshWriteToDatasetTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.SSH_NATIVE, DatasetsAPI::class.java)

  context("writeToDataset") {
    should("writeToDataset execute successfully creating a new dataset member") {
      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToDataset_pds_mem_success",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("OGET")
          && it.contains("TEST.ALLOC2(TESTMEM1)")
        },
        handler = {
          val output = listOf(
            "TEST.ALLOC2(TESTMEM1)",
            "IKJ58502I DIRECTORY INFORMATION NOT AVAILABLE+",
            "IKJ58502I MEMBER NAME NOT FOUND"
          ).joinToString("\n")
          SshMockCommandResponse(output, exitCode = 4)
        }
      )

      // LISTDS 'TEST.ALLOC2(TESTMEM1)' - must be equal to 4
      // OGET '/dev/null' 'TEST.ALLOC2(TESTMEM1)'
    }

    should("writeToDataset fail cause there is a duplicate data set member") {
      sshMockResponseDispatcher.injectResolver(
        "ssh:writeToDataset_pds_mem_success",
        resolver = {
          it.trim().startsWith("tsocmd")
          && it.contains("OGET")
          && it.contains("TEST.ALLOC2(TESTMEM2)")
        },
        handler = {
          SshMockCommandResponse("")
        }
      )

      // LISTDS 'TEST.ALLOC2(TESTMEM2)' - must be equal to 0
      // OGET '/dev/null' 'TEST.ALLOC2(TESTMEM2)'
    }
  }
})
