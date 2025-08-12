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

package org.zowe.kotlinsdk.providers.zowe.zosmf

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.providers.zowe.HttpRequestRunner
import org.zowe.kotlinsdk.providers.zowe.UserPassHttpConnection
import org.zowe.kotlinsdk.providers.zowe.ZoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfListDatasetsRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfListDatasetsResponse
import java.util.concurrent.TimeUnit

class ZosmfDatasetsAPITestSpec  : StringSpec({
  lateinit var zosmfMockResponseDispatcher: HttpMockResponseDispatcher
  lateinit var zosmfMockServer: MockWebServer
  lateinit var zosmfClient: HttpClient
  lateinit var datasetsApi: DatasetsAPI

  val zosmfServerHost = "127.0.0.1"
  val zosmfServerPort = 49443
  val zosmfUsername = "test"
  val zosmfPassword = "test"

  beforeSpec {
    val localhostCertificate = HeldCertificate.Builder()
      .addSubjectAlternativeName("localhost")
      .addSubjectAlternativeName(zosmfServerHost)
      .duration(60, TimeUnit.MINUTES)
      .build()
    val serverCertificates = HandshakeCertificates.Builder()
      .heldCertificate(localhostCertificate)
      .build()
    zosmfMockServer = MockWebServer()
    zosmfMockResponseDispatcher = HttpMockResponseDispatcher()

    zosmfMockServer.dispatcher = zosmfMockResponseDispatcher
    zosmfMockServer.useHttps(serverCertificates.sslSocketFactory(), false)
    zosmfMockServer.start(zosmfServerPort)

    val clientCertificates = HandshakeCertificates.Builder()
      .addTrustedCertificate(localhostCertificate.certificate)
      .build()

    zosmfClient = HttpClient(CIO) {
      install(ContentNegotiation) {
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

    val zoweAPIProvider = ZoweAPIProvider(listOf(HttpRequestRunner(zosmfClient)))

    datasetsApi = zoweAPIProvider.getApi(DatasetsAPI::class.java)
  }

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
    zosmfMockServer.shutdown()
  }

  "listDatasets should return the correct list of datasets" {
    zosmfMockResponseDispatcher.injectResolver(
      "mock:zosmf/restfiles/ds?dslevel=TEST1.*",
      { it.requestLine.contains("/zosmf/restfiles/ds?dslevel=TEST1") },
      {
        MockResponse()
          .setBody(
            "{\n" +
            "    \"items\": [\n" +
            "        {\n" +
            "            \"dsname\": \"TEST1.TEST1\",\n" +
            "            \"blksz\": \"27920\",\n" +
            "            \"catnm\": \"CATALOG.Z23D.MASTER\",\n" +
            "            \"cdate\": \"2023/08/28\",\n" +
            "            \"dev\": \"3390\",\n" +
            "            \"dsorg\": \"PS\",\n" +
            "            \"edate\": \"***None***\",\n" +
            "            \"extx\": \"1\",\n" +
            "            \"lrecl\": \"80\",\n" +
            "            \"migr\": \"NO\",\n" +
            "            \"mvol\": \"N\",\n" +
            "            \"ovf\": \"NO\",\n" +
            "            \"rdate\": \"2024/10/09\",\n" +
            "            \"recfm\": \"FB\",\n" +
            "            \"sizex\": \"6\",\n" +
            "            \"spacu\": \"TRACKS\",\n" +
            "            \"used\": \"16\",\n" +
            "            \"vol\": \"D3SYS1\",\n" +
            "            \"vols\": \"D3SYS1\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"dsname\": \"TEST1.TEST2\",\n" +
            "            \"blksz\": \"27920\",\n" +
            "            \"catnm\": \"CATALOG.Z23D.MASTER\",\n" +
            "            \"cdate\": \"2023/08/28\",\n" +
            "            \"dev\": \"3390\",\n" +
            "            \"dsorg\": \"PS\",\n" +
            "            \"edate\": \"***None***\",\n" +
            "            \"extx\": \"1\",\n" +
            "            \"lrecl\": \"80\",\n" +
            "            \"migr\": \"NO\",\n" +
            "            \"mvol\": \"N\",\n" +
            "            \"ovf\": \"NO\",\n" +
            "            \"rdate\": \"2024/10/09\",\n" +
            "            \"recfm\": \"FB\",\n" +
            "            \"sizex\": \"6\",\n" +
            "            \"spacu\": \"TRACKS\",\n" +
            "            \"used\": \"16\",\n" +
            "            \"vol\": \"D3SYS1\",\n" +
            "            \"vols\": \"D3SYS1\"\n" +
            "        }" +
            "    ],\n" +
            "    \"returnedRows\": 2,\n" +
            "    \"JSONversion\": 1\n" +
            "}"
          )
          .addHeader("Content-Type", "application/json")
      }
    )

    val listDatasetsRequest = ZosmfListDatasetsRequest(
      UserPassHttpConnection(
        zosmfServerHost,
        zosmfServerPort,
        user=zosmfUsername,
        password=zosmfPassword
      ),
      "TEST1.*"
    )
    val listDatasetsResponse = datasetsApi.listDatasets(listDatasetsRequest)

    assertSoftly {
      listDatasetsResponse is ZosmfListDatasetsResponse
      listDatasetsResponse.dsItems.size shouldBe 2
    }
  }
})
