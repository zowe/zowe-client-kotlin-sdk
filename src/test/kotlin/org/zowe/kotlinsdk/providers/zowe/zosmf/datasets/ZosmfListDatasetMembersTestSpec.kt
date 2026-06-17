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
import org.zowe.kotlinsdk.core.datasets.AttributesLevel
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import io.kotest.provided.ProjectConfig.mockHttpConnection
import io.kotest.provided.ProjectConfig.zosmfMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions.ZosmfMemberItem
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfListDatasetMembersRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfListDatasetMembersResponse

class ZosmfListDatasetMembersTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, DatasetsAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("listDatasetMembers") {
    should("listDatasetMembers return the correct list of data set members with full attributes") {
      val dsName = "LMTEST1"

      zosmfMockResponseDispatcher.injectResolver(
        "listDatasetMembers_success",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName/member") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"items\": [" +
                  "{" +
                    "\"c4date\": \"2025/08/04\"," +
                    "\"cnorc\": 5136," +
                    "\"inorc\": 5136, " +
                    "\"m4date\": \"2025/08/04\"," +
                    "\"member\": \"LMMEM1\"," +
                    "\"mnorc\": 0," +
                    "\"mod\": 0," +
                    "\"msec\": \"02\"," +
                    "\"mtime\": \"13:50\"," +
                    "\"sclm\": \"N\"," +
                    "\"user\": \"LMUSR\"," +
                    "\"vers\": 1" +
                  "}," +
                  "{" +
                    "\"c4date\": \"2025/08/20\"," +
                    "\"cnorc\": 9761," +
                    "\"inorc\": 9761," +
                    "\"m4date\": \"2025/08/20\"," +
                    "\"member\": \"LMMEM2\"," +
                    "\"mnorc\": 0," +
                    "\"mod\": 0," +
                    "\"msec\": \"28\"," +
                    "\"mtime\": \"13:19\"," +
                    "\"sclm\": \"N\"," +
                    "\"user\": \"LMUSR\"," +
                    "\"vers\": 1" +
                  "}," +
                  "{" +
                    "\"c4date\": \"2025/08/01\"," +
                    "\"cnorc\": 1768," +
                    "\"inorc\": 1768," +
                    "\"m4date\": \"2025/08/04\"," +
                    "\"member\": \"LMMEM3\"," +
                    "\"mnorc\": 0," +
                    "\"mod\": 1," +
                    "\"msec\": \"23\"," +
                    "\"mtime\": \"08:21\"," +
                    "\"sclm\": \"N\"," +
                    "\"user\": \"LMUSR\"," +
                    "\"vers\": 1" +
                  "}," +
                  "{" +
                    "\"c4date\": \"2025/08/04\"," +
                    "\"cnorc\": 24586," +
                    "\"inorc\": 24586," +
                    "\"m4date\": \"2025/08/04\"," +
                    "\"member\": \"LMMEM4\"," +
                    "\"mnorc\": 0," +
                    "\"mod\": 0," +
                    "\"msec\": \"20\"," +
                    "\"mtime\": \"13:45\"," +
                    "\"sclm\": \"N\"," +
                    "\"user\": \"LMUSR\"," +
                    "\"vers\": 1" +
                  "}," +
                  "{" +
                    "\"c4date\": \"2025/09/19\"," +
                    "\"cnorc\": 4407," +
                    "\"inorc\": 4407," +
                    "\"m4date\": \"2025/09/23\"," +
                    "\"member\": \"LMMEM5\"," +
                    "\"mnorc\": 0," +
                    "\"mod\": 1," +
                    "\"msec\": \"41\"," +
                    "\"mtime\": \"15:47\"," +
                    "\"sclm\": \"N\"," +
                    "\"user\": \"LMUSR\"," +
                    "\"vers\": 1" +
                  "}," +
                  "{" +
                    "\"c4date\": \"2025/07/23\"," +
                    "\"cnorc\": 65535," +
                    "\"inorc\": 65535," +
                    "\"m4date\": \"2025/07/23\"," +
                    "\"member\": \"LMMEM6\"," +
                    "\"mnorc\": 0," +
                    "\"mod\": 0," +
                    "\"msec\": \"24\"," +
                    "\"mtime\": \"12:11\"," +
                    "\"sclm\": \"N\"," +
                    "\"user\": \"LMUSR\"," +
                    "\"vers\": 1" +
                  "}" +
                "]," +
                "\"returnedRows\": 6," +
                "\"JSONversion\": 1" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
        }
      )

      val listDatasetMembersRequest = ZosmfListDatasetMembersRequest(
        mockHttpConnection,
        dsName,
        returnTotalRows = false
      )
      val listDatasetMembersResponse = datasetsApi.listDatasetMembers(listDatasetMembersRequest)
        as? ZosmfListDatasetMembersResponse
        ?: fail("Should be instance of ${ZosmfListDatasetMembersResponse::class.java.name}")

