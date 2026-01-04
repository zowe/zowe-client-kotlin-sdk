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
import okhttp3.mockwebserver.MockResponse
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.mockHttpConnection
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.zosmfMockResponseDispatcher
import org.zowe.kotlinsdk.providers.zowe.KotestZoweProjectConfig.zoweAPIProvider
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfRenameDatasetRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfRenameDatasetRequestBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging.ZosmfRenameDatasetResponse

class ZosmfRenameDatasetTestSpec : ShouldSpec({
  val datasetsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, DatasetsAPI::class.java)

  afterSpec {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("renameDataset") {
    should("renameDataset successfully rename a data set") {
      val oldDsName = "RDTEST1"
      val newDsName = "NDTEST1"

      zosmfMockResponseDispatcher.injectResolver(
        "renameDataset_success",
        { it.requestLine.contains("/zosmf/restfiles/ds/$newDsName") },
        { MockResponse() }
      )

      val renameDatasetRequestBody = ZosmfRenameDatasetRequestBody(
        src = ZosmfRenameDatasetRequestBody.FromDataset(oldDsName)
      )
      val renameDatasetRequest = ZosmfRenameDatasetRequest(
        mockHttpConnection,
        newDsName,
        body = renameDatasetRequestBody
      )
      val renameDatasetResponse = datasetsApi.renameDataset(renameDatasetRequest)
        as? ZosmfRenameDatasetResponse
        ?: fail("Should be instance of ${ZosmfRenameDatasetResponse::class.java.name}")

      assertSoftly {
        renameDatasetResponse.status.type shouldBe StatusType.SUCCESS
      }
    }

    should("renameDataset successfully rename a data set member") {
      val oldDsName = "RDTEST2"
      val oldMemName = "RMTEST2"
      val newDsName = "NDTEST2"
      val newMemName = "NMTEST2"

      zosmfMockResponseDispatcher.injectResolver(
        "renameDataset_success_member",
        { it.requestLine.contains("/zosmf/restfiles/ds/$newDsName($newMemName)") },
        { MockResponse() }
      )

      val renameDatasetRequestBody = ZosmfRenameDatasetRequestBody(
        src = ZosmfRenameDatasetRequestBody.FromDataset(oldDsName, oldMemName)
      )
      val renameDatasetRequest = ZosmfRenameDatasetRequest(
        mockHttpConnection,
        newDsName,
        newMemName,
        body = renameDatasetRequestBody
      )
      val renameDatasetResponse = datasetsApi.renameDataset(renameDatasetRequest)
        as? ZosmfRenameDatasetResponse
        ?: fail("Should be instance of ${ZosmfRenameDatasetResponse::class.java.name}")

      assertSoftly {
        renameDatasetResponse.status.type shouldBe StatusType.SUCCESS
      }
    }

    should("renameDataset fail to rename a data set due to service error (new name too long)") {
      val oldDsName = "RDTEST3"
      val newDsName = "NDTEST3"

      zosmfMockResponseDispatcher.injectResolver(
        "renameDataset_fail_name_too_long",
        { it.requestLine.contains("/zosmf/restfiles/ds/$newDsName") },
        {
          MockResponse()
            .setBody(
              "{" +
                "\"category\":1," +
                "\"rc\":8," +
                "\"reason\":6," +
                "\"message\":\"data set rename failed\"," +
                "\"details\":[\"EDC5051I An error occurred when renaming a file. (errno2=0xC013006A)\"]" +
              "}"
            )
            .setResponseCode(500)
        }
      )

      val renameDatasetRequestBody = ZosmfRenameDatasetRequestBody(
        src = ZosmfRenameDatasetRequestBody.FromDataset(oldDsName)
      )
      val renameDatasetRequest = ZosmfRenameDatasetRequest(
        mockHttpConnection,
        newDsName,
        body = renameDatasetRequestBody
      )
      val renameDatasetResponse = datasetsApi.renameDataset(renameDatasetRequest)
        as? ZosmfRenameDatasetResponse
        ?: fail("Should be instance of ${ZosmfRenameDatasetResponse::class.java.name}")

      assertSoftly {
        renameDatasetResponse.status.type shouldBe StatusType.ERROR
      }
    }

    should("renameDataset fail due to 404 error") {
      val oldDsName = "RDTEST4"
      val newDsName = "NDTEST4"

      zosmfMockResponseDispatcher.injectResolver(
        "renameDataset_fail_not_found",
        { it.requestLine.contains("/zosmf/restfiles/ds/$newDsName") },
        { MockResponse().setResponseCode(404) }
      )

      val renameDatasetRequestBody = ZosmfRenameDatasetRequestBody(
        src = ZosmfRenameDatasetRequestBody.FromDataset(oldDsName)
      )
      val renameDatasetRequest = ZosmfRenameDatasetRequest(
        mockHttpConnection,
        newDsName,
        body = renameDatasetRequestBody
      )
      val renameDatasetResponse = datasetsApi.renameDataset(renameDatasetRequest)
        as? ZosmfRenameDatasetResponse
        ?: fail("Should be instance of ${ZosmfRenameDatasetResponse::class.java.name}")

      assertSoftly {
        renameDatasetResponse.status.type shouldBe StatusType.ERROR
      }
    }
  }
})