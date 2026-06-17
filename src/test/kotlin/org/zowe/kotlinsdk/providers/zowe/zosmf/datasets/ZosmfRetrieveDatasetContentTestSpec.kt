/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import okhttp3.mockwebserver.MockResponse
import org.zowe.kotlinsdk.core.DataType
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.core.ChanneledRequest
import io.kotest.provided.ProjectConfig.mockHttpConnection
import io.kotest.provided.ProjectConfig.zosmfMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.zosmf.XIBMDataType
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfRetrieveDatasetContentRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfRetrieveDatasetContentRequestHeaders
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfRetrieveDatasetContentResponse

class ZosmfRetrieveDatasetContentTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, DatasetsAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("retrieveDatasetContent") {
    should("retrieveDatasetContent return the PS data set content") {
      val dsName = "RDCTEST1"

      zosmfMockResponseDispatcher.injectResolver(
        "retrieveDatasetContent_success_ps",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName") },
        {
          MockResponse()
            .setBody("Test content")
            .addHeader("Content-Type", "text/plain")
        }
      )

      val retrieveDatasetContentRequest = ZosmfRetrieveDatasetContentRequest(mockHttpConnection, dsName)
      val retrieveDatasetContentResponse = datasetsApi.retrieveDatasetContent(retrieveDatasetContentRequest)
        as? ZosmfRetrieveDatasetContentResponse
        ?: fail("Should be instance of ${ZosmfRetrieveDatasetContentResponse::class.java.name}")

      assertSoftly {
        retrieveDatasetContentResponse.status.type shouldBe StatusType.SUCCESS
        retrieveDatasetContentResponse.fetchedDataType shouldBe DataType.TEXT
        retrieveDatasetContentResponse.fetchedText shouldContain "Test content"
      }
    }

    should("retrieveDatasetContent execute successfully retrieving a PDS data set member content") {
      val dsName = "RDCTEST2"
      val memName = "RDCMEM2"

      zosmfMockResponseDispatcher.injectResolver(
        "retrieveDatasetContent_success_pds_mem",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName($memName)") },
        {
          MockResponse()
            .setBody(
              "Test content\n" +
              "Test next line\n" +
              "This is a member content\n" +
              ""
            )
            .addHeader("Content-Type", "text/plain")
        }
      )

      val retrieveDatasetContentRequest = ZosmfRetrieveDatasetContentRequest(mockHttpConnection, dsName, memName)
      val retrieveDatasetContentResponse = datasetsApi.retrieveDatasetContent(retrieveDatasetContentRequest)
        as? ZosmfRetrieveDatasetContentResponse
        ?: fail("Should be instance of ${ZosmfRetrieveDatasetContentResponse::class.java.name}")

      assertSoftly {
        retrieveDatasetContentResponse.status.type shouldBe StatusType.SUCCESS
        retrieveDatasetContentResponse.fetchedDataType shouldBe DataType.TEXT
        retrieveDatasetContentResponse.fetchedText shouldContain "member content"
      }
    }

    should("retrieveDatasetContent execute successfully retrieving an empty PS data set") {
      val dsName = "RDCTEST3"

      zosmfMockResponseDispatcher.injectResolver(
        "retrieveDatasetContent_success_ps_empty",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName") },
        {
          MockResponse()
            .setBody("")
            .addHeader("Content-Type", "text/plain")
        }
      )

      val retrieveDatasetContentRequest = ZosmfRetrieveDatasetContentRequest(mockHttpConnection, dsName)
      val retrieveDatasetContentResponse = datasetsApi.retrieveDatasetContent(retrieveDatasetContentRequest)
        as? ZosmfRetrieveDatasetContentResponse
        ?: fail("Should be instance of ${ZosmfRetrieveDatasetContentResponse::class.java.name}")

      assertSoftly {
        retrieveDatasetContentResponse.status.type shouldBe StatusType.SUCCESS
        retrieveDatasetContentResponse.fetchedDataType shouldBe DataType.TEXT
        retrieveDatasetContentResponse.fetchedText shouldBe ""
      }
    }

