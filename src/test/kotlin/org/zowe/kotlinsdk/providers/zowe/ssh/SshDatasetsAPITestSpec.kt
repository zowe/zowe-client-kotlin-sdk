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
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.StringSpec
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import net.schmizz.sshj.userauth.method.AuthPassword
import net.schmizz.sshj.userauth.password.PasswordUtils
import org.apache.sshd.server.Environment
import org.apache.sshd.server.ExitCallback
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.password.AcceptAllPasswordAuthenticator
import org.apache.sshd.server.command.Command
import org.apache.sshd.server.command.CommandFactory
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.core.datasets.data.DatasetItem
import org.zowe.kotlinsdk.providers.zowe.SshConnection
import org.zowe.kotlinsdk.providers.zowe.SshRequestRunner
import org.zowe.kotlinsdk.providers.zowe.ZoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.ssh.datasets.messaging.SshListDatasetsRequest
import org.zowe.kotlinsdk.providers.zowe.ssh.datasets.messaging.SshListDatasetsResponse
import java.io.InputStream
import java.io.OutputStream
import kotlin.text.contains
import kotlin.text.trim

class SshDatasetsAPITestSpec : StringSpec({
  lateinit var sshMockResponseDispatcher: SshMockResponseDispatcher
  lateinit var sshMockServer: SshServer
  lateinit var sshClient: SSHClient
  lateinit var datasetsApi: DatasetsAPI

  val sshServerHost = "127.0.0.1"
  val sshServerPort = 49022
  val sshUsername = "test"
  val sshAuthMethods = listOf(AuthPassword(PasswordUtils.createOneOff("TEST".toCharArray())))

  beforeSpec {
    sshMockResponseDispatcher = SshMockResponseDispatcher()
    sshMockServer = SshServer.setUpDefaultServer().apply {
      port = sshServerPort
      keyPairProvider = SimpleGeneratorHostKeyProvider()
      passwordAuthenticator = AcceptAllPasswordAuthenticator.INSTANCE
      commandFactory = CommandFactory { _, commandLine ->
        object : Command {
          private lateinit var out: OutputStream
          private lateinit var callback: ExitCallback

          override fun setOutputStream(out: OutputStream) {
            this.out = out
          }
          override fun setErrorStream(err: OutputStream) {}

          override fun setExitCallback(callback: ExitCallback) {
            this.callback = callback
          }

          override fun setInputStream(`in`: InputStream) {}

          override fun start(
            channel: org.apache.sshd.server.channel.ChannelSession?,
            env: Environment?
          ) {
            val response = sshMockResponseDispatcher.dispatch(commandLine)
            out.write(response.toByteArray())
            out.flush()
            callback.onExit(0)
          }

          override fun destroy(channel: org.apache.sshd.server.channel.ChannelSession?) {}
        }
      }
    }
    sshMockServer.start()

    sshClient = SSHClient()
    sshClient.addHostKeyVerifier(PromiscuousVerifier())

    val zoweAPIProvider = ZoweAPIProvider(listOf(SshRequestRunner(sshClient)))

    datasetsApi = zoweAPIProvider.getApi(DatasetsAPI::class.java)
  }

  afterSpec {
    sshMockServer.stop()
    sshMockResponseDispatcher.clearResolvers()
  }

  "listDatasets should return the correct list of datasets" {
    sshMockResponseDispatcher.injectResolver(
      "ssh:listDatasets_success",
      resolver = {
        it != null
        && it.trim().startsWith("tsocmd")
        && it.contains("LISTDS")
        && it.contains("TEST1")
      },
      handler = {
        listOf(
          "TEST1.TEST1".padEnd(44),
          "--RECFM-LRECL-BLKSIZE-DSORG",
          "  VB    12288 12292   PS".padEnd(114),
          "--VOLUMES--",
          "  VOLUM1",
          "--FORMAT 1 DSCB--",
          "F1 E5D6D3E4D4F1 0001 7C00C2 000000 01 00 00 C9C2D4D6E2E5E2F24040404040",
          "7D001180000000 4000 50 00 03E8 0064 00 0000 82 80000000 000001 E2FA 0000",
          "010000A9000600A90006 00000000000000000000 00000000000000000000 0000000000",
          "TEST1.TEST2".padEnd(44),
          "--RECFM-LRECL-BLKSIZE-DSORG",
          "  FB    80    6160    PO".padEnd(114),
          "--VOLUMES--",
          "  VOLUM1",
          "--FORMAT 1 DSCB--",
          "F1 E5D6D3E4D4F1 0001 7C00C2 000000 04 00 00 C9C2D4D6E2E5E2F24040404040",
          "7D002600801810 0200 90 00 1810 0050 00 0000 80 5000004F 003D03 D706 0000",
          "01001C1200001C140009 01011C14000A1C150004 01021C1500051C15000E 0000000405",
          "--FORMAT 3 DSCB--",
          "03030303 01031C1600001C160009 00000000000000000000 00000000000000000000",
          "00000000000000000000 F3 00000000000000000000 00000000000000000000",
          "00000000000000000000 00000000000000000000 00000000000000000000",
          "00000000000000000000 00000000000000000000 00000000000000000000",
          "00000000000000000000 0000000000",
          "TEST1.TEST2.F".padEnd(44),
          "--RECFM-LRECL-BLKSIZE-DSORG",
          "  F     80    27920   PO".padEnd(114),
          "--VOLUMES------",
          "  VOLUM1,VOLUM2",
          "--FORMAT 1 DSCB--",
          "F1 E5D6D3E4D4F1 0001 7C00C2 000000 04 00 00 C9C2D4D6E2E5E2F24040404040",
          "7D002600801810 0200 80 00 1810 0050 00 0000 80 C000004F 003D03 D706 0000",
          "01001C1200001C140009 01011C14000A1C150004 01021C1500051C15000E 0000000405",
          "TEST1.TEST2.V".padEnd(44),
          "--RECFM-LRECL-BLKSIZE-DSORG",
          "  V     80    27920   PS".padEnd(114),
          "--VOLUMES--",
          "  VOLUM1",
          "--FORMAT 1 DSCB--",
          "F1 E5D6D3E4D4F1 0001 7C00C2 000000 04 00 00 C9C2D4D6E2E5E2F24040404040",
          "7D002600801810 0200 40 00 1810 0050 00 0000 80 5000004F 003D03 D706 0000",
          "01001C1200001C140009 01011C14000A1C150004 01021C1500051C15000E 0000000405",
          "TEST1.TEST3".padEnd(44),
          "--RECFM-LRECL-BLKSIZE-DSORG",
          "  U     **    6144    PO".padEnd(114),
          "--VOLUMES--",
          "  VOLUM1",
          "--FORMAT 1 DSCB--",
          "F1 E5D6D3E4D4F1 0001 7C00C2 000000 04 00 00 C9C2D4D6E2E5E2F24040404040",
          "7D002600801810 0200 C0 00 1810 0050 00 0000 80 5000004F 003D03 D706 0000",
          "01001C1200001C140009 01011C14000A1C150004 01021C1500051C15000E 0000000405",
          "TEST1.VSAM1".padEnd(44),
          "--LRECL--DSORG",
          "  **     VSAM".padEnd(114),
          "--VOLUMES-BLKSIZE",
          "            **".padEnd(114),
          "TEST1.VSAM.TEST2".padEnd(44),
          "--LRECL--DSORG-",
          "  **     VSAM".padEnd(114),
          "--VOLUMES-BLKSIZE",
          "  VOLUM1     **".padEnd(114),
          "TEST1.TEST4".padEnd(44),
          "IKJ58507I DATA SET ${"TEST1.TEST4".padEnd(44)} NOT ALLOCATED, REQUIRED VOLUME NOT MOUNTED+".padEnd(114),
          "IKJ58507I VOLUME NOT ON SYSTEM AND CANNOT BE ACCESSED".padEnd(114),
          "TEST1.GDG1".padEnd(44),
          "--LRECL--DSORG-",
          "  **     GDG".padEnd(114),
          "--VOLUMES-BLKSIZE",
          "            **".padEnd(114),
          "",
        ).joinToString("\n")
      }
    )

    val listDatasetsRequest = SshListDatasetsRequest(
      SshConnection(
        sshServerHost,
        port = sshServerPort,
        username = sshUsername,
        authMethods = sshAuthMethods
      ),
      "TEST1.*"
    )

    val listDatasetsResponse = datasetsApi.listDatasets(listDatasetsRequest)

    assertSoftly {
      (listDatasetsResponse is SshListDatasetsResponse) shouldBe true
      listDatasetsResponse.dsItems.size shouldBe 8
      listDatasetsResponse.dsItems[0].datasetName shouldBe "TEST1.TEST1"
      listDatasetsResponse.dsItems[0].sizeInTracks shouldBe 1
      listDatasetsResponse.dsItems[0].spaceUnits shouldBe DatasetItem.SpaceUnits.TRACKS
      listDatasetsResponse.dsItems[1].datasetOrganization shouldBe DatasetItem.DatasetOrganization.PO
      listDatasetsResponse.dsItems[1].sizeInTracks shouldBe 70
      listDatasetsResponse.dsItems[1].spaceUnits shouldBe DatasetItem.SpaceUnits.BLOCKS
      listDatasetsResponse.dsItems[2].blockSize shouldBe 27920
      listDatasetsResponse.dsItems[2].volumeSerial shouldBe "VOLUM1,VOLUM2"
      listDatasetsResponse.dsItems[2].sizeInTracks shouldBe 60
      listDatasetsResponse.dsItems[2].spaceUnits shouldBe DatasetItem.SpaceUnits.CYLINDERS
      listDatasetsResponse.dsItems[3].recordLength shouldBe 80
      listDatasetsResponse.dsItems[4].recordFormat shouldBe DatasetItem.RecordFormat.U
      listDatasetsResponse.dsItems[5].volumeSerial shouldBe null
      listDatasetsResponse.dsItems[6].recordFormat shouldBe DatasetItem.RecordFormat.VSAM
      listDatasetsResponse.dsItems[7].datasetName shouldBe "TEST1.GDG1"
    }
  }
})
