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

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.tls.HandshakeCertificates
import org.junit.jupiter.api.Test
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfListDatasetsRequest
import java.util.concurrent.TimeUnit
import okhttp3.tls.HeldCertificate
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfListDatasetsResponse

class NewAPIExample {
  private data class ValidationListElem(
    val name: String,
    val validator: (RecordedRequest?) -> Boolean,
    val handler: (RecordedRequest?) -> MockResponse
  )

  private class MockResponseDispatcher : Dispatcher() {

    private var validationList = mutableListOf<ValidationListElem>()

    fun injectEndpoint(
      name: String,
      validator: (RecordedRequest?) -> Boolean,
      handler: (RecordedRequest?) -> MockResponse
    ) {
      validationList.add(ValidationListElem(name, validator, handler))
    }

    fun removeEndpoint(name: String) {
      validationList.removeAll { it.name == name }
    }

    fun removeAllEndpoints() {
      validationList.clear()
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
      println("Request received: ${request.requestLine}")

      val foundValidator = validationList
        .firstOrNull { it.validator(request) }

      return foundValidator
        ?.handler
        ?.let { it(request) }
        ?: MockResponse()
          .setBody("Response is not implemented")
          .setResponseCode(404)
          .addHeader("Content-Type", "application/json")
    }
  }

  @Test
  fun exampleTest() {
    val localhostCertificate = HeldCertificate.Builder()
      .addSubjectAlternativeName("localhost")
      .addSubjectAlternativeName("127.0.0.1")
      .duration(60, TimeUnit.MINUTES)
      .build()
    val serverCertificates = HandshakeCertificates.Builder()
      .heldCertificate(localhostCertificate)
      .build()
    val mockServer = MockWebServer()
    val responseDispatcher = MockResponseDispatcher()
    mockServer.dispatcher = responseDispatcher
    mockServer.useHttps(serverCertificates.sslSocketFactory(), false)
    mockServer.start(49222)

    val clientCertificates = HandshakeCertificates.Builder()
      .addTrustedCertificate(localhostCertificate.certificate)
      .build()

    val ktorClient = HttpClient(CIO) {
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

    responseDispatcher.injectEndpoint(
      "mock:zosmf/restfiles/ds?dslevel=TEST.*",
      { it?.requestLine?.contains("/zosmf/restfiles/ds?dslevel=TEST") ?: false },
      {
        MockResponse()
          .setBody(
            "{\n" +
            "    \"items\": [\n" +
            "        {\n" +
            "            \"dsname\": \"TEST.TEST1\",\n" +
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
            "            \"dsname\": \"TEST.TEST2\",\n" +
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

    val zoweAPIProvider = ZoweAPIProvider(listOf(HttpRequestRunner(ktorClient)))
    val datasetsApi = zoweAPIProvider.getApi(DatasetsAPI::class.java)

    val listDatasetsRequest = ZosmfListDatasetsRequest(
      UserPassConnection("127.0.0.1", "49222", "https", "TEST", "TEST"),
      "TEST.*"
    )
    val listDatasetsResponse = datasetsApi.listDatasets(listDatasetsRequest)
    assert(listDatasetsResponse is ZosmfListDatasetsResponse)
  }
}