    should("retrieveDatasetContent fail because there is no entity found") {
      val dsName = "RDCTEST4"

      zosmfMockResponseDispatcher.injectResolver(
        "retrieveDatasetContent_fail_not_found",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName") },
        { MockResponse().setResponseCode(404) }
      )

      val retrieveDatasetContentRequest = ZosmfRetrieveDatasetContentRequest(mockHttpConnection, dsName)
      val retrieveDatasetContentResponse = datasetsApi.retrieveDatasetContent(retrieveDatasetContentRequest)
        as? ZosmfRetrieveDatasetContentResponse
        ?: fail("Should be instance of ${ZosmfRetrieveDatasetContentResponse::class.java.name}")

      assertSoftly {
        retrieveDatasetContentResponse.status.type shouldBe StatusType.ERROR
        retrieveDatasetContentResponse.fetchedDataType shouldBe DataType.ERROR
        retrieveDatasetContentResponse.status.text shouldContain "404"
      }
    }

    should("retrieveDatasetContent fail due to some server error") {
      val dsName = "RDCTEST5"

      zosmfMockResponseDispatcher.injectResolver(
        "retrieveDatasetContent_fail_server_error",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName") },
        {
          MockResponse()
            .setBody(
              "\"category\":6," +
              "\"rc\":8," +
              "\"reason\":201," +
              "\"message\":\"fopen() failed\"," +
              "\"stack\":\"\"," +
              "\"details\":[\"Data set is busy\"]"
            )
            .setResponseCode(500)
        }
      )

      val retrieveDatasetContentRequest = ZosmfRetrieveDatasetContentRequest(mockHttpConnection, dsName)
      val retrieveDatasetContentResponse = datasetsApi.retrieveDatasetContent(retrieveDatasetContentRequest)
        as? ZosmfRetrieveDatasetContentResponse
        ?: fail("Should be instance of ${ZosmfRetrieveDatasetContentResponse::class.java.name}")

      assertSoftly {
        retrieveDatasetContentResponse.status.type shouldBe StatusType.ERROR
        retrieveDatasetContentResponse.fetchedDataType shouldBe DataType.ERROR
        retrieveDatasetContentResponse.status.text shouldContain "500"
        retrieveDatasetContentResponse.status.text shouldContain "Data set is busy"
      }
    }

    should("retrieveDatasetContent get a PDS member content in binary format") {
      val dsName = "RDCTEST6"
      val memName = "RDCMEM6"

      zosmfMockResponseDispatcher.injectResolver(
        "retrieveDatasetContent_success_pds_mem_binary",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName($memName)") },
        {
          val bodyToReturn = "Test binary content\nof a PDS member"
          MockResponse()
            .setChunkedBody(
              bodyToReturn,
              ChanneledRequest.DEFAULT_CHANNEL_SIZE
            )
            .addHeader("Content-Type", "application/octet-stream")
            .addHeader("Content-Length", bodyToReturn.length)
        }
      )

      val retrieveDatasetContentRequest = ZosmfRetrieveDatasetContentRequest(
        mockHttpConnection,
        dsName,
        memName,
        headers = ZosmfRetrieveDatasetContentRequestHeaders(xIBMDataType = XIBMDataType(XIBMDataType.Type.BINARY)),
      )
      val retrieveDatasetContentResponse = datasetsApi.retrieveDatasetContent(retrieveDatasetContentRequest)
        as? ZosmfRetrieveDatasetContentResponse
        ?: fail("Should be instance of ${ZosmfRetrieveDatasetContentResponse::class.java.name}")

      val readChunks = retrieveDatasetContentResponse.readAsIs()
      assertSoftly {
        retrieveDatasetContentResponse.status.type shouldBe StatusType.SUCCESS
        retrieveDatasetContentResponse.fetchedDataType shouldBe DataType.BINARY
        readChunks.joinToString { String(it) } shouldContain "PDS member"
      }
    }
  }
})