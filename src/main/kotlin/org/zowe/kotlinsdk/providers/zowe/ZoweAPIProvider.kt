/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe

import org.zowe.kotlinsdk.core.APIProvider
import org.zowe.kotlinsdk.core.RequestRunner
import org.zowe.kotlinsdk.core.SupportedProtocol
import org.zowe.kotlinsdk.core.WrapperType
import org.zowe.kotlinsdk.core.datasets.api.DatasetsAPI
import org.zowe.kotlinsdk.core.files.api.FilesAPI
import org.zowe.kotlinsdk.core.info.api.InfoAPI
import org.zowe.kotlinsdk.core.jes.api.JesAPI
import org.zowe.kotlinsdk.providers.zowe.openssh.datasets.SshDatasetsAPI
import org.zowe.kotlinsdk.providers.zowe.openssh.files.SshFilesAPI
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.SshJesAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.ZosmfDatasetsAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.files.ZosmfFilesAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.info.ZosmfInfoAPI
import org.zowe.kotlinsdk.providers.zowe.zosmf.jes.ZosmfJesAPI

// TODO: doc
/**
 * Zowe's official implementation of the z/OS-compatible API provider
 * @param requestRunners a list of [RequestRunner] instances that provide API implementations
 */
@OptIn(ZoweInternalAPI::class)
class ZoweAPIProvider(
  private val requestRunners: List<RequestRunner>
) : APIProvider(
  mapOf(
    WrapperType.ZOSMF to (requestRunners
      .find { it.protocol == SupportedProtocol.HTTP }
      ?.let {
        it as HttpRequestRunner
        mapOf(
          DatasetsAPI::class.java to ZosmfDatasetsAPI(it),
          FilesAPI::class.java to ZosmfFilesAPI(it),
          JesAPI::class.java to ZosmfJesAPI(it),
          InfoAPI::class.java to ZosmfInfoAPI(it)
        )
      } ?: mapOf()
    ),
    WrapperType.OPEN_SSH to (requestRunners
      .find { it.protocol == SupportedProtocol.SSH }
      ?.let {
        it as SshRequestRunner
        mapOf(
          DatasetsAPI::class.java to SshDatasetsAPI(it),
          FilesAPI::class.java to SshFilesAPI(it),
          JesAPI::class.java to SshJesAPI(it)
        )
      }
      ?: mapOf()
    )
  )
) {
  override val supportedWrapperTypes
    get() = availableApis.keys.toList()
  override val supportedProtocols: List<SupportedProtocol>
    get() = requestRunners.map { it.protocol }
}
