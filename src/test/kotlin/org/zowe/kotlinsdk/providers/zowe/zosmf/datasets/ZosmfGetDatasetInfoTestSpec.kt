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
import io.kotest.provided.ProjectConfig.mockHttpConnection
import io.kotest.provided.ProjectConfig.zosmfMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfGetDatasetInfoRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfGetDatasetInfoResponse

class ZosmfGetDatasetInfoTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, DatasetsAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("getDatasetInfo") {
    should("getDatasetInfo return the data set instance") {
      val dsMaskTest = "GDITEST1"

      zosmfMockResponseDispatcher.injectResolver(
        "getDatasetInfo_success",
        { it.requestLine.contains("/zosmf/restfiles/ds?dslevel=$dsMaskTest") },
        {
          MockResponse()
            .setBody(
              "{\n" +
              "  \"items\": [\n" +
              "    {\n" +
              "      \"dsname\": \"$dsMaskTest\",\n" +
              "      \"blksz\": \"27920\",\n" +
              "      \"catnm\": \"CATALOG.Z23D.MASTER\",\n" +
              "      \"cdate\": \"2023/08/28\",\n" +
              "      \"dev\": \"3390\",\n" +
              "      \"dsorg\": \"PS\",\n" +
              "      \"edate\": \"***None***\",\n" +
              "      \"extx\": \"1\",\n" +
              "      \"lrecl\": \"80\",\n" +
              "      \"migr\": \"NO\",\n" +
              "      \"mvol\": \"N\",\n" +
              "      \"ovf\": \"NO\",\n" +
              "      \"rdate\": \"2024/10/09\",\n" +
              "      \"recfm\": \"FB\",\n" +
              "      \"sizex\": \"6\",\n" +
              "      \"spacu\": \"TRACKS\",\n" +
              "      \"used\": \"16\",\n" +
              "      \"vol\": \"D3SYS1\",\n" +
              "      \"vols\": \"D3SYS1\"\n" +
              "    }\n" +
              "  ],\n" +
              "  \"returnedRows\": 1,\n" +
              "  \"JSONversion\": 1\n" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
        }
      )

      val getDatasetInfoRequest = ZosmfGetDatasetInfoRequest(mockHttpConnection, dsMaskTest)
      val getDatasetInfoResponse = datasetsApi.getDatasetInfo(getDatasetInfoRequest)
        as? ZosmfGetDatasetInfoResponse
        ?: fail("Should be instance of ${ZosmfGetDatasetInfoResponse::class.java.name}")

      assertSoftly {
        getDatasetInfoResponse.status.type shouldBe StatusType.SUCCESS
        getDatasetInfoResponse.dataset.datasetName shouldBe dsMaskTest
        getDatasetInfoResponse.dataset.recordLength shouldBe 80
      }
    }

    should("getDatasetInfo return an error status when the data set is not found") {
      val dsMaskTest = "GDITEST2"

      zosmfMockResponseDispatcher.injectResolver(
        "getDatasetInfo_fail_no_dataset",
        { it.requestLine.contains("/zosmf/restfiles/ds?dslevel=$dsMaskTest") },
        {
          MockResponse()
            .setBody("{\"items\": [],\"returnedRows\": 0,\"JSONversion\": 1}")
            .addHeader("Content-Type", "application/json")
        }
      )

      val getDatasetInfoRequest = ZosmfGetDatasetInfoRequest(mockHttpConnection, dsMaskTest)
      val getDatasetInfoResponse = datasetsApi.getDatasetInfo(getDatasetInfoRequest)
        as? ZosmfGetDatasetInfoResponse
        ?: fail("Should be instance of ${ZosmfGetDatasetInfoResponse::class.java.name}")

      assertSoftly {
        getDatasetInfoResponse.status.type shouldBe StatusType.ERROR
        getDatasetInfoResponse.status.text shouldContain "404"
      }
    }

    should("getDatasetInfo return an error status when the listDataset request returns an error") {
      val dsMaskTest = "GDITEST3"

      zosmfMockResponseDispatcher.injectResolver(
        "getDatasetInfo_fail_listDatasets_error",
        { it.requestLine.contains("/zosmf/restfiles/ds?dslevel=$dsMaskTest") },
        {
          MockResponse()
            .setBody(
              "{\"category\": 16,\"rc\": 16,\"reason\": 1,\"message\": \"Server error occurred\",\"details\":[]}"
            )
            .addHeader("Content-Type", "application/json")
            .setResponseCode(500)
        }
      )

      val getDatasetInfoRequest = ZosmfGetDatasetInfoRequest(mockHttpConnection, dsMaskTest)
      val getDatasetInfoResponse = datasetsApi.getDatasetInfo(getDatasetInfoRequest)
        as? ZosmfGetDatasetInfoResponse
        ?: fail("Should be instance of ${ZosmfGetDatasetInfoResponse::class.java.name}")

      assertSoftly {
        getDatasetInfoResponse.status.type shouldBe StatusType.ERROR
        getDatasetInfoResponse.status.text shouldContain "500"
      }
    }
  }
})