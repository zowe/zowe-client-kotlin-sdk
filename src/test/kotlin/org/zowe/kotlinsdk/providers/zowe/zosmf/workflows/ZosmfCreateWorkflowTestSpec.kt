/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.workflows

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.provided.ProjectConfig.mockHttpConnection
import io.kotest.provided.ProjectConfig.zosmfMockResponseDispatcher
import io.kotest.provided.ProjectConfig.zoweAPIProvider
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.mockwebserver.RecordedRequest
import org.zowe.kotlinsdk.core.StatusType
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.workflows.api.WorkflowsAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.definitions.ZosmfWorkflowAccess
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfCreateWorkflowRequest
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfCreateWorkflowRequestBody
import org.zowe.kotlinsdk.providers.zowe.zosmf.workflows.messaging.ZosmfCreateWorkflowResponse

class ZosmfCreateWorkflowTestSpec : ShouldSpec({
  val workflowsApi = zoweAPIProvider.getApi(WrapperType.ZOSMF, WorkflowsAPI::class.java)
  val createPath = "$TEST_WORKFLOWS_PATH/workflows"

  afterEach {
    zosmfMockResponseDispatcher.clearResolvers()
  }

  context("createWorkflow") {
    should("createWorkflow send only the required properties when the optional ones are not provided") {
      var recordedRequest: RecordedRequest? = null
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "createWorkflow_success_required",
        { it.isWorkflowsRequest("POST", createPath) },
        {
          recordedRequest = it
          recordedBody = it.body.readUtf8()
          workflowsJsonResponse(201, readWorkflowsMock("createWorkflow"))
        }
      )

      val request = ZosmfCreateWorkflowRequest(
        mockHttpConnection,
        ZosmfCreateWorkflowRequestBody(
          workflowName = "ZOWE_WF_CONFIGURE",
          workflowDefinitionFile = "/u/ibmuser/workflow.xml",
          system = "PLEX1.SYS1",
          owner = "IBMUSER"
        )
      )
      val response = workflowsApi.createWorkflow(request) as? ZosmfCreateWorkflowResponse
        ?: fail("Should be instance of ${ZosmfCreateWorkflowResponse::class.java.name}")
      val body = recordedBody.toJsonObject()

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        response.status.text shouldContain "201"
        response.workflowKey shouldBe "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"
        response.workflowDescription shouldBe "Configure the Zowe instance"
        response.workflowId shouldBe "ZoweConfigure"
        response.workflowVersion shouldBe "1.0"
        response.vendor shouldBe "IBM"
        recordedRequest?.getHeader("Content-Type") shouldContain "application/json"
        body.keys shouldBe setOf("workflowName", "workflowDefinitionFile", "system", "owner")
        body["workflowName"]?.jsonPrimitive?.content shouldBe "ZOWE_WF_CONFIGURE"
        body["workflowDefinitionFile"]?.jsonPrimitive?.content shouldBe "/u/ibmuser/workflow.xml"
        body["system"]?.jsonPrimitive?.content shouldBe "PLEX1.SYS1"
        body["owner"]?.jsonPrimitive?.content shouldBe "IBMUSER"
        request.workflowName shouldBe "ZOWE_WF_CONFIGURE"
        request.workflowDefinitionFile shouldBe "/u/ibmuser/workflow.xml"
        request.system shouldBe "PLEX1.SYS1"
        request.owner shouldBe "IBMUSER"
      }
    }

    should("createWorkflow send all the properties with the z/OSMF names when all of them are provided") {
      var recordedBody: String? = null

      zosmfMockResponseDispatcher.injectResolver(
        "createWorkflow_success_all",
        { it.isWorkflowsRequest("POST", createPath) },
        {
          recordedBody = it.body.readUtf8()
          workflowsJsonResponse(201, readWorkflowsMock("createWorkflow"))
        }
      )

      val request = ZosmfCreateWorkflowRequest(
        mockHttpConnection,
        ZosmfCreateWorkflowRequestBody(
          workflowName = "ZOWE_WF_CONFIGURE",
          workflowDefinitionFile = "SYS1.WORKFLOW(ZOWE)",
          system = "PLEX1.SYS1",
          owner = "IBMUSER",
          workflowDefinitionFileSystem = "PLEX1.SYS2",
          variableInputFile = "/u/ibmuser/vars.properties",
          variables = listOf(
            ZosmfCreateWorkflowRequestBody.Variable("hlq", "IBMUSER.ZOWE"),
            ZosmfCreateWorkflowRequestBody.Variable("port", "7554")
          ),
          resolveGlobalConflictByUsing = ZosmfCreateWorkflowRequestBody.ResolveGlobalConflictByUsing.INPUT,
          workflowArchiveSafId = "ARCHUSR",
          comments = "Created by the Kotlin SDK",
          assignToOwner = false,
          accessType = ZosmfWorkflowAccess.PRIVATE,
          accountInfo = "ACCT#",
          jobStatement = "//IBMUSERA JOB (ACCT#)",
          deleteCompletedJobs = true,
          jobsOutputDirectory = "/u/ibmuser/jobs",
          autoDeleteOnCompletion = true,
          targetSystemUser = "REMOTEU",
          targetSystemPassword = "REMOTEP"
        )
      )
      val response = workflowsApi.createWorkflow(request) as ZosmfCreateWorkflowResponse
      val body = recordedBody.toJsonObject()
      val variables = body["variables"]?.jsonArray ?: fail("variables must be sent")

      assertSoftly {
        response.status.type shouldBe StatusType.SUCCESS
        body.keys shouldBe setOf(
          "workflowName", "workflowDefinitionFile", "system", "owner", "workflowDefinitionFileSystem",
          "variableInputFile", "variables", "resolveGlobalConflictByUsing", "workflowArchiveSAFID", "comments",
          "assignToOwner", "accessType", "accountInfo", "jobStatement", "deleteCompletedJobs",
          "jobsOutputDirectory", "autoDeleteOnCompletion", "targetSystemuid", "targetSystempwd"
        )
        body["workflowDefinitionFileSystem"]?.jsonPrimitive?.content shouldBe "PLEX1.SYS2"
        body["variableInputFile"]?.jsonPrimitive?.content shouldBe "/u/ibmuser/vars.properties"
        variables.size shouldBe 2
        variables[0].jsonObject["name"]?.jsonPrimitive?.content shouldBe "hlq"
        variables[0].jsonObject["value"]?.jsonPrimitive?.content shouldBe "IBMUSER.ZOWE"
        variables[1].jsonObject["name"]?.jsonPrimitive?.content shouldBe "port"
        variables[1].jsonObject["value"]?.jsonPrimitive?.content shouldBe "7554"
        body["resolveGlobalConflictByUsing"]?.jsonPrimitive?.content shouldBe "input"
        body["workflowArchiveSAFID"]?.jsonPrimitive?.content shouldBe "ARCHUSR"
        body["comments"]?.jsonPrimitive?.content shouldBe "Created by the Kotlin SDK"
        body["assignToOwner"]?.jsonPrimitive?.boolean shouldBe false
        body["accessType"]?.jsonPrimitive?.content shouldBe "Private"
        body["accountInfo"]?.jsonPrimitive?.content shouldBe "ACCT#"
        body["jobStatement"]?.jsonPrimitive?.content shouldBe "//IBMUSERA JOB (ACCT#)"
        body["deleteCompletedJobs"]?.jsonPrimitive?.boolean shouldBe true
        body["jobsOutputDirectory"]?.jsonPrimitive?.content shouldBe "/u/ibmuser/jobs"
        body["autoDeleteOnCompletion"]?.jsonPrimitive?.boolean shouldBe true
        body["targetSystemuid"]?.jsonPrimitive?.content shouldBe "REMOTEU"
        body["targetSystempwd"]?.jsonPrimitive?.content shouldBe "REMOTEP"
      }
    }

    should("createWorkflow return an error status with the z/OSMF message when the workflow name is not unique") {
      zosmfMockResponseDispatcher.injectResolver(
        "createWorkflow_fail_400",
        { it.isWorkflowsRequest("POST", createPath) },
        { workflowsErrorResponse(400, "IZUWF0105E", "The workflow name is not unique.") }
      )

      val request = ZosmfCreateWorkflowRequest(
        mockHttpConnection,
        ZosmfCreateWorkflowRequestBody("DUPLICATE", "/u/ibmuser/workflow.xml", "PLEX1.SYS1", "IBMUSER")
      )
      val response = workflowsApi.createWorkflow(request) as ZosmfCreateWorkflowResponse

      assertSoftly {
        response.status.shouldBeWorkflowsError(400, "IZUWF0105E")
        response.workflowKey shouldBe ""
      }
    }

    should("createWorkflow request body not expose the target system password in its string representation") {
      val body = ZosmfCreateWorkflowRequestBody(
        "ZOWE_WF_CONFIGURE",
        "/u/ibmuser/workflow.xml",
        "PLEX1.SYS1",
        "IBMUSER",
        targetSystemPassword = "SECRETPWD"
      )

      body.toString() shouldNotContain "SECRETPWD"
    }
  }
})
