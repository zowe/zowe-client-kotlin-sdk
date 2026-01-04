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
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.mockHttpConnection
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.zosmfMockResponseDispatcher
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.ZosmfDatasetItem
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfCreateDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfCreateDatasetRequestBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfCreateDatasetResponse

class ZosmfCreateDatasetTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, DatasetsAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("createDataset") {
    should("createDataset successfully create a new PS dataset") {
      val dsName = "CDTEST1"

      zosmfMockResponseDispatcher.injectResolver(
        "createDataset_success_ps",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName") },
        { MockResponse().setResponseCode(201) }
      )

      val createDatasetRequest = ZosmfCreateDatasetRequest(
        mockHttpConnection,
        dsName,
        ZosmfCreateDatasetRequestBody(
          datasetOrganization = ZosmfDatasetItem.ZosmfDatasetOrganization.PS,
          primaryAllocation = 10,
          secondaryAllocation = 2,
          recordFormat = ZosmfDatasetItem.ZosmfRecordFormat.FB,
          allocationUnit = ZosmfCreateDatasetRequestBody.ZosmfAllocationUnit.TRK,
          recordLength = 80,
          blockSize = 8000
        )
      )
      val createDatasetResponse = datasetsApi.createDataset(createDatasetRequest)
        as? ZosmfCreateDatasetResponse
        ?: fail("Should be instance of ${ZosmfCreateDatasetResponse::class.java.name}")

      assertSoftly {
        createDatasetResponse.status.type shouldBe StatusType.SUCCESS
      }
    }

    should("createDataset successfully create a new PDS dataset") {
      val dsName = "CDTEST2"

      zosmfMockResponseDispatcher.injectResolver(
        "createDataset_success_pds",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName") },
        { MockResponse().setResponseCode(201) }
      )

      val createDatasetRequest = ZosmfCreateDatasetRequest(
        mockHttpConnection,
        dsName,
        ZosmfCreateDatasetRequestBody(
          datasetOrganization = ZosmfDatasetItem.ZosmfDatasetOrganization.PO,
          primaryAllocation = 10,
          secondaryAllocation = 2,
          recordFormat = ZosmfDatasetItem.ZosmfRecordFormat.FB,
          allocationUnit = ZosmfCreateDatasetRequestBody.ZosmfAllocationUnit.TRK,
          recordLength = 80,
          blockSize = 8000
        )
      )
      val createDatasetResponse = datasetsApi.createDataset(createDatasetRequest)
        as? ZosmfCreateDatasetResponse
        ?: fail("Should be instance of ${ZosmfCreateDatasetResponse::class.java.name}")

      assertSoftly {
        createDatasetResponse.status.type shouldBe StatusType.SUCCESS
      }
    }

    should("createDataset fail to create a PDS dataset with duplicated name") {
      val dsName = "CDTEST3"

      zosmfMockResponseDispatcher.injectResolver(
        "createDataset_fail_duplicate_name",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName") },
        {
          MockResponse()
            .setBody("{\"category\": 8, \"rc\": -26868, \"reason\": 0, \"message\": \"Dynamic allocation error\"}")
            .setResponseCode(500)
        }
      )

      val createDatasetRequest = ZosmfCreateDatasetRequest(
        mockHttpConnection,
        dsName,
        ZosmfCreateDatasetRequestBody(
          datasetOrganization = ZosmfDatasetItem.ZosmfDatasetOrganization.PO,
          primaryAllocation = 10,
          secondaryAllocation = 2,
          recordFormat = ZosmfDatasetItem.ZosmfRecordFormat.FB,
          allocationUnit = ZosmfCreateDatasetRequestBody.ZosmfAllocationUnit.TRK,
          recordLength = 80,
          blockSize = 8000
        )
      )
      val createDatasetResponse = datasetsApi.createDataset(createDatasetRequest)
        as? ZosmfCreateDatasetResponse
        ?: fail("Should be instance of ${ZosmfCreateDatasetResponse::class.java.name}")

      assertSoftly {
        createDatasetResponse.status.type shouldBe StatusType.ERROR
        createDatasetResponse.status.text shouldContain "Dynamic allocation error"
        createDatasetResponse.status.text shouldContain "500"
      }
    }

    should("createDataset fail to create a dataset due to a client error") {
      val dsName = "CDTEST4"

      zosmfMockResponseDispatcher.injectResolver(
        "createDataset_fail_client_error",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName") },
        { MockResponse().setResponseCode(400) }
      )

      val createDatasetRequest = ZosmfCreateDatasetRequest(
        mockHttpConnection,
        dsName,
        ZosmfCreateDatasetRequestBody(
          datasetOrganization = ZosmfDatasetItem.ZosmfDatasetOrganization.PS,
          primaryAllocation = 10,
          secondaryAllocation = 2,
          recordFormat = ZosmfDatasetItem.ZosmfRecordFormat.FB,
          allocationUnit = ZosmfCreateDatasetRequestBody.ZosmfAllocationUnit.TRK,
          recordLength = 80,
          blockSize = 8000
        )
      )
      val createDatasetResponse = datasetsApi.createDataset(createDatasetRequest)
        as? ZosmfCreateDatasetResponse
        ?: fail("Should be instance of ${ZosmfCreateDatasetResponse::class.java.name}")

      assertSoftly {
        createDatasetResponse.status.type shouldBe StatusType.ERROR
        createDatasetResponse.status.text shouldContain "400"
      }
    }
  }
})