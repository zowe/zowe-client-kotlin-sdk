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

package org.zowe.kotlinsdk.providers.zowe

import org.zowe.kotlinsdk.core.APIProvider
import org.zowe.kotlinsdk.core.RequestRunner
import org.zowe.kotlinsdk.core.SupportedProtocol
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.core.files.api.FilesAPI
import org.zowe.kotlinsdk.core.info.api.InfoAPI
import org.zowe.kotlinsdk.core.jes.api.JesAPI
import org.zowe.kotlinsdk.providers.zowe.ssh.datasets.SshDatasetsAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.ZosmfDatasetsAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.ZosmfFilesAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.info.ZosmfInfoAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.jes.ZosmfJesAPI

/**
 * Zowe's official implementation of the z/OS-compatible API provider
 * @param requestRunners a list of [RequestRunner] instances that provide the API implementations
 */
class ZoweAPIProvider(
  private val requestRunners: List<RequestRunner>
) : APIProvider(
  DatasetsAPI::class.java to ZosmfDatasetsAPI(
    requestRunners.find { it.protocol == SupportedProtocol.HTTP } ?: throw Exception("HTTP is not supported")
  ),
  FilesAPI::class.java to ZosmfFilesAPI(
    requestRunners.find { it.protocol == SupportedProtocol.HTTP } ?: throw Exception("HTTP is not supported")
  ),
  JesAPI::class.java to ZosmfJesAPI(
    requestRunners.find { it.protocol == SupportedProtocol.HTTP } ?: throw Exception("HTTP is not supported")
  ),
  InfoAPI::class.java to ZosmfInfoAPI(
    requestRunners.find { it.protocol == SupportedProtocol.HTTP } ?: throw Exception("HTTP is not supported")
  ),
  DatasetsAPI::class.java to SshDatasetsAPI(
    requestRunners.find { it.protocol == SupportedProtocol.SSH } ?: throw Exception("SSH is not supported")
  )
) {
  override val supportedProtocols: List<SupportedProtocol>
    get() = requestRunners.map { it.protocol }
}
