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
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfDeleteDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfDeleteDatasetResponse

class ZosmfDeleteDatasetTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, DatasetsAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("deleteDataset") {
    should("deleteDataset successfully delete a data set") {
      val dsName = "DDTEST1"

      zosmfMockResponseDispatcher.injectResolver(
        "deleteDataset_success",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName") },
        { MockResponse().setResponseCode(204) }
      )

      val deleteDatasetRequest = ZosmfDeleteDatasetRequest(mockHttpConnection, dsName)
      val deleteDatasetResponse = datasetsApi.deleteDataset(deleteDatasetRequest)
        as? ZosmfDeleteDatasetResponse
        ?: fail("Should be instance of ${ZosmfDeleteDatasetResponse::class.java.name}")

      assertSoftly {
        deleteDatasetResponse.status.type shouldBe StatusType.SUCCESS
      }
    }

    should("deleteDataset successfully delete a data set member") {
      val dsName = "DDTEST2"
      val memName = "DDMEM2"

      zosmfMockResponseDispatcher.injectResolver(
        "deleteDataset_success_member",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName($memName)") },
        { MockResponse().setResponseCode(204) }
      )

      val deleteDatasetRequest = ZosmfDeleteDatasetRequest(mockHttpConnection, dsName, memberName = memName)
      val deleteDatasetResponse = datasetsApi.deleteDataset(deleteDatasetRequest)
        as? ZosmfDeleteDatasetResponse
        ?: fail("Should be instance of ${ZosmfDeleteDatasetResponse::class.java.name}")

      assertSoftly {
        deleteDatasetResponse.status.type shouldBe StatusType.SUCCESS
      }
    }

    should("deleteDataset successfully delete a data set on a specified volume") {
      val dsName = "DDTEST3"
      val volume = "TESTV"

      zosmfMockResponseDispatcher.injectResolver(
        "deleteDataset_success_volume",
        { it.requestLine.contains("/zosmf/restfiles/ds/-($volume)/$dsName") },
        { MockResponse().setResponseCode(204) }
      )

      val deleteDatasetRequest = ZosmfDeleteDatasetRequest(mockHttpConnection, dsName, volume)
      val deleteDatasetResponse = datasetsApi.deleteDataset(deleteDatasetRequest)
        as? ZosmfDeleteDatasetResponse
        ?: fail("Should be instance of ${ZosmfDeleteDatasetResponse::class.java.name}")

      assertSoftly {
        deleteDatasetResponse.status.type shouldBe StatusType.SUCCESS
      }
    }

    should("deleteDataset fail to delete a data set as it does not exist") {
      val dsName = "DDTEST4"

      zosmfMockResponseDispatcher.injectResolver(
        "deleteDataset_fail_not_exist",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\": 4," +
                "\"rc\": 8," +
                "\"reason\": 0," +
                "\"message\": \"LMERASE error\"," +
                "\"details\":[\"ISRZ002 Data set not cataloged - '$dsName' was not found in catalog.\"]" +
              "}"
            )
            .setResponseCode(404)
        }
      )

      val deleteDatasetRequest = ZosmfDeleteDatasetRequest(mockHttpConnection, dsName)
      val deleteDatasetResponse = datasetsApi.deleteDataset(deleteDatasetRequest)
        as? ZosmfDeleteDatasetResponse
        ?: fail("Should be instance of ${ZosmfDeleteDatasetResponse::class.java.name}")

      assertSoftly {
        deleteDatasetResponse.status.type shouldBe StatusType.ERROR
        deleteDatasetResponse.status.text shouldContain "404"
        deleteDatasetResponse.status.text shouldContain "LMERASE error"
      }
    }

    should("deleteDataset fail to delete a data set member as some user is editing it") {
      val dsName = "DDTEST5"
      val memName = "DDMEM5"

      zosmfMockResponseDispatcher.injectResolver(
        "deleteDataset_fail_busy_mem",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName($memName)") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\": 4," +
                "\"rc\": 12," +
                "\"reason\": 0," +
                "\"message\": \"LMMDEL error\"," +
                "\"details\":[\"ISRZ002 Member in use - Member is being updated by you or another user. Enter HELP for a list of users using the data set.\"]" +
              "}"
            )
            .setResponseCode(500)
        }
      )

      val deleteDatasetRequest = ZosmfDeleteDatasetRequest(mockHttpConnection, dsName, memberName = memName)
      val deleteDatasetResponse = datasetsApi.deleteDataset(deleteDatasetRequest)
        as? ZosmfDeleteDatasetResponse
        ?: fail("Should be instance of ${ZosmfDeleteDatasetResponse::class.java.name}")

      assertSoftly {
        deleteDatasetResponse.status.type shouldBe StatusType.ERROR
        deleteDatasetResponse.status.text shouldContain "500"
        deleteDatasetResponse.status.text shouldContain "LMMDEL error"
      }
    }

    should("deleteDataset fail to delete a data set as some user is editing it") {
      val dsName = "DDTEST6"

      zosmfMockResponseDispatcher.injectResolver(
        "deleteDataset_fail_busy",
        { it.requestLine.contains("/zosmf/restfiles/ds/$dsName") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\": 4," +
                "\"rc\": 8," +
                "\"reason\": 0," +
                "\"message\": \"LMERASE error\"," +
                "\"details\":[\"ISRZ002 Data set in use - Data set '$dsName' in use by another user, try later or enter HELP for a list of jobs and users allocated to '$dsName'.\"]" +
              "}"
            )
            .setResponseCode(500)
        }
      )

      val deleteDatasetRequest = ZosmfDeleteDatasetRequest(mockHttpConnection, dsName)
      val deleteDatasetResponse = datasetsApi.deleteDataset(deleteDatasetRequest)
        as? ZosmfDeleteDatasetResponse
        ?: fail("Should be instance of ${ZosmfDeleteDatasetResponse::class.java.name}")

      assertSoftly {
        deleteDatasetResponse.status.type shouldBe StatusType.ERROR
        deleteDatasetResponse.status.text shouldContain "500"
        deleteDatasetResponse.status.text shouldContain "LMERASE error"
      }
    }
  }
})