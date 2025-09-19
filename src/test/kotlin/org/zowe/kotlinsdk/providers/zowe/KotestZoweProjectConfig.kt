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

package org.zowe.kotlinsdk.providers.zowe

import io.kotest.core.config.AbstractProjectConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import net.schmizz.sshj.userauth.method.AuthPassword
import net.schmizz.sshj.userauth.password.PasswordUtils
import okhttp3.mockwebserver.MockWebServer
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import org.apache.sshd.server.Environment
import org.apache.sshd.server.ExitCallback
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.password.AcceptAllPasswordAuthenticator
import org.apache.sshd.server.channel.ChannelSession
import org.apache.sshd.server.command.Command
import org.apache.sshd.server.command.CommandFactory
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.zowe.kotlinsdk.providers.zowe.ssh.SshMockResponseDispatcher
import org.zowe.kotlinsdk.providers.zowe.SshRequestRunner
import org.zowe.kotlinsdk.providers.zowe.zosmf.HttpMockResponseDispatcher
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit

object KotestZoweProjectConfig : AbstractProjectConfig() {
  const val MOCK_SERVER_HOST = "127.0.0.1"
  const val MOCK_SSH_SERVER_PORT = 49022
  const val MOCK_ZOSMF_SERVER_PORT = 49443
  const val MOCK_USERNAME = "test"
  const val MOCK_PASSWORD = "test"

  val sshAuthMethods = listOf(AuthPassword(PasswordUtils.createOneOff(MOCK_PASSWORD.toCharArray())))
  lateinit var sshMockResponseDispatcher: SshMockResponseDispatcher
  lateinit var sshMockServer: SshServer
  lateinit var sshClient: SSHClient

  val mockSshConnection = SshConnection(
    MOCK_SERVER_HOST,
    port = MOCK_SSH_SERVER_PORT,
    username = MOCK_USERNAME,
    authMethods = sshAuthMethods
  )

  lateinit var zosmfMockResponseDispatcher: HttpMockResponseDispatcher
  lateinit var zosmfMockServer: MockWebServer
  lateinit var zosmfClient: HttpClient

  lateinit var zoweAPIProvider: ZoweAPIProvider

  private fun setupSshMockServer() {
    sshMockResponseDispatcher = SshMockResponseDispatcher()
    sshMockServer = SshServer.setUpDefaultServer().apply {
      port = MOCK_SSH_SERVER_PORT
      keyPairProvider = SimpleGeneratorHostKeyProvider()
      passwordAuthenticator = AcceptAllPasswordAuthenticator.INSTANCE
      commandFactory = CommandFactory { _, commandLine ->
        object : Command {
          private lateinit var out: OutputStream
          private lateinit var error: OutputStream
          private lateinit var callback: ExitCallback

          override fun setOutputStream(out: OutputStream) {
            this.out = out
          }

          override fun setErrorStream(err: OutputStream) {
            this.error = err
          }

          override fun setExitCallback(callback: ExitCallback) {
            this.callback = callback
          }

          override fun setInputStream(`in`: InputStream) {}

          override fun start(
            channel: ChannelSession?,
            env: Environment?
          ) {
            val response = sshMockResponseDispatcher.dispatch(commandLine)
            out.write(response.output.toByteArray())
            out.flush()
            error.write(response.error.toByteArray())
            callback.onExit(response.exitCode)
          }

          override fun destroy(channel: ChannelSession?) {}
        }
      }
    }
    sshMockServer.start()

    sshClient = SSHClient()
    sshClient.addHostKeyVerifier(PromiscuousVerifier())
    sshClient.connect(MOCK_SERVER_HOST, MOCK_SSH_SERVER_PORT)
    sshClient.auth(MOCK_USERNAME, sshAuthMethods)
  }

  private fun cleanupSshMockServer() {
    sshClient.disconnect()
    sshMockServer.stop()
    sshMockResponseDispatcher.clearResolvers()
  }

  private fun setupZosmfMockServer() {
    val localhostCertificate = HeldCertificate.Builder()
      .addSubjectAlternativeName("localhost")
      .addSubjectAlternativeName(MOCK_SERVER_HOST)
      .duration(60, TimeUnit.MINUTES)
      .build()
    val serverCertificates = HandshakeCertificates.Builder()
      .heldCertificate(localhostCertificate)
      .build()
    zosmfMockServer = MockWebServer()
    zosmfMockResponseDispatcher = HttpMockResponseDispatcher()

    zosmfMockServer.dispatcher = zosmfMockResponseDispatcher
    zosmfMockServer.useHttps(serverCertificates.sslSocketFactory(), false)
    zosmfMockServer.start(MOCK_ZOSMF_SERVER_PORT)

    val clientCertificates = HandshakeCertificates.Builder()
      .addTrustedCertificate(localhostCertificate.certificate)
      .build()

    zosmfClient = HttpClient(CIO) {
      install(ContentNegotiation.Plugin) {
        json(
          Json {
            ignoreUnknownKeys = true
            prettyPrint = true
          }
        )
      }
      engine {
        https {
          trustManager = clientCertificates.trustManager
        }
      }
    }
  }

  private fun cleanupZosmfMockServer() {
    zosmfMockResponseDispatcher.clearResolvers()
    zosmfMockServer.shutdown()
  }

  override suspend fun beforeProject() {
    setupSshMockServer()
    setupZosmfMockServer()

    zoweAPIProvider = ZoweAPIProvider(listOf(SshRequestRunner(sshClient), HttpRequestRunner(zosmfClient)))
  }

  override suspend fun afterProject() {
    cleanupZosmfMockServer()
    cleanupSshMockServer()
  }
}