      assertSoftly {
        listDatasetMembersResponse.status.type shouldBe StatusType.SUCCESS
        listDatasetMembersResponse.memberItems.size shouldBe 6
        listDatasetMembersResponse.memberItems[0].currentNumberOfRecords shouldBe 5136
        listDatasetMembersResponse.memberItems[1].modifiedIn shouldBe ZosmfMemberItem.ModifiedIn.ISPF
        listDatasetMembersResponse.memberItems[2].versionNumber shouldBe 1
        listDatasetMembersResponse.memberItems[3].numberOfChangedRecords shouldBe 0
        listDatasetMembersResponse.memberItems[4].modificationLevel shouldBe 1
        listDatasetMembersResponse.memberItems[5].secondsOfLastChangeTime shouldBe 24
      }
    }

    should("listDatasetMembers return the correct list of data set members with names only with total rows") {
      val dsName = "LMTEST2"

      zosmfMockResponseDispatcher.injectResolver(
        "listDatasetMembers_success_name",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName/member") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"items\": [" +
                  "{\"member\": \"LMMEM1\"}," +
                  "{\"member\": \"LMMEM2\"}," +
                  "{\"member\": \"LMMEM3\"}," +
                  "{\"member\": \"LMMEM4\"}" +
                "]," +
                "\"returnedRows\": 4," +
                "\"totalRows\": 4," +
                "\"JSONversion\": 1" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
        }
      )

      val listDatasetMembersRequest = ZosmfListDatasetMembersRequest(
        mockHttpConnection,
        dsName,
        attributesLevel = AttributesLevel.NAME
      )
      val listDatasetMembersResponse = datasetsApi.listDatasetMembers(listDatasetMembersRequest)
        as? ZosmfListDatasetMembersResponse
        ?: fail("Should be instance of ${ZosmfListDatasetMembersResponse::class.java.name}")

      assertSoftly {
        listDatasetMembersResponse.status.type shouldBe StatusType.SUCCESS
        listDatasetMembersResponse.memberItems.size shouldBe 4
        listDatasetMembersResponse.memberItems[0].memberName shouldBe "LMMEM1"
        listDatasetMembersResponse.memberItems[1].modifiedIn shouldBe null
        listDatasetMembersResponse.memberItems[2].modificationDate shouldBe null
        listDatasetMembersResponse.memberItems[3].creationDate shouldBe null
        listDatasetMembersResponse.totalRows shouldBe 4
      }
    }

    should("listDatasetMembers return an empty list when there is no data set members in the specified data set") {
      val dsName = "LMTEST3"

      zosmfMockResponseDispatcher.injectResolver(
        "listDatasetMembers_success_empty_list",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName/member") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"items\": []," +
                "\"returnedRows\": 0," +
                "\"JSONversion\": 1" +
              "}"
            )
            .addHeader("Content-Type", "application/json")
        }
      )

      val listDatasetMembersRequest = ZosmfListDatasetMembersRequest(
        mockHttpConnection,
        dsName,
        attributesLevel = AttributesLevel.NAME,
        returnTotalRows = false
      )
      val listDatasetMembersResponse = datasetsApi.listDatasetMembers(listDatasetMembersRequest)
        as? ZosmfListDatasetMembersResponse
        ?: fail("Should be instance of ${ZosmfListDatasetMembersResponse::class.java.name}")

      assertSoftly {
        listDatasetMembersResponse.status.type shouldBe StatusType.SUCCESS
        listDatasetMembersResponse.memberItems.size shouldBe 0
      }
    }

    should("listDatasetMembers return a client error when the data set does not exist") {
      val dsName = "LMTEST4"

      zosmfMockResponseDispatcher.injectResolver(
        "listDatasetMembers_error_not_found",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName/member") },
        { MockResponse().setResponseCode(404) }
      )

      val listDatasetMembersRequest = ZosmfListDatasetMembersRequest(
        mockHttpConnection,
        dsName,
        attributesLevel = AttributesLevel.NAME,
        returnTotalRows = false
      )
      val listDatasetMembersResponse = datasetsApi.listDatasetMembers(listDatasetMembersRequest)
        as? ZosmfListDatasetMembersResponse
        ?: fail("Should be instance of ${ZosmfListDatasetMembersResponse::class.java.name}")

      assertSoftly {
        listDatasetMembersResponse.status.type shouldBe StatusType.ERROR
        listDatasetMembersResponse.status.text shouldContain "404"
      }
    }

    should("listDatasetMembers return a server error when it is not possible to read the data set") {
      val dsName = "LMTEST5"

      zosmfMockResponseDispatcher.injectResolver(
        "listDatasetMembers_error_server_error",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName/member") },
        { MockResponse().setResponseCode(500) }
      )

      val listDatasetMembersRequest = ZosmfListDatasetMembersRequest(
        mockHttpConnection,
        dsName,
        attributesLevel = AttributesLevel.NAME,
        returnTotalRows = false
      )
      val listDatasetMembersResponse = datasetsApi.listDatasetMembers(listDatasetMembersRequest)
        as? ZosmfListDatasetMembersResponse
        ?: fail("Should be instance of ${ZosmfListDatasetMembersResponse::class.java.name}")

      assertSoftly {
        listDatasetMembersResponse.status.type shouldBe StatusType.ERROR
        listDatasetMembersResponse.status.text shouldContain "500"
      }
    }
  }
})