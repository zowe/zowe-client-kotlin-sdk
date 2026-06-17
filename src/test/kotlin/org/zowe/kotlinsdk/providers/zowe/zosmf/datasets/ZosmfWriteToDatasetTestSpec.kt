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
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.core.datasets.api.messaging.WriteToDatasetRequest
import io.kotest.provided.ProjectConfig.mockHttpConnection
import io.kotest.provided.ProjectConfig.zosmfMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfWriteToDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfWriteToDatasetResponse

class ZosmfWriteToDatasetTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, DatasetsAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("writeToDataset") {
    should("writeToDataset successfully writes content to a PS data set") {
      val dsName = "WDTEST1"
      val testContent = "Hello World!".toByteArray()

      zosmfMockResponseDispatcher.injectResolver(
        "writeToDataset_success_ps",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName") },
        { MockResponse().setResponseCode(204) }
      )

      val writeToDatasetRequest = ZosmfWriteToDatasetRequest(mockHttpConnection, dsName, testContent)
      val writeToDatasetResponse = datasetsApi.writeToDataset(writeToDatasetRequest)
        as? ZosmfWriteToDatasetResponse
        ?: fail("Should be instance of ${ZosmfWriteToDatasetResponse::class.java.name}")

      assertSoftly { writeToDatasetResponse.status.type shouldBe StatusType.SUCCESS }
    }

    should("writeToDataset successfully writes binary content to a PS data set") {
      val dsName = "WDTEST2"
      val testContent = "Hello World".toByteArray() +
        byteArrayOf(0x00.toByte(), 0xFF.toByte(), 0x01.toByte(), 0x7F.toByte())

      zosmfMockResponseDispatcher.injectResolver(
        "writeToDataset_success_ps_binary",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName") },
        { MockResponse().setResponseCode(204) }
      )

      val writeToDatasetRequest = ZosmfWriteToDatasetRequest(
        mockHttpConnection,
        dsName,
        testContent,
        contentType = WriteToDatasetRequest.ContentType.BINARY
      )
      val writeToDatasetResponse = datasetsApi.writeToDataset(writeToDatasetRequest)
        as? ZosmfWriteToDatasetResponse
        ?: fail("Should be instance of ${ZosmfWriteToDatasetResponse::class.java.name}")

      assertSoftly { writeToDatasetResponse.status.type shouldBe StatusType.SUCCESS }
    }

    should("writeToDataset successfully writes content to a PDS data set member") {
      val dsName = "WDTEST3"
      val memName = "TESTMEM1"
      val testContent = "Hello World!".toByteArray()

      zosmfMockResponseDispatcher.injectResolver(
        "writeToDataset_success_pds_mem",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName($memName)") },
        { MockResponse().setResponseCode(204) }
      )

      val writeToDatasetRequest = ZosmfWriteToDatasetRequest(
        mockHttpConnection,
        "$dsName($memName)",
        testContent
      )
      val writeToDatasetResponse = datasetsApi.writeToDataset(writeToDatasetRequest)
        as? ZosmfWriteToDatasetResponse
        ?: fail("Should be instance of ${ZosmfWriteToDatasetResponse::class.java.name}")

      assertSoftly { writeToDatasetResponse.status.type shouldBe StatusType.SUCCESS }
    }

    should("writeToDataset successfully creates a PDS data set member with the provided content") {
      val dsName = "WDTEST4"
      val memName = "TESTMEM2"
      val testContent = "Hello World!".toByteArray()

      zosmfMockResponseDispatcher.injectResolver(
        "writeToDataset_success_pds_mem_create",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName($memName)") },
        { MockResponse().setResponseCode(201) }
      )

      val writeToDatasetRequest = ZosmfWriteToDatasetRequest(
        mockHttpConnection,
        "$dsName($memName)",
        testContent
      )
      val writeToDatasetResponse = datasetsApi.writeToDataset(writeToDatasetRequest)
        as? ZosmfWriteToDatasetResponse
        ?: fail("Should be instance of ${ZosmfWriteToDatasetResponse::class.java.name}")

      assertSoftly { writeToDatasetResponse.status.type shouldBe StatusType.SUCCESS }
    }

    should("writeToDataset produce warning because the HTTP code is not in the list of the correct ones") {
      val dsName = "WDTEST5"
      val testContent = "Hello World!".toByteArray()

      zosmfMockResponseDispatcher.injectResolver(
        "writeToDataset_warn_unexpected_success_code",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName") },
        { MockResponse().setResponseCode(200) }
      )

      val writeToDatasetRequest = ZosmfWriteToDatasetRequest(mockHttpConnection, dsName, testContent)
      val writeToDatasetResponse = datasetsApi.writeToDataset(writeToDatasetRequest)
        as? ZosmfWriteToDatasetResponse
        ?: fail("Should be instance of ${ZosmfWriteToDatasetResponse::class.java.name}")

      assertSoftly { writeToDatasetResponse.status.type shouldBe StatusType.WARNING }
    }

    should("writeToDataset fail to write content to a PS data set because it does not exist") {
      val dsName = "WDTEST6"
      val testContent = "Hello World!".toByteArray()

      zosmfMockResponseDispatcher.injectResolver(
        "writeToDataset_fail_ds_not_exist",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":6," +
                "\"rc\":8," +
                "\"reason\":386400778," +
                "\"message\":\"Data set not found.\"," +
                "\"details\":[\"IKJ56228I DATA SET $dsName NOT IN CATALOG OR CATALOG CAN NOT BE ACCESSED    \"]" +
              "}"
            )
            .setResponseCode(404)
        }
      )

      val writeToDatasetRequest = ZosmfWriteToDatasetRequest(mockHttpConnection, dsName, testContent)
      val writeToDatasetResponse = datasetsApi.writeToDataset(writeToDatasetRequest)
        as? ZosmfWriteToDatasetResponse
        ?: fail("Should be instance of ${ZosmfWriteToDatasetResponse::class.java.name}")

      assertSoftly {
        writeToDatasetResponse.status.type shouldBe StatusType.ERROR
        writeToDatasetResponse.status.text shouldContain "Data set not found."
      }
    }

    should("writeToDataset fail to write content due to some internal server error") {
      val dsName = "WDTEST7"
      val testContent = "Hello World!".toByteArray()

      zosmfMockResponseDispatcher.injectResolver(
        "writeToDataset_fail_internal_server_error",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName") },
        { MockResponse().setResponseCode(500) }
      )

      val writeToDatasetRequest = ZosmfWriteToDatasetRequest(mockHttpConnection, dsName, testContent)
      val writeToDatasetResponse = datasetsApi.writeToDataset(writeToDatasetRequest)
        as? ZosmfWriteToDatasetResponse
        ?: fail("Should be instance of ${ZosmfWriteToDatasetResponse::class.java.name}")

      assertSoftly { writeToDatasetResponse.status.type shouldBe StatusType.ERROR }
    }
  }
})