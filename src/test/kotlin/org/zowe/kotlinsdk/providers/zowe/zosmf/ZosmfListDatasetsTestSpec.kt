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
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import okhttp3.mockwebserver.MockResponse
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.MOCK_PASSWORD
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.MOCK_SERVER_HOST
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.MOCK_USERNAME
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.MOCK_ZOSMF_SERVER_PORT
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.zosmfMockResponseDispatcher
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.UserPassHttpConnection
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfListDatasetsRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfListDatasetsResponse

class ZosmfListDatasetsTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, DatasetsAPI::class.java)

  context("listDatasets") {
    should("listDatasets return the correct list of datasets") {
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
          MOCK_SERVER_HOST,
          MOCK_ZOSMF_SERVER_PORT,
          user=MOCK_USERNAME,
          password=MOCK_PASSWORD
        ),
        "TEST1.*"
      )
      val listDatasetsResponse = datasetsApi.listDatasets(listDatasetsRequest)

      assertSoftly {
        listDatasetsResponse is ZosmfListDatasetsResponse
        listDatasetsResponse.dsItems.size shouldBe 2
      }
    }
  }
})
