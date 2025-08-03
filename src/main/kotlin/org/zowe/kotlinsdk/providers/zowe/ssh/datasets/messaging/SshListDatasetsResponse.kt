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

package org.zowe.kotlinsdk.providers.zowe.ssh.datasets.messaging

import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetsResponse
import org.zowe.kotlinsdk.core.datasets.data.DatasetItem
import org.zowe.kotlinsdk.providers.zowe.SshResponse

// TODO: doc
class SshListDatasetsResponse(private val sshCmdOutput: String) : SshResponse, ListDatasetsResponse {
  override val dsItems: List<DatasetItem>

  // TODO: doc
  private fun produceDsListFromSshCmdOutput(): List<DatasetItem> {
    val collectedDatasetAttributesStrings = mutableListOf<MutableList<String>>()
    val nextDatasetAttributesStrings = mutableListOf<String>()
    val sshCmdOutputList = sshCmdOutput.split("\n")

    var currSshCmdOutIdx = 0

    while (currSshCmdOutIdx != sshCmdOutputList.size) {
      if (
        nextDatasetAttributesStrings.isEmpty()
        && sshCmdOutputList[currSshCmdOutIdx + 1].matches(Regex("^--.*"))
      ) {

      }
      currSshCmdOutIdx++
    }


    val datasetsRaw = sshCmdOutput
      .split("\n")
      .chunked(5)
      .map { (dsName, attrsHeader, attributes, volsHeader, volumes) ->
        println()
      }

    return listOf()
  }

  init {
    /** Examples to process: */
//tsocmd LISTDS "'TEST.*'"
//LISTDS 'TEST.*'
//TEST.TEST1
//--RECFM-LRECL-BLKSIZE-DSORG
//  VB    12288 12292   PS
//--VOLUMES--
//  VOLUM1
//TEST.TEST2
//--RECFM-LRECL-BLKSIZE-DSORG
//  FB    80    27920   PO
//--VOLUMES--
//  VOLUM1
//TEST.TEST3
//--RECFM-LRECL-BLKSIZE-DSORG
//  U     **    6144    PO
//--VOLUMES--
//  VOLUM1
//TEST.VSAM1
//--LRECL--DSORG-
//  **     VSAM
//--VOLUMES-BLKSIZE
//            **
//TEST.VSAM2
//--LRECL--DSORG-
//  **     VSAM
//--VOLUMES-BLKSIZE
//  VOLUM1    **
//TEST.TEST4
//IKJ58507I DATA SET TEST.TEST4                                NOT ALLOCATED, REQUIRED VOLUME NOT MOUNTED+
//IKJ58507I VOLUME NOT ON SYSTEM AND CANNOT BE ACCESSED
//TEST.GDG1
//--LRECL--DSORG-
//  **     GDG
//--VOLUMES-BLKSIZE
//            **
    dsItems = produceDsListFromSshCmdOutput()
  }
}
