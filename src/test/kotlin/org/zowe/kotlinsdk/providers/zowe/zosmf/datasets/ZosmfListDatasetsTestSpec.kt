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
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfListDatasetsRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfListDatasetsResponse

class ZosmfListDatasetsTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, DatasetsAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("listDatasets") {
    should("listDatasets return the correct list of datasets") {
      val dsMaskTest = "LDTEST1"

      zosmfMockResponseDispatcher.injectResolver(
        "listDatasets_success",
        { it.requestLine.contains("/zosmf/restfiles/ds?dslevel=$dsMaskTest") },
        {
          MockResponse()
            .setBody(
              "{\n" +
              "  \"items\": [\n" +
              "    {\n" +
              "      \"dsname\": \"$dsMaskTest.TEST1\",\n" +
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
              "    },\n" +
              "    {\n" +
              "      \"dsname\": \"$dsMaskTest.TEST2\",\n" +
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
              "    }" +
              "  ],\n" +
              "  \"returnedRows\": 2,\n" +
              "  \"JSONversion\": 1\n" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
        }
      )

      val listDatasetsRequest = ZosmfListDatasetsRequest(mockHttpConnection, "$dsMaskTest.*")
      val listDatasetsResponse = datasetsApi.listDatasets(listDatasetsRequest) as? ZosmfListDatasetsResponse
        ?: fail("Should be instance of ${ZosmfListDatasetsResponse::class.java.name}")

      assertSoftly {
        listDatasetsResponse.status.type shouldBe StatusType.SUCCESS
        listDatasetsResponse.dsItems.size shouldBe 2
      }
    }

    should("listDatasets return an empty list when there is no data sets under the specified mask") {
      val dsMaskTest = "LDTEST2"

      zosmfMockResponseDispatcher.injectResolver(
        "listDatasets_success_empty",
        { it.requestLine.contains("/zosmf/restfiles/ds?dslevel=$dsMaskTest") },
        {
          MockResponse()
            .setBody("{\"items\": [],\"returnedRows\": 0,\"totalRows\": 0,\"JSONversion\": 1}")
            .addHeader("Content-Type", "application/json")
        }
      )

      val listDatasetsRequest = ZosmfListDatasetsRequest(mockHttpConnection, "$dsMaskTest.*")
      val listDatasetsResponse = datasetsApi.listDatasets(listDatasetsRequest) as? ZosmfListDatasetsResponse
        ?: fail("Should be instance of ${ZosmfListDatasetsResponse::class.java.name}")

      assertSoftly {
        listDatasetsResponse.status.type shouldBe StatusType.SUCCESS
        listDatasetsResponse.dsItems.size shouldBe 0
      }
    }

    should("listDatasets return an error when there are archived data sets on a non-MIGRAT volumes") {
      val dsMaskTest = "LDTEST3"

      zosmfMockResponseDispatcher.injectResolver(
        "listDatasets_error_arcive",
        { it.requestLine.contains("/zosmf/restfiles/ds?dslevel=$dsMaskTest") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\": 2," +
                "\"rc\": 4," +
                "\"reason\": 3," +
                "\"message\": \"ServletDispatcher failed - received TSO Prompt when expecting TsoServletResponse\"" +
                "\"details\":[" +
                  "\"ARC1020I DFSMSHSM IS RECALLING FROM TAPE DSN=TSARB.BACKUP.ASNA21.HDT0.D160323, YOU MAY CONTINUE THE RECALL IN THE BACKGROUND AND FREE YOUR TSO SESSION BY PRESSING THE ATTENTION KEY                    \",\n" +
                  "\"ARC1020I DFSMSHSM IS RECALLING FROM TAPE DSN=TSARB.BACKUP.VOLTAGE.HDT0.D150120, YOU MAY CONTINUE THE RECALL IN THE BACKGROUND AND FREE YOUR TSO SESSION BY PRESSING THE ATTENTION KEY                   \",\n" +
                  "\"ARC1020I DFSMSHSM IS RECALLING FROM TAPE DSN=TSARB.BACKUP.ZSECURE.HDT0.D150128, YOU MAY CONTINUE THE RECALL IN THE BACKGROUND AND FREE YOUR TSO SESSION BY PRESSING THE ATTENTION KEY                   \",\n" +
                  "\"IDI0034I Fault analysis skipped due to: EXCLUDE option specification (FAST)\",\n" +
                  "\"* ISPF Subtask abend *        \",\n" +
                  "\"IKJ56641I ISPSTART ENDED DUE TO ERROR+               \",\n" +
                  "\"READY \"" +
                "]" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
            .setResponseCode(500)
        }
      )

      val listDatasetsRequest = ZosmfListDatasetsRequest(mockHttpConnection, "$dsMaskTest.*")
      val listDatasetsResponse = datasetsApi.listDatasets(listDatasetsRequest) as? ZosmfListDatasetsResponse
        ?: fail("Should be instance of ${ZosmfListDatasetsResponse::class.java.name}")

      assertSoftly {
        listDatasetsResponse.status.type shouldBe StatusType.ERROR
        listDatasetsResponse.status.text shouldContain "500"
        listDatasetsResponse.status.text shouldContain "Category: 2"
        listDatasetsResponse.status.text shouldContain "RC: 4"
        listDatasetsResponse.status.text shouldContain "Reason: 3"
      }
    }
  }
})