// Copyright © 2020 IBA Group, a.s. All rights reserved. Use of this source code is governed by Eclipse Public License – v 2.0 that can be found at: https://www.eclipse.org/legal/epl-2.0/

package org.zowe.kotlinsdk.zowe.zosjobs

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.zowe.kotlinsdk.Job
import org.zowe.kotlinsdk.RequestVersion
import org.zowe.kotlinsdk.zowe.*
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import org.zowe.kotlinsdk.zowe.client.sdk.zosjobs.DeleteJobs
import okhttp3.OkHttpClient
import org.junit.jupiter.api.*
import java.net.InetSocketAddress
import java.net.Proxy

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeleteJobsTest {

  lateinit var mockServer: MockWebServer
  lateinit var proxyClient: OkHttpClient
  lateinit var responseDispatcher: MockResponseDispatcher

  @BeforeAll
  fun createMockServer() {
    mockServer = MockWebServer()
    responseDispatcher = MockResponseDispatcher()
    mockServer.dispatcher = responseDispatcher
    mockServer.start()
    val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(mockServer.hostName, mockServer.port))
    proxyClient = OkHttpClient.Builder().proxy(proxy).build()
  }

  @AfterAll
  fun stopMockServer() {
    mockServer.shutdown()
  }

  @Test
  fun deleteJob() {
    val connection = ZOSConnection(TEST_HOST, TEST_PORT, TEST_USER, TEST_PASSWORD, "http")
    val deleteJobs = DeleteJobs(connection, proxyClient)
    responseDispatcher.injectEndpoint(
      {
        it?.requestLine?.matches(Regex("DELETE http://.*/zosmf/restjobs/jobs/TESTJOBW/JOB00085 HTTP/.*")) == true &&
            it.getHeader("X-IBM-Job-Modify-Version") == RequestVersion.SYNCHRONOUS.value
      },
      {
        MockResponse().setBody(responseDispatcher.readMockJson("deleteJobs") ?: "").setResponseCode(200)
      }
    )
    val response = deleteJobs.deleteJob("TESTJOBW", "JOB00085", RequestVersion.SYNCHRONOUS)
    Assertions.assertEquals("0", response.status)

    responseDispatcher.clearValidationList()
  }

  @Test
  fun deleteJobForJob() {
    val connection = ZOSConnection(TEST_HOST, TEST_PORT, TEST_USER, TEST_PASSWORD, "http")
    val deleteJobs = DeleteJobs(connection, proxyClient)
    responseDispatcher.injectEndpoint(
      {
        it?.requestLine?.matches(Regex("DELETE http://.*/zosmf/restjobs/jobs/TESTJOBW/JOB00085 HTTP/.*")) == true &&
            it.getHeader("X-IBM-Job-Modify-Version") == RequestVersion.SYNCHRONOUS.value
      },
      {
        MockResponse().setBody(responseDispatcher.readMockJson("deleteJobs") ?: "").setResponseCode(200)
      }
    )
    val response = deleteJobs.deleteJobForJob(
      Job(
        jobId = "JOB00085", jobName = "TESTJOBW", owner = "", type = Job.JobType.JOB,
        url = "", filesUrl = "", phase = 0, phaseName = ""
      ),
      RequestVersion.SYNCHRONOUS
    )
    Assertions.assertEquals("0", response.status)
  }
}
